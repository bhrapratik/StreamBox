# StreamBox
streaming app. Modules: :core, :ui-mobile, :mobile, :tv, :automotive.
Data: TMDB + TVmaze (core/network/).

## Commands
- Core tests: ./gradlew :core:testDebugUnitTest
- One class:  ./gradlew :core:testDebugUnitTest --tests "com.streambox.core.ui.search.SearchViewModelTest"
- Full check: ./gradlew assembleDebug testDebugUnitTest lint
- Odd build after editing *.gradle.kts → retry with --no-configuration-cache

## Architecture
- :core = models, repositories, ViewModels, playback, DI modules, vehicle contract. No screens.
- :ui-mobile = shared phone UI; exposes :core via api(). Used by :mobile and :automotive.
- :tv depends on :core only — never on :ui-mobile.
- Platform features go behind an interface in :core, implemented in the app module
  (pattern: VehicleStatusSource + @BindsOptionalOf, impl in :automotive).

## Conventions
- ViewModel state: StateFlow<XUiState>, XUiState is a sealed interface.
- ViewModel inputs: MutableStateFlow, not Compose mutableStateOf (testability).
- Rethrow CancellationException in catch blocks.
- Hilt for DI. Repositories are interfaces → tests use hand-written fakes, no mocking libs.
- Dependencies only via gradle/libs.versions.toml (libs.*), never inline coordinates.

## Tests
- StandardTestDispatcher + Dispatchers.setMain/resetMain; Turbine for Flow sequences.
- Time-based logic (debounce, timeouts) must use advanceTimeBy, not just advanceUntilIdle.
- Every new test must be able to fail: state which code change would break it.

## Secrets
- TMDB_TOKEN comes from local.properties → BuildConfig. Never hardcode or commit tokens.

## Rules
- Ask before adding dependencies or editing libs.versions.toml / build.gradle.kts.
- Don't change production code when asked only for tests — report the issue instead.
- One task per commit, with a descriptive message.
- Done = build passes, relevant tests ran (count > 0), short diff summary.