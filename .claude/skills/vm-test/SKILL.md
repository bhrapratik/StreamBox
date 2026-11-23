---
name: vm-test
description: Write unit tests for a :core ViewModel. Use when asked to test a ViewModel or its UiState/Flow behavior.
---
Write tests for $ARGUMENTS in :core following CLAUDE.md test conventions.

Existing tests: !`find core/src/test -name "*Test.kt"`

Steps:
1. Read the ViewModel, its UiState and repository interface. Do not edit production code.
   If it's untestable as written, stop and explain why.
2. Write a hand-written fake repository. Cover: initial state, success, empty, error,
   and any time-based logic with advanceTimeBy (never only advanceUntilIdle).
3. Run: ./gradlew :core:testDebugUnitTest --tests "*$ARGUMENTS*Test" — confirm count > 0.
4. Mutation check: for the most important test, break the production line it guards,
   run it, confirm it fails, revert. Report which line you broke.
5. Finish with: tests added, mutation result, git diff --stat.