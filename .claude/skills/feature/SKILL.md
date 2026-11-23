---
name: feature
description: Run the full plan → implement → test → review workflow for a spec in docs/specs.
---
Implement the spec at $ARGUMENTS using this workflow. Follow it exactly.

1. PLAN: Read the spec and the files it names. Write a numbered plan:
   files to change, tests to add (and the line each test protects).
   STOP and wait for my approval.
2. BRANCH: create feat/<spec-slug> from main.
3. IMPLEMENT: make the changes. Stay inside the spec's scope.
4. TEST: use the test-runner subagent. If tests fail, fix and re-run.
5. REVIEW: use the android-reviewer subagent with the spec path and
   `git diff main...HEAD` plus uncommitted changes.
6. LOOP: fix every BLOCKER and MAJOR, then repeat steps 4–5.
   Max 2 review rounds. If still not APPROVE, stop and report.
   7. FINISH: don't commit. Show: plan vs. done, test result,
      final reviewer verdict, open MINOR findings, git diff --stat.