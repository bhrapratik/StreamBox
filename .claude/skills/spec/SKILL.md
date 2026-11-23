---
name: spec
description: Draft a feature spec in docs/specs before implementation. Use when planning a new feature or change.
---
Draft a spec for: $ARGUMENTS

Existing specs: !`ls docs/specs 2>/dev/null`

Create docs/specs/NNN-<slug>.md (next number) with sections:
Goal, Context (real file paths — read them), Requirements, Constraints,
Acceptance (each item = one test, and how it could fail), Verify (exact gradle command), Out of scope.
Ask me up to 3 questions for anything ambiguous. Don't write code.