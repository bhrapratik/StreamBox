# AI workflow metrics

| # | Task | Review rounds | Blockers caught by reviewer | Human corrections | Mutation checks passed | CI first run |
|---|------|---------------|-----------------------------|-------------------|------------------------|--------------|
| 001 | Search debounce refactor | 0 | 0 | 2 | 1/1 | n/a (no CI yet) |
| 002 | Search retry() | 2 | 1 | 2 | 2/2 | red (:ui-mobile junit) |
| 003 | Guardrail hooks | 0 | 0 | 0 | n/a | n/a |

How to read it: falling "human corrections" and "CI first run: green" over time mean the
specs, CLAUDE.md and guardrails are working. "Blockers caught by reviewer" > 0 shows the
review stage earns its cost.
