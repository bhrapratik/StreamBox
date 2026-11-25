---
name: ai-log
description: Write an AI work log entry for the task just finished in this session. Use before committing AI-assisted work.
---
Write an entry in docs/ai-log/ for the task just completed in this session.
File name: docs/ai-log/<spec-number or next number>-<slug>.md. Use this exact template,
filled only with facts from this session. If something didn't happen, write "none".
Don't invent numbers.

# <number> — <title>
- Date: <today>
- Spec: <path or "none">
- Branch / commit: <branch> / <short sha or "uncommitted">
- Agents used: <main session, subagents by name>

## What the AI did
<3–5 bullets: plan, files changed, tests added>

## Checks
- Tests: <run / failed>
- Mutation checks: <what was broken → which test failed>
- Review rounds: <n>, final verdict: <APPROVE / CHANGES_REQUESTED / UNREVIEWED>

## Problems found
<each: who found it (reviewer / human / CI / hook), what, how fixed>

## Human decisions
<where the human approved, corrected or overrode the agent>

Then append one row to docs/ai-log/metrics.md:
| <number> | <title> | <review rounds> | <blockers caught by reviewer> | <human corrections> | <mutation checks passed>/<run> | <CI first-run result> |

Finally, show me the entry. Don't commit.
