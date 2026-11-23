# 001 — Search debounce refactor to MutableStateFlow

## Goal

`SearchViewModel` currently drives its debounce/search pipeline off a Compose
`mutableStateOf` via `snapshotFlow`. Per CLAUDE.md ("ViewModel inputs:
MutableStateFlow, not Compose mutableStateOf (testability)"), replace the
pipeline's input with a `MutableStateFlow<String>` while leaving the
ViewModel's public surface (`query`, `onQueryChange`) and all observable
behavior unchanged.

## Context

- `core/src/main/java/com/streambox/core/ui/search/SearchViewModel.kt:31-53`
  — `query` is a `mutableStateOf("")`; `state` is built from
  `snapshotFlow { query.trim() }.debounce(400.milliseconds).distinctUntilChanged().flatMapLatest { ... }`.
- `core/src/main/java/com/streambox/core/ui/search/SearchUiState.kt` — sealed
  interface (`Idle`, `Loading`, `Results`, `Empty`, `Error`); unchanged.
- `core/src/test/java/com/streambox/core/ui/search/SearchViewModelTest.kt` —
  5 existing tests using `StandardTestDispatcher` + Turbine, all driving the
  ViewModel via `onQueryChange` and `advanceUntilIdle()`.
- `ui-mobile/src/main/java/com/streambox/uimobile/ui/search/SearchScreen.kt:58-66`
  — reads `viewModel.query` (plain `String`) for `OutlinedTextField`'s
  `value`, calls `viewModel.onQueryChange(it)` on change. This call site is
  why the public surface must not change shape.
- `tv/src/main/java/com/streambox/tv/ui/search/TvSearchScreen.kt` — not yet
  read in detail; flagged because `:tv` depends on `:core` directly and may
  also bind to `query`/`onQueryChange`. Confirm before merging that it
  doesn't need changes either.

## Requirements

1. Add a private `MutableStateFlow<String>` (e.g. `queryFlow`) that becomes
   the actual source for the debounce → distinctUntilChanged → flatMapLatest
   → search pipeline, replacing `snapshotFlow { query.trim() }`.
2. Keep `query` as a Compose-observable `String` (whatever backing field is
   simplest — it only needs to feed `SearchScreen.kt`'s `TextField` value and
   clear button) and keep `onQueryChange(value: String)` as the single
   setter used by the UI.
3. `onQueryChange` must update both `query` (so the TextField echoes
   keystrokes immediately) and `queryFlow.value` (so the pipeline sees the
   new value), synchronously and in the same call.
4. Preserve exact current behavior: 400ms debounce, `.trim()` before
   `distinctUntilChanged()`, `q.length < 2` → `Idle`, `Loading` → `Results`/
   `Empty`/`Error` sequencing, and the `CancellationException` rethrow in
   `search()`.
5. `SearchViewModel`'s constructor signature and public members
   (`query`, `onQueryChange`, `state`) keep their current types, so
   `SearchScreen.kt` (and `TvSearchScreen.kt` if applicable) compile with
   zero changes.

## Constraints

- No new dependencies — `MutableStateFlow` is already available via
  `kotlinx-coroutines-core`, already a `:core` dependency. Don't touch
  `gradle/libs.versions.toml` or `build.gradle.kts`.
- `snapshotFlow`/Compose runtime imports may be removed from
  `SearchViewModel.kt` if no longer used, but don't remove `mutableStateOf`
  if it's still the backing store for the public `query` property — that's a
  judgment call for whoever implements this; either is fine as long as
  Requirement 3 holds.
- This is a refactor, not a behavior change: no new `SearchUiState` cases,
  no debounce-duration change, no threshold change.
- One commit for this task, per CLAUDE.md.

## Acceptance

All 5 existing tests in `SearchViewModelTest.kt` must pass **unmodified**
(no edits to the test file's assertions or timing style — `advanceUntilIdle`
stays, per explicit decision not to introduce `advanceTimeBy` in this task):

1. `short query stays Idle without hitting the repository` — fails if the
   `q.length < 2` → `Idle` short-circuit breaks, or if the repository gets
   called for a 1-character query.
2. `typing a query emits Loading then Results` — fails if the
   `MutableStateFlow` pipeline drops the `Loading` emission or doesn't reach
   `search()`.
3. `no results emits Empty with the query` — fails if the `Empty(query)`
   branch stops carrying the right query string.
4. `repository failure emits Error` — fails if exception handling in
   `search()` regresses (e.g. a non-`CancellationException` escapes instead
   of mapping to `SearchUiState.Error`).
5. `rapid typing only searches the debounced query` — the most important
   regression check for this refactor: fails if `MutableStateFlow` doesn't
   coalesce rapid `onQueryChange` calls the same way `snapshotFlow` did
   (e.g. if intermediate values like `"d"`, `"du"`, `"dun"` leak through to
   the repository instead of only `"dune"`).

One new test to cover the invariant this refactor introduces (two fields —
`query` and `queryFlow` — that must stay in sync):

6. `onQueryChange updates query immediately, before debounce fires` —
   assert `viewModel.query == "d"` right after calling
   `onQueryChange("d")`, with **no** `advanceUntilIdle()`/`advanceTimeBy()`
   call first. Fails if `query` is only updated via the debounced flow
   (making the TextField appear to lag behind typing) instead of
   synchronously in `onQueryChange`.

## Verify

```
./gradlew :core:testDebugUnitTest --tests "com.streambox.core.ui.search.SearchViewModelTest"
```

## Out of scope

- Changes to `SearchScreen.kt` or `TvSearchScreen.kt` (confirmed: public
  `SearchViewModel` surface doesn't change, so no call-site edits needed).
- Rewriting existing tests to use `advanceTimeBy` instead of
  `advanceUntilIdle` (explicitly declined for this task — could be a
  separate follow-up spec if the team wants stricter timing assertions).
- Any change to debounce duration, minimum query length, or `SearchUiState`
  shape.
