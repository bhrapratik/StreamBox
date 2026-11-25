# 002 — SearchViewModel.retry()
- Date: 2026-11-23
- Spec: docs/specs/002-search-retry.md
- Branch / commit: feat/002-search-retry / b6e2d3d
- Agents used: main session via /feature; test-runner; android-reviewer

## What the AI did
- Planned against spec 002 and waited for approval
- Added retry(), a buffered MutableSharedFlow retry path merged into flatMapLatest, and lastSearchedQuery
- Added 5 tests (12 total)

## Checks
- Tests: 12 run / 0 failed (CI: 17 across all modules)
- Mutation checks: removed the Error guard → "no-ops outside Error" failed; flatMapLatest → flatMapMerge → "retry cancelled by typed query" failed
- Review rounds: 2 (max), final verdict: CHANGES_REQUESTED, last fix self-verified → UNREVIEWED

## Problems found
- Human (spec review): a no-buffer MutableSharedFlow drops tryEmit values while collected → spec now requires extraBufferCapacity = 1
- Agent: acceptance item 5 ("second retry cancels the first") cannot happen (retry only acts in Error; a retry puts the state in Loading) → escalated to the human, retargeted
- Reviewer (round 2, BLOCKER): the fake repository read its result lazily, so the flatMapMerge regression was masked → fixed by capturing the result at call start
- Human: the agent fixed the final BLOCKER and verified it itself, bending the 2-round rule → /feature skill now marks this UNREVIEWED
- CI: :ui-mobile had a test but no junit dependency → placeholder test removed

## Human decisions
- Approved the plan; chose the retarget for item 5
- Tightened the /feature skill after the self-verification gap
