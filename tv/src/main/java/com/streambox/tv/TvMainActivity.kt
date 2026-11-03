package com.streambox.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.streambox.core.ui.setting.SettingsViewModel
import com.streambox.tv.ui.TvMainScreen
import com.streambox.tv.ui.TvNavHost
import com.streambox.tv.ui.theme.StreamBoxTvTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TvMainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val theme by settingsViewModel.theme.collectAsStateWithLifecycle()
            theme?.let { current ->
                StreamBoxTvTheme(current) {
                    TvNavHost()
                }
            }
        }
    }
}


