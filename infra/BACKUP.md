# Local backups and isolated restore drills

`tools/backup.py` is the maintained command (Python 3.9+ and Docker). Runtime JSON, SQL, archives and logs stay outside Git. Historical shell entry points now require `S_NOWK_BACKUP_CONFIG` and invoke the complete configured backup set.

```sh
python3 tools/backup.py --config /private/path/config.json backup
python3 tools/backup.py --config /private/path/config.json verify
python3 tools/backup.py --config /private/path/config.json restore-drill
```

| Configuration | Meaning |
| --- | --- |
| `output_dir` | Absolute private output directory outside source control |
| `databases` | Objects with existing `container` and `database` identifiers |
| `archive.volume` | Optional existing object-storage Docker volume |
| `mysql_image`, `archive_image` | Pinned local `sha256:` image IDs |
| `memory_container` | Running container used to read Docker VM available memory |
| `keep` | Completed sets retained, default 14, minimum 2 |
| `minimum_free_bytes` | Disk headroom, default 10 GiB |
| `docker` | Executable path, defaults to Homebrew installation |

The private Mac configuration covers Blog, shared production Identity and Media databases and the current Media MinIO volume. Container-local credentials use `MYSQL_PWD`, never command-line password values. Dumps include routines/events/triggers and use single-transaction/quick. Avoid DDL migrations during dumps: concurrent DDL is not protected by single-transaction ([MySQL documentation](https://dev.mysql.com/doc/refman/8.4/en/mysqldump.html)).

Mode700 directories, mode600 files, process locking, private staging, SQL completion checks, gzip CRC, archive path/type validation and SHA-256 manifests protect backup creation. Failure does not advance `latest.json` or prune previous sets. Retention removes only owned completed sets after a new backup succeeds, skipping symlinks and unrelated folders. Crash leftovers named `.partial-*` are not valid backups.

## Restore drill

Hashes are validated first. The drill creates a random temporary MySQL container/volume with **no network or published ports**, imports SQL, compares base-table inventory and runs SQL `CHECK TABLE`. It needs 1280 MiB available Docker VM memory and limits the container to 768 MiB/one CPU. The empty temporary root password applies only to this isolated container. Live/dev databases are never restore targets. Generated resources are cleaned in `finally`; after abrupt host termination, inspect only resources labelled `snowk.backup.owner=snowk-backup-v1` before manual cleanup.

Archive extraction rejects traversal, links and special entries, writes a private temporary directory and compares every extracted file hash. This verifies extraction, **not a running MinIO service recovery**. Private `restore.json` records results without rows or personal identifiers.

## Schedule and limitations

Private LaunchAgents run daily backup and weekly restore drills after code promotion. Check last exit status, manifest timestamps and `restore.json`; local records do not deliver external alerts.

The backups are on the same Mac, so machine loss is not covered. File permissions are not encryption. Offsite encrypted backup is not configured. Retention can temporarily preserve personal information deleted from live services.

Separate SQL snapshots and an online object-volume archive are **not an atomic cross-service snapshot or point-in-time recovery**. Concurrent MinIO writes can require reconciliation. Real recovery needs an explicitly selected recovery point, isolated database and MinIO service checks, media-reference reconciliation and reviewed cutover. No live overwrite command is provided. Infrastructure configuration and secrets need a separate private recovery plan and are not embedded in dumps.
