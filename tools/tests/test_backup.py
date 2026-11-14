import gzip
import io
import json
from pathlib import Path
import tarfile
import tempfile
import unittest
from unittest.mock import patch
from tools import backup


class BackupTests(unittest.TestCase):
    def sql(self, path, complete=True):
        with gzip.open(path, 'wb') as stream:
            stream.write(b'CREATE TABLE `sample` (id int);\n')
            if complete:
                stream.write(b'-- Dump completed on 2026-11-14\n')

    def archive(self, path, name='images/photo.bin', kind=tarfile.REGTYPE):
        with tarfile.open(path, 'w:gz') as stream:
            info = tarfile.TarInfo(name)
            info.type = kind
            info.size = 3 if kind == tarfile.REGTYPE else 0
            stream.addfile(info, io.BytesIO(b'abc') if info.size else None)

    def test_truncated_sql_and_corrupt_gzip_fail(self):
        with tempfile.TemporaryDirectory() as root:
            path = Path(root) / 'db.sql.gz'
            self.sql(path)
            self.assertEqual(backup.sql_info(path), ['sample'])
            self.sql(path, False)
            with self.assertRaises(ValueError):
                backup.sql_info(path)
            path.write_bytes(b'not gzip')
            with self.assertRaises(OSError):
                backup.sql_info(path)

    def test_archive_traversal_links_and_devices_rejected(self):
        with tempfile.TemporaryDirectory() as root:
            path = Path(root) / 'objects.tar.gz'
            for name, kind in [('../escape', tarfile.REGTYPE), ('/absolute', tarfile.REGTYPE),
                               ('link', tarfile.SYMTYPE), ('device', tarfile.CHRTYPE)]:
                self.archive(path, name, kind)
                with self.assertRaises(ValueError):
                    backup.archive_info(path)
            self.archive(path)
            target = Path(root) / 'restore'
            target.mkdir()
            self.assertEqual(backup.archive_info(path, target), {'files': 1, 'bytes': 3})
            self.assertEqual((target / 'images/photo.bin').read_bytes(), b'abc')

    def test_hash_tamper_fails_before_restore(self):
        with tempfile.TemporaryDirectory() as root:
            folder = Path(root) / '20261114T000000Z-12345678'
            folder.mkdir()
            path = folder / 'db.sql.gz'
            self.sql(path)
            backup.write_json(folder / 'manifest.json', {'owner': backup.OWNER, 'artifacts': [
                {'kind': 'mysql', 'file': path.name, 'sha256': backup.digest(path), 'tables': ['sample']} ]})
            backup.verify(folder)
            path.write_bytes(b'changed')
            with self.assertRaisesRegex(ValueError, 'hash_mismatch'):
                backup.verify(folder)

    def test_failed_backup_never_prunes_or_changes_latest(self):
        with tempfile.TemporaryDirectory() as root:
            root = Path(root).resolve()
            backup.write_json(root / 'latest.json', {'directory': 'previous'})
            config = {'databases': [{'container': 'database', 'database': 'blog'}], 'minimum_free_bytes': 0}
            with patch.object(backup, 'docker', side_effect=RuntimeError('private sql')), patch.object(backup, 'prune') as prune:
                with self.assertRaises(RuntimeError):
                    backup.backup(config, root)
                prune.assert_not_called()
            self.assertEqual(json.loads((root / 'latest.json').read_text())['directory'], 'previous')
            self.assertEqual(sorted(p.name for p in root.iterdir()), ['latest.json'])

    def test_retention_ignores_unowned_and_symlink_backups(self):
        with tempfile.TemporaryDirectory() as root:
            root = Path(root).resolve()
            for n in range(4):
                folder = root / f'20261114T00000{n}Z-12345678'
                folder.mkdir()
                backup.write_json(folder / 'manifest.json', {'owner': backup.OWNER, 'artifacts': [{}]})
            (root / '20261114T000000Z-12345678' / 'link').symlink_to(root / 'latest.json')
            other = root / '20261114T000004Z-12345678'
            other.mkdir()
            backup.write_json(other / 'manifest.json', {'owner': 'someone-else'})
            backup.prune(root, 2)
            self.assertTrue(other.exists())
            self.assertTrue((root / '20261114T000000Z-12345678').exists())
            self.assertFalse((root / '20261114T000001Z-12345678').exists())

    def test_invalid_identifiers_and_symlink_roots_rejected(self):
        for value in ['x; rm', '../foo', '-flag', 'x/y']:
            with self.assertRaises(ValueError):
                backup.ident(value)
        with tempfile.TemporaryDirectory() as root:
            path = Path(root) / 'link'
            path.symlink_to(Path(root), target_is_directory=True)
            with self.assertRaises(ValueError):
                backup.private_dir(path)

    def test_restore_insufficient_memory_never_creates_container(self):
        with patch.object(backup, 'verify', return_value={}), patch.object(backup, 'docker', return_value=b'MemAvailable: 10 kB') as docker:
            with self.assertRaisesRegex(RuntimeError, 'insufficient_restore_memory'):
                backup.restore_drill({'memory_container': 'edge'}, Path('/unused'))
            self.assertEqual(docker.call_count, 1)

    def test_restore_failure_cleans_only_new_isolated_resources(self):
        calls = []
        def docker(cfg, *args, **kwargs):
            calls.append(args)
            if args[:2] == ('exec', 'edge'):
                return b'MemAvailable: 2097152 kB'
            if args[0] == 'run':
                raise RuntimeError('start failure')
            return b''
        with patch.object(backup, 'verify', return_value={'artifacts': []}), patch.object(backup, 'docker', side_effect=docker), patch.object(backup.subprocess, 'run') as cleanup:
            with self.assertRaises(RuntimeError):
                backup.restore_drill({'memory_container': 'edge', 'mysql_image': 'image'}, Path('/unused'))
        create = next(args for args in calls if args[:2] == ('volume', 'create'))
        remove = next(args for args in calls if args[:2] == ('volume', 'rm'))
        self.assertEqual(create[-1], remove[-1])
        self.assertTrue(remove[-1].startswith('snowk-restore-'))
        start = next(args for args in calls if args[0] == 'run')
        self.assertEqual(start[start.index('--network') + 1], 'none')
        self.assertNotIn('-p', start)
        self.assertTrue(cleanup.call_args.args[0][-1].startswith('snowk-restore-'))
