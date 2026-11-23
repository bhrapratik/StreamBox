# 001 — Search debounce refactor to MutableStateFlow

## Goal

`SearchViewModel` currently drives its debounce/search pipeline off a Compose
`mutableStateOf` via `snapshotFlow`. Per CLAUDE.md ("ViewModel inputs:
MutableStateFlow, not Compose mutableStateOf (testability)"), replace the
pipeline's input with a `MutableStateFlow<String>` while leaving the
ViewModel's public surface (`query`, `onQueryChange`) and all observable
behavior unchanged.
The point is testability: with MutableStateFlow, tests can start the
pipeline first, then type, then advance virtual time — which snapshotFlow
doesn't support in JVM unit tests.

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
- `tv/src/main/java/com/streambox/tv/ui/search/TvSearchScreen.kt:61-62` — binds
  `viewModel.query` and `viewModel::onQueryChange`, same as SearchScreen.
  Public surface must stay unchanged for both.

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

Existing tests 1–4 must still pass. Each names the line that breaks it:
1. short query stays Idle — breaks if `q.length < 2` check is removed
2. Loading then Results — breaks if `emit(SearchUiState.Loading)` is removed
3. Empty with query — breaks if `items.isEmpty()` branch is removed
4. failure emits Error — breaks if the `catch (e: Exception)` is removed

Test 5 ("rapid typing") is kept but does NOT guard debounce: all input
happens before the pipeline starts. Debounce is guarded by new tests 6–7.

New tests (subscribe, runCurrent(), THEN type, then advanceTimeBy):
6. `search waits for 400ms of silence` — "du", +399ms → no repository call;
   +2ms → queries == ["du"].
   Breaks if `.debounce(400.milliseconds)` is removed or shortened.
7. `burst of typing searches only the last query` — "du", +100ms, "dun",
   +100ms, "dune", +401ms → queries == ["dune"].
   Breaks if `.debounce(...)` is removed.

Mutation check (required): delete `.debounce(400.milliseconds)`, run tests,
confirm 6 and 7 fail, restore it.

## Verify

```
./gradlew :core:testDebugUnitTest --tests "com.streambox.core.ui.search.SearchViewModelTest"
```

## Out of scope

- Changes to `SearchScreen.kt` or `TvSearchScreen.kt` (confirmed: public
  `SearchViewModel` surface doesn't change, so no call-site edits needed).
- Any change to debounce duration, minimum query length, or `SearchUiState`
  shape.
