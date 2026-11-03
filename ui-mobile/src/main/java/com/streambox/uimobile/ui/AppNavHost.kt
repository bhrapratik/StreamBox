package com.streambox.uimobile.ui
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.streambox.core.model.hasDetails
import com.streambox.uimobile.ui.details.DetailsScreen
import com.streambox.uimobile.ui.player.PlayerScreen
import kotlinx.serialization.Serializable

@Serializable object MainRoute
@Serializable data class DetailsRoute(val contentId: String)

@Serializable data class PlayerRoute(
    val contentId: String,
    val title: String,
    val artworkUrl: String? = null,
)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    Surface(Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = MainRoute) {
            composable<MainRoute> {
                MainScreen(
                    onContentClick = { content ->
                        if (content.hasDetails) navController.navigate(DetailsRoute(content.id))
                    },
                )
            }
            composable<DetailsRoute> {
                DetailsScreen(
                    onBack = { navController.popBackStack() },
                    onPlay = { d -> navController.navigate(PlayerRoute(d.id, d.title, d.backdropUrl)) },
                )
            }

            composable<PlayerRoute> {
                PlayerScreen(
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}