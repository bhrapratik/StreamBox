# :tv
- Use androidx.tv:tv-material components; no androidx.compose.material3 in TV screens.
- Every screen must be fully usable with D-pad: visible focus, sensible initial focus,
  focus restored on back navigation (see ui/focus/).
- No touch-only interactions (swipe, long-press as the only path).
- Logic stays in :core ViewModels; this module is UI only.