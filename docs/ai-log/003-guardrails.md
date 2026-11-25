# 003 — Guardrail hooks (bash + test silencing)
- Date: 2026-11-25
- Spec: none
- Branch / commit: feat/002-search-retry / 03a6a61
- Agents used: main session (Claude Code)

## What the AI did
- Added `.claude/settings.json` wiring a PreToolUse hook on Bash and a PostToolUse hook on Edit|Write
- Added `.claude/hooks/guard-bash.sh`: blocks force-push, push-to-main, recursive `rm -rf`, and reads/edits of `local.properties`
- Added `.claude/hooks/guard-tests.sh`: after editing a `*Test.kt` file, blocks (exit 2) if `@Ignore`/`@Disabled` is present, forcing a fix instead of a silenced test
- Added `permissions.deny` entries in settings.json for `Read`/`Edit` on `local.properties`

## Checks
- Tests: none (hook/config change, no Kotlin test suite touched)
- Mutation checks: none
- Review rounds: 0, final verdict: UNREVIEWED

## Problems found
- none recorded this session

## Human decisions
- none recorded this session
