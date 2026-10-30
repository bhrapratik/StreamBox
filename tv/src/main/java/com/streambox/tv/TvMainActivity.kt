package com.streambox.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.streambox.tv.ui.TvMainScreen
import com.streambox.tv.ui.TvNavHost
import com.streambox.tv.ui.theme.StreamBoxTvTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TvMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StreamBoxTvTheme {
                TvNavHost()
            }
        }
    }
}



