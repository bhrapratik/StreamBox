package com.streambox.tv.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import coil3.compose.AsyncImage
import com.streambox.core.model.Content
import com.streambox.tv.ui.home.TvHomeScreen
import com.streambox.tv.ui.profile.TvProfileScreen
import com.streambox.tv.ui.search.TvSearchScreen
import com.streambox.tv.ui.theme.TvColors

enum class TvDestination(val label: String, val icon: ImageVector) {
    Search("Search", Icons.Outlined.Search),
    Home("Home", Icons.Outlined.Home),
    Profile("Profile", Icons.Outlined.Person),
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TvMainScreen(
    onContentClick: (Content) -> Unit,
) {
    var destination by rememberSaveable { mutableStateOf(TvDestination.Home) }
    var backdropUrl by remember { mutableStateOf<String?>(null) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val selectedItemFocus = remember { FocusRequester() }
    var drawerHasFocus by remember { mutableStateOf(false) }

    // Back ladder, step 2: at the first card (home's handler is disabled) → move focus to the drawer.
    // Registered BEFORE the home screen's handler, so home's handler gets priority when enabled.
    BackHandler(enabled = !drawerHasFocus) {
        runCatching { selectedItemFocus.requestFocus() }   // drawer opens automatically on focus
    }
    // Back ladder, step 3: drawer focused → no handler enabled → system finishes the Activity (exit).

    Surface(
        modifier = Modifier.fillMaxSize(),
        colors = SurfaceDefaults.colors(
            containerColor = TvColors.Background,
            contentColor = Color.White,
        ),
    ) {
        TvBackdrop(url = backdropUrl.takeIf { destination == TvDestination.Home })

        ModalNavigationDrawer(
            drawerState = drawerState,
            scrimBrush = Brush.horizontalGradient(listOf(TvColors.Background, Color.Transparent)),
            drawerContent = { _ ->
                Column(
                    Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 12.dp, vertical = 24.dp)
                        .onFocusChanged { drawerHasFocus = it.hasFocus }
                        .focusRestorer()                 // re-entering → last drawer item (= selected one)
                        .selectableGroup(),
                    verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
                ) {
                    TvDestination.entries.forEach { dest ->
                        NavigationDrawerItem(
                            selected = destination == dest,
                            onClick = { destination = dest },
                            leadingContent = { Icon(dest.icon, contentDescription = null) },
                            modifier = if (destination == dest) Modifier.focusRequester(
                                selectedItemFocus
                            ) else Modifier,
                        ) {
                            Text(dest.label)
                        }
                    }
                }
            },
        ) {
            when (destination) {
                TvDestination.Home -> TvHomeScreen(
                    onBackdropChange = { backdropUrl = it },
                    onContentClick = onContentClick,
                )

                TvDestination.Search -> TvSearchScreen(
                    onContentClick = onContentClick,
                )
                TvDestination.Profile -> TvProfileScreen()

                else -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("${destination.label} – coming soon")
                }
            }
        }
    }
}

@Composable
private fun TvBackdrop(url: String?) {
    Box(Modifier.fillMaxSize()) {
        Crossfade(targetState = url, animationSpec = tween(600), label = "backdrop") { target ->
            if (target != null) {
                AsyncImage(
                    model = target,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(0f to TvColors.Background, 0.6f to Color.Transparent)
                )
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(0.35f to Color.Transparent, 1f to TvColors.Background)
                )
        )
    }
}