import json
from pathlib import Path
import tempfile
import unittest
from unittest.mock import MagicMock, patch
from tools import monitor


class MonitorTests(unittest.TestCase):
    def response(self, status=200, body=b'{"status":"UP"}', url='https://example.test/health'):
        response = MagicMock()
        response.__enter__.return_value = response
        response.status, response.url = status, url
        response.read.return_value = body
        return response

    def test_health_checks_expected_json_without_recording_body(self):
        with patch.object(monitor.urllib.request, 'urlopen', return_value=self.response()):
            result = monitor.probe({'name': 'api', 'url': 'https://example.test/health', 'json_contains': {'status': 'UP'}})
        self.assertTrue(result['ok'])
        self.assertNotIn('UP', json.dumps(result))

    def test_wrong_body_redirect_and_oversized_body_fail(self):
        for response in [self.response(body=b'{"status":"DOWN"}'),
                         self.response(url='https://example.test/login'), self.response(body=b'x' * 65537)]:
            with patch.object(monitor.urllib.request, 'urlopen', return_value=response):
                self.assertFalse(monitor.probe({'name': 'api', 'url': 'https://example.test/health', 'json_contains': {'status': 'UP'}})['ok'])

    def test_exception_details_never_enter_record(self):
        with patch.object(monitor.urllib.request, 'urlopen', side_effect=RuntimeError('private-token')):
            result = monitor.probe({'name': 'api', 'url': 'https://example.test/health'})
        self.assertFalse(result['ok'])
        self.assertNotIn('private-token', json.dumps(result))

    def test_container_oom_restart_and_missing_are_detected(self):
        info = [{'Name': '/api', 'State': {'Running': True, 'OOMKilled': True}, 'RestartCount': 2}]
        result = MagicMock(stdout=json.dumps(info))
        with patch.object(monitor.subprocess, 'run', return_value=result):
            checks = monitor.docker_checks({}, ['api', 'missing'], {'checks': [{'name': 'api', 'restartCount': 1}]})
        self.assertTrue(all(not c['ok'] for c in checks))
        self.assertTrue(checks[0]['restartedSinceLastCheck'])

    def test_transition_events_recovery_and_rotation(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            bad = {'observedAt': 'now', 'ok': False, 'checks': [{'name': 'api', 'ok': False}]}
            monitor.save_report(root, bad, {})
            monitor.save_report(root, bad, bad)
            good = {'observedAt': 'later', 'ok': True, 'checks': [{'name': 'api', 'ok': True}]}
            monitor.save_report(root, good, bad)
            self.assertEqual(len((root / 'events.jsonl').read_text().splitlines()), 2)
            self.assertTrue(json.loads((root / 'status.json').read_text())['ok'])
            self.assertEqual((root / 'status.json').stat().st_mode & 0o777, 0o600)
            monitor.append_bounded(root / 'history.jsonl', {}, limit=1)
            self.assertTrue((root / 'history.previous.jsonl').exists())
