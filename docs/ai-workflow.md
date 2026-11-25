# AI-assisted development workflow

StreamBox is built with coding agents (Claude Code) working inside a workflow that
makes their output checkable. This page explains how it works and what it has caught.

## The flow

```
spec ──▶ PLAN ──▶ ✋ human approves ──▶ IMPLEMENT ──▶ TEST ──▶ REVIEW ──▶ fix (max 2 rounds) ──▶ ✋ human commits ──▶ CI
         main      plan checkpoint      main          test-     android-                           /ai-log            gates
         session                        session       runner    reviewer
```

One command runs it: `/feature docs/specs/<spec>.md`.

## Building blocks

| Piece | Where | Job |
|---|---|---|
| Project rules | `CLAUDE.md`, `tv/CLAUDE.md`, `automotive/CLAUDE.md` | Commands, module boundaries, conventions, definition of done. Every rule traces back to a real agent mistake. |
| Specs | `docs/specs/` | Goal, constraints, acceptance criteria. Each test names the production line it protects. |
| Skills | `.claude/skills/` | Repeatable procedures: `/spec`, `/vm-test`, `/feature`, `/review`, `/ai-log` |
| Subagents | `.claude/agents/` | `android-reviewer` (read-only, fresh context, independent review) and `test-runner` (keeps Gradle noise out of the main context) |
| Hooks | `.claude/hooks/`, `.claude/settings.json` | Enforced guardrails: block force-push, push to main, `rm -rf`, reading `local.properties`; flag `@Ignore`/`@Disabled` in tests |
| CI | `.github/workflows/ci.yml` | Unit tests on all modules, a "tests actually ran" count gate, lint, agent-config validation |
| Traceability | `docs/ai-log/` | One entry per AI-assisted change, plus `metrics.md` |

## Principles

1. **Green is a claim, not proof.** Tests must be able to fail: specs name the line each
   test protects, and a mutation check (delete that line, watch the test go red) is part of acceptance.
2. **The author doesn't grade its own work.** Review runs in a separate subagent with no
   memory of how the code was written.
3. **Requests vs. enforcement.** `CLAUDE.md` asks; hooks and CI enforce. Anything costly to
   get wrong (secrets, force-push, silenced tests) is enforced.
4. **Humans own two decisions:** the plan and the commit.
5. **Everything is logged** so the process can be audited and measured.

## What it caught

| Caught by | Problem |
|---|---|
| Human + mutation check | 5 agent-written search tests passed with `debounce()` deleted — input arrived before the pipeline started. Replaced with virtual-time tests that fail without debounce. |
| Human (spec review) | `tryEmit` on a no-buffer `MutableSharedFlow` silently drops values while collected → spec requires `extraBufferCapacity = 1`. |
| Agent (implementation) | An acceptance criterion was impossible (retry only acts in `Error`; a retry moves state to `Loading`) → escalated instead of faking a test. |
| Reviewer subagent | A fake repository read its result lazily, masking the exact regression its test existed to catch. |
| Reviewer subagent | Planted bug: swallowed `CancellationException` — invisible to all 7 tests. |
| Agent + `CLAUDE.md` rule | Gradle reported UP-TO-DATE with 0 tests run → forced a real run. |
| CI | `:ui-mobile` had a test but no JUnit dependency — never seen locally because only `:core` was tested. |

See [docs/ai-log/metrics.md](ai-log/metrics.md) for per-task numbers.

## Next

- detekt with the coroutines rule set (swallowed cancellation is better caught statically than by tests)
- GitHub MCP server so agents can read CI logs and PRs directly
- AI review as a non-blocking PR check
