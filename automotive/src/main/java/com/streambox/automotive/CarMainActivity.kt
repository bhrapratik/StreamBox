package com.streambox.automotive

import android.car.Car
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.streambox.core.ui.setting.SettingsViewModel
import com.streambox.uimobile.ui.AppNavHost
import com.streambox.uimobile.ui.theme.StreamBoxTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CarMainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    private val carPermissions = arrayOf(
        Car.PERMISSION_SPEED,
        Car.PERMISSION_POWERTRAIN
    )

    private val requestCarPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.values.any { it }) recreate()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val missingPermissions = carPermissions.filter {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }
        if (missingPermissions.isNotEmpty()) {
            requestCarPermissions.launch(missingPermissions.toTypedArray())
        }
        setContent {
            val theme by settingsViewModel.theme.collectAsStateWithLifecycle()
            theme?.let { StreamBoxTheme(it) { AppNavHost() } }
        }
    }
}