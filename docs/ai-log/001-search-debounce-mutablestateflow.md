# 001 — Search debounce refactor to MutableStateFlow
- Date: 2026-11-23
- Spec: docs/specs/001-search-debounce-mutablestateflow.md
- Branch / commit: main / see `git log --grep "spec 001"`
- Agents used: main session (Claude Code); spec drafted with /spec

## What the AI did
- Wrote 5 SearchViewModel tests (before the spec), all green
- Drafted spec 001 with /spec
- Refactored the pipeline input from snapshotFlow to MutableStateFlow
- Added 2 timing tests: "waits for 400ms of silence", "burst of typing"

## Checks
- Tests: 7 run / 0 failed
- Mutation checks: removed `.debounce(400.milliseconds)` → 2 new timing tests failed (the 5 original tests stayed green)
- Review rounds: 0 (no reviewer subagent yet), final verdict: UNREVIEWED

## Problems found
- Human: the original 5 tests could not fail without debounce — input arrived before the pipeline started (stateIn WhileSubscribed + StandardTestDispatcher). Proven by deleting debounce: still 5/5 green. Fixed by spec 001 tests 6–7.
- Human: first spec draft claimed test 5 guarded debounce and kept advanceUntilIdle — contradicting its own testability goal. Fixed in spec review.
- Agent: noticed Gradle UP-TO-DATE (0 tests actually ran) and forced a rerun, per the CLAUDE.md rule.

## Human decisions
- Required a mutation check in the spec's acceptance criteria
- Rejected the spec's "keep advanceUntilIdle, don't add tests" decision
