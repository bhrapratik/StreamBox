package com.streambox.mobile.ui
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.streambox.core.model.hasDetails
import com.streambox.mobile.ui.details.DetailsScreen
import kotlinx.serialization.Serializable

@Serializable object MainRoute
@Serializable data class DetailsRoute(val contentId: String)

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
                    onPlay = { /* Step 7: player */ },
                )
            }
        }
    }
}