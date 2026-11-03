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
    private val requestSpeed =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkSelfPermission(Car.PERMISSION_SPEED) != PackageManager.PERMISSION_GRANTED) {
            requestSpeed.launch(Car.PERMISSION_SPEED)
        }
        setContent {
            val theme by settingsViewModel.theme.collectAsStateWithLifecycle()
            theme?.let { StreamBoxTheme(it) { AppNavHost() } }
        }
    }
}