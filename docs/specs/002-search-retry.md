# 002 — SearchViewModel.retry()

## Goal

Add a `retry()` function to `SearchViewModel` that, when the current state is
`SearchUiState.Error`, re-runs the query that produced that error
immediately — without waiting for the 400ms debounce. `:core` only, no UI
changes.

## Context

- `core/src/main/java/com/streambox/core/ui/search/SearchViewModel.kt:35-58`
  — `queryFlow` (`MutableStateFlow<String>`) feeds
  `.map { trim() }.debounce(400ms).distinctUntilChanged().flatMapLatest { ... }`,
  which drives `state: StateFlow<SearchUiState>` via `stateIn`. `state` is a
  read-only projection of this pipeline — there is currently no way to push a
  value into it other than through `queryFlow` + debounce.
- `core/src/main/java/com/streambox/core/ui/search/SearchUiState.kt` —
  sealed interface (`Idle`, `Loading`, `Results`, `Empty`, `Error`). No new
  case is needed; retry reuses `Loading` → `Results`/`Empty`/`Error`.
- `core/src/test/java/com/streambox/core/ui/search/SearchViewModelTest.kt` —
  existing 7 tests, pattern to follow: `StandardTestDispatcher`,
  `viewModel.state.test { ... }` (Turbine), `advanceTimeBy`/`runCurrent` for
  timing assertions, `FakeSearchRepository` implementing `SearchRepository`.
- `core/src/main/java/com/streambox/core/ui/search/SearchRepository.kt` —
  interface with `suspend fun search(query: String): List<Content>`.
- No UI call sites (`ui-mobile`/`tv` `SearchScreen.kt`/`TvSearchScreen.kt`)
  are touched by this spec — out of scope per the task.

## Requirements

1. Track the query that actually entered the search pipeline (the `q` passed
   into the `flatMapLatest` block, after trim/debounce/distinctUntilChanged)
   in a private field, e.g. `lastSearchedQuery: String`, updated each time a
   search is about to run. This is deliberately *not* the same as the public
   `query` field — `query` may hold newer, not-yet-debounced keystrokes.
2. Add a second input into the existing pipeline that bypasses debounce and
   distinctUntilChanged entirely — e.g. a `MutableSharedFlow<String>`
   (`retryTrigger`) merged with the debounced query flow, both feeding the
   same `flatMapLatest` block. A value arriving via this path must reach
   `search()` with no 400ms wait. Use `MutableSharedFlow<String>(extraBufferCapacity = 1)` and
   `tryEmit` in `retry()` — a no-buffer SharedFlow drops tryEmit
   values while collected.
3. `fun retry()`:
   - If `state.value` is not `SearchUiState.Error`, no-op (per answered
     question: only acts on Error).
   - If `state.value` is `SearchUiState.Error`, push `lastSearchedQuery`
     into the retry path.
4. A retry re-runs the full `Loading` → `search(q)` → `Results`/`Empty`/
   `Error` sequence — same as a normal search (per answered question: show
   Loading on retry).
5. If `retry()` is called again while a previous retry's `search()` call is
   still suspended, the latest retry wins and cancels the in-flight one —
   the same `flatMapLatest` cancellation semantics that already apply to
   normal typed queries (per answered question).
6. Preserve all current behavior of the typed-query path: 400ms debounce,
   `.trim()`, `q.length < 2` → `Idle`, `CancellationException` rethrow in
   `search()`. Retry must not alter any existing test's outcome.

## Constraints

- No new dependencies — `MutableSharedFlow`/`merge` are already available
  via `kotlinx-coroutines-core`. Don't touch `gradle/libs.versions.toml` or
  `build.gradle.kts`.
- No new `SearchUiState` case (e.g. no `Retrying` state) — reuse `Loading`.
- `:core` only — no changes to `ui-mobile`, `tv`, or `mobile`/`automotive`
  modules, and no changes to `SearchScreen.kt` / `TvSearchScreen.kt`.
- `query` and `onQueryChange`'s existing behavior and types are unchanged.
- One commit for this task, per CLAUDE.md.

## Acceptance

Each item is one test; each names the line/behavior whose removal breaks it.

1. **retry on Error re-runs the failed query and succeeds** — query "inter"
   fails (`Error`); call `retry()` with a repository now configured to
   succeed; expect `Loading` then `Results`. Breaks if `retry()` doesn't
   push `lastSearchedQuery` into the pipeline, or if the merge/retry path is
   removed.
2. **retry is immediate, no debounce wait** — after step 1's `Error`, call
   `retry()`, then `advanceTimeBy(0)`/`runCurrent()` only (not 400ms);
   `repository.queries` already contains the retried query before any
   debounce-length time has passed. Breaks if the retry path is routed
   through `.debounce(400.milliseconds)` instead of bypassing it.
3. **retry no-ops outside Error state** — in `Idle`, `Loading`, or `Results`,
   calling `retry()` produces no new repository call and no new emission
   (`expectNoEvents()`). Breaks if the `state.value is Error` guard in
   `retry()` is removed.
4. **retry uses the last *searched* query, not the current unsubmitted
   text** — query "inter" fails (`Error`); then `onQueryChange("interst")`
   (within the 400ms debounce window, so no search fires for it yet); then
   `retry()`. Expect the repository is called with `"inter"`, not
   `"interst"`. Breaks if `retry()` reads `query`/`queryFlow.value` instead
   of the tracked `lastSearchedQuery`. Assert `repository.queries.last() == "inter"` before 400ms has
   elapsed since `onQueryChange("interst")`.
5. **a second retry cancels the first (latest wins)** — query fails; call
   `retry()` against a repository whose `search()` suspends long enough to
   observe overlap; before it resolves, call `retry()` again (repository
   configured to return a different result the second time); expect the
   final state reflects only the second call's result, and the first call's
   result is never emitted. Breaks if the retry path uses `flatMapMerge`/
   `flatMapConcat` instead of `flatMapLatest`-style cancellation, or if two
   independent collectors are started instead of one shared pipeline.
6. **existing 7 tests in `SearchViewModelTest.kt` still pass unmodified** —
   guards against the retry addition changing typed-query debounce,
   `distinctUntilChanged`, or sequencing behavior.

Mutation check (required): remove the `state.value is SearchUiState.Error`
guard from `retry()` (make it unconditional), run the test suite, confirm
test 3 fails; restore the guard.

## Verify

```
./gradlew :core:testDebugUnitTest --tests "com.streambox.core.ui.search.SearchViewModelTest"
```

## Out of scope

- Any UI change (no retry button, no call sites in `SearchScreen.kt` /
  `TvSearchScreen.kt`).
- A new `SearchUiState` case for "retrying".
- Retry limits, backoff, or retry-count tracking.
- Changes to debounce duration or minimum query length.
