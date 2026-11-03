package com.streambox.automotive.vehicle

import android.car.Car
import android.car.VehicleGear
import android.car.VehiclePropertyIds
import android.car.drivingstate.CarUxRestrictionsManager
import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import android.content.Context
import android.util.Log
import com.streambox.core.vehicle.VehicleState
import com.streambox.core.vehicle.VehicleStatusSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class CarVehicleStatusSource @Inject constructor(
    @ApplicationContext private val context: Context,
) : VehicleStatusSource {

    override val state: Flow<VehicleState> = callbackFlow {
        val car = Car.createCar(context)
        val uxManager = car.getCarManager(Car.CAR_UX_RESTRICTION_SERVICE) as CarUxRestrictionsManager
        val propertyManager = car.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager


        var current = VehicleState(
            drivingRestricted = uxManager.currentCarUxRestrictions.isRequiresDistractionOptimization,
        )
        trySend(current)

        // Driving state (parked / idling / moving), derived by CarService
        uxManager.registerListener { restrictions ->
            current = current.copy(drivingRestricted = restrictions.isRequiresDistractionOptimization)
            trySend(current)
        }

        fun readInitial() {
            runCatching {
                val speed = propertyManager.getFloatProperty(VehiclePropertyIds.PERF_VEHICLE_SPEED, 0)
                current = current.copy(speedKmh = speed * 3.6f)
            }.onFailure { Log.w("Vehicle", "speed read failed", it) }
            runCatching {
                val gear = propertyManager.getIntProperty(VehiclePropertyIds.GEAR_SELECTION, 0)
                current = current.copy(gear = gearLabel(gear))
            }.onFailure { Log.w("Vehicle", "gear read failed", it) }
            trySend(current)
        }
        readInitial()
        // Raw properties from the Vehicle HAL
        val callback = object : CarPropertyManager.CarPropertyEventCallback {
            override fun onChangeEvent(value: CarPropertyValue<*>) {
                current = when (value.propertyId) {
                    VehiclePropertyIds.PERF_VEHICLE_SPEED ->
                        current.copy(speedKmh = (value.value as Float) * 3.6f)     // m/s → km/h
                    VehiclePropertyIds.GEAR_SELECTION ->
                        current.copy(gear = gearLabel(value.value as Int))
                    else -> current
                }
                trySend(current)
            }
            override fun onErrorEvent(propertyId: Int, areaId: Int) = Unit
        }

        // SecurityException if the permission isn't granted yet → section shows "unknown"
        runCatching { propertyManager.registerCallback(callback, VehiclePropertyIds.PERF_VEHICLE_SPEED, CarPropertyManager.SENSOR_RATE_ONCHANGE) }
            .onFailure { Log.w("Vehicle", "speed subscribe failed", it) }
        runCatching { propertyManager.registerCallback(callback, VehiclePropertyIds.GEAR_SELECTION, CarPropertyManager.SENSOR_RATE_ONCHANGE) }
            .onFailure { Log.w("Vehicle", "gear subscribe failed", it) }

        awaitClose {
            uxManager.unregisterListener()
            propertyManager.unregisterCallback(callback)
            car.disconnect()
        }
    }


    private fun gearLabel(gear: Int) = when (gear) {
        VehicleGear.GEAR_PARK -> "P"
        VehicleGear.GEAR_REVERSE -> "R"
        VehicleGear.GEAR_NEUTRAL -> "N"
        VehicleGear.GEAR_DRIVE -> "D"
        else -> "–"
    }
}