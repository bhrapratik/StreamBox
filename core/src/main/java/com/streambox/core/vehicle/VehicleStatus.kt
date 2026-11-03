package com.streambox.core.vehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.BindsOptionalOf
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.util.Optional
import javax.inject.Inject

data class VehicleState(
    val drivingRestricted: Boolean,
    val gear: String? = null,
    val speedKmh: Float? = null,
)

/** Implemented only in :automotive. */
interface VehicleStatusSource {
    val state: Flow<VehicleState>
}

@Module
@InstallIn(SingletonComponent::class)
abstract class VehicleModule {
    @BindsOptionalOf
    abstract fun optionalVehicleStatusSource(): VehicleStatusSource
}

@HiltViewModel
class VehicleViewModel @Inject constructor(
    source: Optional<VehicleStatusSource>,
) : ViewModel() {
    /** null on phone → UI hides the Vehicle section */
    val state: StateFlow<VehicleState?> =
        if (source.isPresent) {
            source.get().state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
        } else {
            MutableStateFlow(null)
        }
}