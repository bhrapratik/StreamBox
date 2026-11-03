package com.streambox.automotive.vehicle
import com.streambox.core.vehicle.VehicleStatusSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CarModule {
    @Binds
    abstract fun bindVehicleStatusSource(impl: CarVehicleStatusSource): VehicleStatusSource
}