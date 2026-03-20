#!/usr/bin/env python3
from __future__ import annotations

import argparse
import shutil
import sys
from dataclasses import dataclass
from datetime import date
from pathlib import Path
from typing import Any

import yaml


@dataclass
class SyncResult:
    artifact_id: str
    mode: str
    source: Path
    target: Path


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Archive an OpenSpec change using repo-local archive.syncArtifacts rules "
            "from the configured schema."
        )
    )
    parser.add_argument("change_name", help="Active change name under openspec/changes/")
    parser.add_argument(
        "--repo-root",
        default=str(Path(__file__).resolve().parents[1]),
        help="Repository root. Defaults to this script's parent repo.",
    )
    parser.add_argument(
        "--skip-sync",
        action="store_true",
        help="Archive without syncing repo-level docs even if the schema defines sync rules.",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Show what would happen without copying files or moving the change directory.",
    )
    return parser.parse_args()


def load_yaml(path: Path) -> dict[str, Any]:
    if not path.exists():
        raise FileNotFoundError(f"YAML file not found: {path}")
    data = yaml.safe_load(path.read_text(encoding="utf-8"))
    if not isinstance(data, dict):
        raise ValueError(f"Expected mapping at root of {path}")
    return data


def resolve_base_path(base: str, repo_root: Path, change_dir: Path) -> Path:
    if base == "repo":
        return repo_root
    if base == "change":
        return change_dir
    raise ValueError(f"Unsupported base '{base}'. Expected 'repo' or 'change'.")


def resolve_single_source(path_pattern: str, base_path: Path, mode: str) -> Path:
    matches = sorted(base_path.glob(path_pattern))
    if not matches:
        raise FileNotFoundError(f"No source file matched pattern '{path_pattern}' under {base_path}")

    if mode == "overwrite":
        if len(matches) != 1:
            raise ValueError(
                f"Mode 'overwrite' expects exactly one source file for pattern '{path_pattern}', "
                f"found {len(matches)}."
            )
        return matches[0]

    if mode == "copy_latest":
        return max(matches, key=lambda path: path.stat().st_mtime)

    raise ValueError(f"Unsupported sync mode '{mode}'.")


def resolve_target_path(source: Path, destination_root: Path, destination_path: str) -> Path:
    raw_target = destination_root / destination_path
    if destination_path.endswith("/"):
        return raw_target / source.name
    if raw_target.exists() and raw_target.is_dir():
        return raw_target / source.name
    if raw_target.suffix == "":
        return raw_target / source.name
    return raw_target


def sync_artifacts(
    repo_root: Path,
    change_dir: Path,
    sync_artifacts_config: list[dict[str, Any]],
    dry_run: bool,
) -> list[SyncResult]:
    sync_results: list[SyncResult] = []

    for entry in sync_artifacts_config:
        artifact_id = str(entry["id"])
        mode = str(entry["mode"])

        source_config = entry["from"]
        target_config = entry["to"]

        source_base_path = resolve_base_path(str(source_config["base"]), repo_root, change_dir)
        target_base_path = resolve_base_path(str(target_config["base"]), repo_root, change_dir)

        source = resolve_single_source(str(source_config["path"]), source_base_path, mode)
        target = resolve_target_path(source, target_base_path, str(target_config["path"]))

        sync_results.append(SyncResult(artifact_id=artifact_id, mode=mode, source=source, target=target))

        if dry_run:
            continue

        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(source, target)

    return sync_results


def archive_change(repo_root: Path, change_name: str, dry_run: bool) -> tuple[Path, Path]:
    change_dir = repo_root / "openspec" / "changes" / change_name
    if not change_dir.exists():
        raise FileNotFoundError(f"Active change directory not found: {change_dir}")

    archive_root = repo_root / "openspec" / "changes" / "archive"
    archive_root.mkdir(parents=True, exist_ok=True)

    archive_target = archive_root / f"{date.today().isoformat()}-{change_name}"
    if archive_target.exists():
        raise FileExistsError(f"Archive target already exists: {archive_target}")

    if not dry_run:
        shutil.move(str(change_dir), str(archive_target))

    return change_dir, archive_target


def main() -> int:
    args = parse_args()
    repo_root = Path(args.repo_root).resolve()
    change_name = args.change_name
    change_dir = repo_root / "openspec" / "changes" / change_name

    change_config = load_yaml(change_dir / ".openspec.yaml")
    schema_name = str(change_config["schema"])
    schema_path = repo_root / "openspec" / "schemas" / schema_name / "schema.yaml"
    schema_config = load_yaml(schema_path)

    archive_config = schema_config.get("archive", {})
    sync_enabled = bool(archive_config.get("syncOnArchive", False)) and not args.skip_sync
    sync_artifacts_config = archive_config.get("syncArtifacts", [])

    sync_results: list[SyncResult] = []
    if sync_enabled:
        if not isinstance(sync_artifacts_config, list):
            raise ValueError("schema.archive.syncArtifacts must be a list.")
        sync_results = sync_artifacts(repo_root, change_dir, sync_artifacts_config, args.dry_run)

    source, target = archive_change(repo_root, change_name, args.dry_run)

    print(f"change={change_name}")
    print(f"schema={schema_name}")
    print(f"dry_run={str(args.dry_run).lower()}")
    print(f"sync_enabled={str(sync_enabled).lower()}")
    for result in sync_results:
        print(
            "sync:"
            f" artifact={result.artifact_id}"
            f" mode={result.mode}"
            f" source={result.source.relative_to(repo_root)}"
            f" target={result.target.relative_to(repo_root)}"
        )
    print(f"archive_source={source.relative_to(repo_root)}")
    print(f"archive_target={target.relative_to(repo_root)}")
    if args.dry_run:
        print("result=dry-run-only")
    else:
        print("result=archived")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except Exception as error:  # pragma: no cover - CLI guard
        print(f"error={error}", file=sys.stderr)
        raise SystemExit(1)
