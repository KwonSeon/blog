"""Private local backups and isolated restore drills. No live restore operation."""
import argparse
import datetime as dt
import fcntl
import gzip
import hashlib
import json
import os
from pathlib import Path, PurePosixPath
import re
import shutil
import subprocess
import tarfile
import tempfile
import time
import uuid

OWNER = 'snowk-backup-v1'
RUN = re.compile(r'\d{8}T\d{6}Z-[a-f0-9]{8}')
NAME = re.compile(r'[A-Za-z0-9][A-Za-z0-9_.-]{0,100}')
DB = re.compile(r'[A-Za-z][A-Za-z0-9_]{0,63}')


def ident(value, pattern=NAME):
    if not isinstance(value, str) or not pattern.fullmatch(value):
        raise ValueError('invalid_identifier')
    return value


def private_dir(path):
    path = Path(path).absolute()
    if any(p.is_symlink() for p in [path, *path.parents]):
        raise ValueError('symlink_directory')
    path.mkdir(parents=True, exist_ok=True, mode=0o700)
    path.chmod(0o700)
    return path


def write_json(path, data):
    temporary = path.with_name(path.name + '.' + uuid.uuid4().hex + '.tmp')
    with temporary.open('x') as stream:
        json.dump(data, stream, indent=2)
        stream.flush()
        os.fsync(stream.fileno())
    temporary.chmod(0o600)
    temporary.replace(path)


def digest(path):
    with path.open('rb') as stream:
        result = hashlib.sha256()
        while chunk := stream.read(1024 * 1024):
            result.update(chunk)
        return result.hexdigest()


def run(command, *, timeout=600, stdin=None, stdout=subprocess.PIPE):
    # Never expose subprocess stderr: SQL and credentials may appear there.
    result = subprocess.run(command, stdin=stdin, stdout=stdout,
                            stderr=subprocess.DEVNULL, timeout=timeout)
    if result.returncode:
        raise RuntimeError('command_failed')
    return result.stdout


def docker(cfg, *args, **kwargs):
    return run([cfg.get('docker', '/opt/homebrew/bin/docker'), *args], **kwargs)


def sql_info(path):
    tables, tail = set(), b''
    with gzip.open(path, 'rb') as stream:
        for line in stream:
            match = re.match(rb'CREATE TABLE `([^`]+)`', line)
            if match:
                tables.add(match[1].decode('utf-8'))
            tail = (tail + line)[-4096:]
    if not tables or b'-- Dump completed on ' not in tail:
        raise ValueError('incomplete_sql_dump')
    return sorted(tables)


def archive_info(path, destination=None):
    """Read every byte; reject special entries and traversal before extraction."""
    with gzip.open(path, 'rb') as compressed:
        while compressed.read(1024 * 1024):
            pass  # Consume gzip trailer/CRC as tar readers may stop at padding.
    count, total, names = 0, 0, set()
    with tarfile.open(path, 'r:gz') as archive:
        for member in archive:
            name = PurePosixPath(member.name)
            if name.is_absolute() or '..' in name.parts or not (member.isfile() or member.isdir()):
                raise ValueError('unsafe_archive_entry')
            if name.as_posix() in names:
                raise ValueError('duplicate_archive_entry')
            names.add(name.as_posix())
            if not member.isfile():
                continue
            source = archive.extractfile(member)
            expected = hashlib.sha256()
            output = None
            if destination:
                target = destination.joinpath(*name.parts)
                target.parent.mkdir(parents=True, exist_ok=True, mode=0o700)
                output = target.open('xb')
            try:
                while chunk := source.read(1024 * 1024):
                    expected.update(chunk)
                    if output:
                        output.write(chunk)
                if output:
                    output.close()
                    if digest(target) != expected.hexdigest():
                        raise ValueError('restore_hash_mismatch')
            finally:
                if output:
                    output.close()
                source.close()
            count += 1
            total += member.size
    if count == 0:
        raise ValueError('empty_archive')
    return {'files': count, 'bytes': total}


def verify(folder):
    if folder.is_symlink() or not RUN.fullmatch(folder.name):
        raise ValueError('invalid_backup_directory')
    manifest = json.loads((folder / 'manifest.json').read_text())
    if manifest.get('owner') != OWNER or not manifest.get('artifacts'):
        raise ValueError('invalid_manifest')
    for item in manifest['artifacts']:
        name = ident(item['file'])
        path = folder / name
        if path.is_symlink() or not path.is_file() or digest(path) != item['sha256']:
            raise ValueError('backup_hash_mismatch')
        if item['kind'] == 'mysql':
            if sql_info(path) != item['tables']:
                raise ValueError('backup_table_mismatch')
        elif item['kind'] == 'archive':
            if archive_info(path) != item['contents']:
                raise ValueError('backup_archive_mismatch')
        else:
            raise ValueError('unknown_artifact')
    return manifest


def prune(root, keep):
    if not 2 <= keep <= 365:
        raise ValueError('invalid_retention')
    complete = []
    for folder in sorted(root.iterdir(), reverse=True):
        if folder.is_symlink() or not folder.is_dir() or not RUN.fullmatch(folder.name):
            continue
        try:
            manifest = json.loads((folder / 'manifest.json').read_text())
            if manifest.get('owner') == OWNER and manifest.get('artifacts'):
                complete.append(folder)
        except (OSError, ValueError):
            continue
    for folder in complete[keep:]:
        # Never follow symlinks, including a replaced manifest or artifact.
        if any(path.is_symlink() for path in folder.rglob('*')):
            continue
        shutil.rmtree(folder)


def backup(cfg, root):
    if shutil.disk_usage(root).free < cfg.get('minimum_free_bytes', 10 * 1024**3):
        raise RuntimeError('insufficient_disk_space')
    stamp = dt.datetime.now(dt.timezone.utc).strftime('%Y%m%dT%H%M%SZ')
    name = stamp + '-' + uuid.uuid4().hex[:8]
    staging = private_dir(root / ('.partial-' + name))
    artifacts = []
    try:
        for target in cfg['databases']:
            container, database = ident(target['container']), ident(target['database'], DB)
            raw, compressed = staging / 'dump.tmp.sql', staging / (database + '.sql.gz')
            with raw.open('xb') as output:
                if target.get('disable_fulltext_stopwords', False):
                    output.write(b'SET SESSION innodb_ft_enable_stopword=OFF;\n')
                    output.flush()
                docker(cfg, 'exec', container, 'sh', '-c',
                       'MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec mysqldump -uroot '
                       '--single-transaction --quick --no-tablespaces --set-gtid-purged=OFF '
                       '--routines --events --triggers --hex-blob "$1"', 'backup', database,
                       stdout=output)
            with raw.open('rb') as source, gzip.open(compressed, 'wb') as output:
                shutil.copyfileobj(source, output, 1024 * 1024)
            raw.unlink()
            artifacts.append({'kind': 'mysql', 'file': compressed.name, 'database': database,
                              'tables': sql_info(compressed), 'sha256': digest(compressed)})
        media = cfg.get('archive')
        if media:
            helper = 'snowk-backup-' + uuid.uuid4().hex
            archive = staging / 'objects.tar.gz'
            try:
                with archive.open('xb') as output:
                    docker(cfg, 'run', '--rm', '--name', helper, '--network', 'none', '--read-only',
                           '--cap-drop', 'ALL', '--memory', '128m', '--cpus', '0.5',
                           '--mount', 'type=volume,src=' + ident(media['volume']) + ',dst=/source,readonly',
                           '--entrypoint', 'tar', cfg['archive_image'], '-czf', '-', '-C', '/source', '.',
                           stdout=output)
            finally:
                subprocess.run([cfg.get('docker', '/opt/homebrew/bin/docker'), 'rm', '-f', helper],
                               stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, timeout=30)
            artifacts.append({'kind': 'archive', 'file': archive.name,
                              'contents': archive_info(archive), 'sha256': digest(archive)})
        manifest = {'owner': OWNER, 'createdAt': stamp, 'artifacts': artifacts,
                    'crossServiceAtomic': False}
        write_json(staging / 'manifest.json', manifest)
        final = root / name
        staging.rename(final)
        verify(final)
        write_json(root / 'latest.json', {'directory': name})
        prune(root, cfg.get('keep', 14))
        return final
    finally:
        if staging.exists():
            shutil.rmtree(staging)


def restore_drill(cfg, folder):
    manifest = verify(folder)
    mem = docker(cfg, 'exec', ident(cfg['memory_container']), 'cat', '/proc/meminfo', timeout=10)
    available = int(re.search(rb'MemAvailable:\s+(\d+)', mem)[1]) * 1024
    if available < 1280 * 1024**2:
        raise RuntimeError('insufficient_restore_memory')
    name = 'snowk-restore-' + uuid.uuid4().hex
    volume = name + '-data'
    results = []
    created = False
    try:
        docker(cfg, 'volume', 'create', '--label', 'snowk.backup.owner=' + OWNER, volume, timeout=30)
        created = True
        docker(cfg, 'run', '-d', '--name', name, '--label', 'snowk.backup.owner=' + OWNER,
               '--network', 'none', '--memory', '768m', '--cpus', '1',
               '-e', 'MYSQL_ALLOW_EMPTY_PASSWORD=yes', '--mount', 'type=volume,src=' + volume + ',dst=/var/lib/mysql',
               cfg['mysql_image'], '--innodb-buffer-pool-size=64M', '--performance-schema=OFF',
               '--skip-log-bin', timeout=60)
        for attempt in range(60):
            try:
                docker(cfg, 'exec', name, 'sh', '-c',
                       'test "$(cat /proc/1/comm)" = mysqld && mysqladmin -uroot ping', timeout=5)
                break
            except RuntimeError:
                time.sleep(2)
        else:
            raise RuntimeError('restore_database_not_ready')
        for item in manifest['artifacts']:
            if item['kind'] == 'mysql':
                database = ident(item['database'], DB)
                docker(cfg, 'exec', name, 'mysql', '-uroot', '-e', 'CREATE DATABASE `' + database + '`')
                with tempfile.TemporaryFile() as raw:
                    with gzip.open(folder / item['file'], 'rb') as source:
                        shutil.copyfileobj(source, raw, 1024 * 1024)
                    raw.seek(0)
                    docker(cfg, 'exec', '-i', name, 'mysql', '-uroot', database, stdin=raw)
                actual = docker(cfg, 'exec', name, 'mysql', '-uroot', '-N', '-B', '-e',
                                "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA='" +
                                database + "' AND TABLE_TYPE='BASE TABLE' ORDER BY TABLE_NAME").decode().splitlines()
                if sorted(actual) != sorted(item['tables']):
                    raise ValueError('restored_tables_mismatch')
                for table in actual:
                    if '`' in table:
                        raise ValueError('invalid_table_name')
                    checked = docker(cfg, 'exec', name, 'mysql', '-uroot', '-N', '-B', database,
                                     '-e', 'CHECK TABLE `' + table + '`').decode().splitlines()
                    if not checked or any(not row.endswith('\tstatus\tOK') for row in checked):
                        raise ValueError('restored_table_check_failed')
                results.append({'kind': 'mysql', 'database': database, 'tables': len(actual), 'ok': True})
            else:
                with tempfile.TemporaryDirectory(dir=folder.parent, prefix='.restore-') as target:
                    contents = archive_info(folder / item['file'], Path(target))
                    results.append({'kind': 'archive', **contents, 'ok': True})
        report = {'owner': OWNER, 'verifiedAt': dt.datetime.now(dt.timezone.utc).isoformat(),
                  'directory': folder.name, 'isolated': True, 'checks': results}
        write_json(folder / 'restore.json', report)
        return report
    finally:
        # Only names freshly generated for this drill, never configured live containers/volumes.
        subprocess.run([cfg.get('docker', '/opt/homebrew/bin/docker'), 'rm', '-f', name],
                       stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, timeout=30)
        if created:
            docker(cfg, 'volume', 'rm', volume, timeout=30)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--config', required=True)
    parser.add_argument('action', choices=['backup', 'verify', 'restore-drill'])
    args = parser.parse_args()
    os.umask(0o077)
    cfg = json.loads(Path(args.config).read_text())
    # Images must be immutable local digests; no implicitly upgraded helper images.
    for key in ['mysql_image', 'archive_image']:
        if not re.fullmatch(r'sha256:[a-f0-9]{64}', cfg[key]):
            raise ValueError('image_digest_required')
    root = private_dir(cfg['output_dir'])
    with (root / '.lock').open('a') as lock:
        fcntl.flock(lock, fcntl.LOCK_EX | fcntl.LOCK_NB)
        if args.action == 'backup':
            folder = backup(cfg, root)
        else:
            latest = json.loads((root / 'latest.json').read_text())['directory']
            if not RUN.fullmatch(latest):
                raise ValueError('invalid_latest')
            folder = root / latest
            verify(folder) if args.action == 'verify' else restore_drill(cfg, folder)
        print(json.dumps({'ok': True, 'action': args.action, 'directory': folder.name}))


if __name__ == '__main__':
    try:
        main()
    except Exception as error:
        # Exception messages may contain SQL/credentials; emit only a fixed class name.
        print(json.dumps({'ok': False, 'errorType': type(error).__name__}))
        raise SystemExit(1) from None
