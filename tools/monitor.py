#!/usr/bin/env python3
"""Read-only health monitoring. Runtime targets and records stay outside Git."""
import argparse
import concurrent.futures
import datetime as dt
import fcntl
import json
import os
from pathlib import Path
import shutil
import subprocess
import tempfile
import time
import urllib.error
import urllib.request


def atomic_json(path, value):
    with tempfile.NamedTemporaryFile(mode='w', dir=path.parent, delete=False) as handle:
        temporary = Path(handle.name)
        json.dump(value, handle, ensure_ascii=False, indent=2)
        handle.flush()
        os.fsync(handle.fileno())
    temporary.chmod(0o600)
    temporary.replace(path)


def append_bounded(path, value, limit=10 * 1024 * 1024):
    if path.exists() and path.stat().st_size >= limit:
        path.replace(path.with_suffix('.previous.jsonl'))
    with path.open('a') as handle:
        handle.write(json.dumps(value, ensure_ascii=False) + '\n')
    path.chmod(0o600)


def probe(endpoint):
    start = time.monotonic()
    result = {'name': endpoint['name'], 'kind': 'http', 'ok': False}
    try:
        request = urllib.request.Request(endpoint['url'], headers={
            'User-Agent': 's-nowk-health-monitor', **endpoint.get('headers', {})})
        with urllib.request.urlopen(request, timeout=8) as response:
            body = response.read(65537)
            result['status'] = response.status
            result['ok'] = response.status == endpoint.get('status', 200)
            if response.url != endpoint['url']:
                result.update(ok=False, error='unexpected_redirect')
            if 'json_contains' in endpoint:
                if len(body) > 65536:
                    raise ValueError('response_too_large')
                data = json.loads(body)
                result['ok'] = result['ok'] and all(
                    data.get(key) == value for key, value in endpoint['json_contains'].items())
    except urllib.error.HTTPError as error:
        result.update(status=error.code, error='http_error')
    except Exception as error:
        # Never persist response bodies, exception messages, credentials or request headers.
        result.update(ok=False, error=type(error).__name__)
    result['milliseconds'] = round((time.monotonic() - start) * 1000)
    return result


def docker_checks(config, names, previous):
    binary = config.get('docker', 'docker')
    checks = []
    previous_checks = {item['name']: item for item in previous.get('checks', [])}
    try:
        process = subprocess.run([binary, 'inspect', *names], capture_output=True,
                                 text=True, timeout=10)
        inventory = {item['Name'].lstrip('/'): item for item in json.loads(process.stdout)}
        for name in names:
            info = inventory.get(name)
            if info is None:
                checks.append({'name': name, 'kind': 'container', 'ok': False, 'error': 'missing'})
                continue
            state = info['State']
            healthy = state.get('Health', {}).get('Status', 'none')
            restarts = info.get('RestartCount', 0)
            changed = restarts > previous_checks.get(name, {}).get('restartCount', restarts)
            checks.append({'name': name, 'kind': 'container', 'ok': bool(state.get('Running'))
                           and not state.get('OOMKilled') and healthy not in ('unhealthy', 'starting')
                           and not changed, 'health': healthy, 'restartCount': restarts,
                           'restartedSinceLastCheck': changed, 'oomKilled': bool(state.get('OOMKilled'))})
    except Exception as error:
        checks.append({'name': 'container-inventory', 'kind': 'container', 'ok': False,
                       'error': type(error).__name__})
    if config.get('memory_container'):
        try:
            process = subprocess.run([binary, 'exec', config['memory_container'], 'cat', '/proc/meminfo'],
                                     capture_output=True, text=True, timeout=5, check=True)
            fields = dict(line.split(':', 1) for line in process.stdout.splitlines() if ':' in line)
            available = int(fields['MemAvailable'].split()[0]) // 1024
            checks.append({'name': 'docker-memory', 'kind': 'resource', 'ok': available >= 512,
                           'availableMiB': available})
        except Exception as error:
            checks.append({'name': 'docker-memory', 'kind': 'resource', 'ok': False,
                           'error': type(error).__name__})
    return checks


def collect(config, previous):
    endpoints = config.get('endpoints', [])
    if not 1 <= len(endpoints) <= 12:
        raise ValueError('Configure between one and twelve endpoints')
    names = list(config.get('containers', []))
    if len(names) > 12:
        raise ValueError('Too many configured containers')
    release = None
    if config.get('release_file'):
        state = json.loads(Path(config['release_file']).read_text())
        release = state['sha']
        names.extend(state[key] for key in ('api', 'web') if state.get(key))
    with concurrent.futures.ThreadPoolExecutor(max_workers=4) as pool:
        checks = list(pool.map(probe, endpoints))
    if names:
        checks.extend(docker_checks(config, list(dict.fromkeys(names)), previous))
    free = shutil.disk_usage(config['output']).free
    checks.append({'name': 'backup-disk', 'kind': 'resource',
                   'ok': free >= config.get('minimum_free_bytes', 5 * 1024**3),
                   'freeGiB': round(free / 1024**3, 2)})
    return {'observedAt': dt.datetime.now(dt.timezone.utc).isoformat(),
            'release': release, 'ok': all(item['ok'] for item in checks), 'checks': checks}


def save_report(root, report, previous):
    failures = sorted(item['name'] for item in report['checks'] if not item['ok'])
    old_failures = sorted(item['name'] for item in previous.get('checks', []) if not item['ok'])
    if failures != old_failures:
        append_bounded(root / 'events.jsonl', {'observedAt': report['observedAt'],
                       'event': 'recovered' if not failures else 'degraded', 'checks': failures}, 1024**2)
    append_bounded(root / 'history.jsonl', report)
    atomic_json(root / 'status.json', report)


def main():
    os.umask(0o077)
    parser = argparse.ArgumentParser()
    parser.add_argument('--config', type=Path, required=True)
    args = parser.parse_args()
    config = json.loads(args.config.read_text())
    root = Path(config['output'])
    root.mkdir(mode=0o700, parents=True, exist_ok=True)
    root.chmod(0o700)
    with (root / 'monitor.lock').open('a') as lock:
        try:
            fcntl.flock(lock, fcntl.LOCK_EX | fcntl.LOCK_NB)
        except BlockingIOError:
            return 0
        previous = json.loads((root / 'status.json').read_text()) if (root / 'status.json').exists() else {}
        report = collect(config, previous)
        save_report(root, report, previous)
        print(json.dumps({'observedAt': report['observedAt'], 'ok': report['ok'],
                          'failedChecks': [c['name'] for c in report['checks'] if not c['ok']]}))
        return 0 if report['ok'] else 1


if __name__ == '__main__':
    try:
        raise SystemExit(main())
    except Exception as error:
        print(json.dumps({'ok': False, 'error': type(error).__name__}))
        raise SystemExit(1)
