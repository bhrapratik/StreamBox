package com.streambox.tv.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import com.streambox.core.model.hasDetails
import com.streambox.tv.ui.home.detail.TvDetailsScreen
import com.streambox.tv.ui.theme.TvColors
import kotlinx.serialization.Serializable

@Serializable object TvMainRoute
@Serializable data class TvDetailsRoute(val contentId: String)

@Composable
fun TvNavHost() {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        colors = SurfaceDefaults.colors(containerColor = TvColors.Background, contentColor = Color.White),
    ) {
        NavHost(navController = navController, startDestination = TvMainRoute) {
            composable<TvMainRoute> {
                TvMainScreen(
                    onContentClick = { content ->
                        if (content.hasDetails) navController.navigate(TvDetailsRoute(content.id))
                    },
                )
            }
            composable<TvDetailsRoute> {
                TvDetailsScreen(onPlay = { /* Step 7 */ })
            }
        }
    }
}