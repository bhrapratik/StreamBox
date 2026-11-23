# :automotive
- Reuses :ui-mobile's AppNavHost; car-specific code lives here only.
- Driving restriction comes from CarUxRestrictionsManager via CarVehicleStatusSource.
  When drivingRestricted = true: no video playback, no text input, audio only.
- Never call android.car APIs from :core or :ui-mobile — go through VehicleStatusSource.
- Large touch targets, minimal text (Car app quality guidelines).