# CLAUDE.md
# StreamBox
Multi-module Android streaming app: :core, :ui-mobile, :mobile, :tv, :automotive.

## Commands
- Unit tests: ./gradlew :core:testDebugUnitTest
- Full check: ./gradlew assembleDebug testDebugUnitTest lint

## Architecture
- :core = models, repositories, ViewModels, playback. No Compose UI.
- :ui-mobile re-exports :core (api) for :mobile and :automotive.
- :tv depends on :core only — never on :ui-mobile.

## Conventions
- ViewModels expose StateFlow<XUiState> (sealed interface).
- Tests: StandardTestDispatcher + Dispatchers.setMain, fakes over mocks.

## Rules
- Ask before adding dependencies or editing libs.versions.toml.
- Done = builds, relevant tests ran (count > 0), diff summary given.
