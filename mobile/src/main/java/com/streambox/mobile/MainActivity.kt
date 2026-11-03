package com.streambox.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.streambox.core.ui.setting.SettingsViewModel
import com.streambox.uimobile.ui.AppNavHost
import com.streambox.uimobile.ui.theme.StreamBoxTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        setContent {
            val theme by settingsViewModel.theme.collectAsStateWithLifecycle()
            theme?.let { current ->                 // null = first DataStore read not done yet (a few ms)
                StreamBoxTheme(current) {
                    AppNavHost()
                }
            }
        }
    }
}