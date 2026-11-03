package com.streambox.uimobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.streambox.core.model.Content
import com.streambox.uimobile.ui.adaptive.isWideLayout
import com.streambox.uimobile.ui.home.HomeScreen
import com.streambox.uimobile.ui.profile.ProfileScreen
import com.streambox.uimobile.ui.search.SearchScreen
import com.streambox.uimobile.ui.theme.StreamColors


enum class Tab(val label: String, val selectedIcon: ImageVector, val icon: ImageVector) {
    Home("Home", Icons.Filled.Home, Icons.Outlined.Home),
    Search("Search", Icons.Filled.Search, Icons.Outlined.Search),
    Profile("Profile", Icons.Filled.Person, Icons.Outlined.Person),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onContentClick: (Content) -> Unit,
) {
    var selected by rememberSaveable { mutableStateOf(Tab.Home) }

    val wide = isWideLayout()

    val content: @Composable (Modifier) -> Unit = { m ->
        when (selected) {
            Tab.Home -> HomeScreen(onContentClick = onContentClick, modifier = m)
            Tab.Search -> SearchScreen(onContentClick = onContentClick, modifier = m)
            Tab.Profile -> ProfileScreen(modifier = m)
        }
    }


    if (wide) {
        Row(Modifier.fillMaxSize().background(StreamColors.Background)) {
            NavigationRail(
                containerColor = StreamColors.Background,
                header = {
                    Text("SB", fontWeight = FontWeight.Bold, color = StreamColors.Primary,
                        modifier = Modifier.padding(vertical = 16.dp))
                },
            ) {
                Spacer(Modifier.weight(1f))
                Tab.entries.forEach { tab ->
                    NavigationRailItem(
                        selected = selected == tab,
                        onClick = { selected = tab },
                        icon = { Icon(if (selected == tab) tab.selectedIcon else tab.icon, null) },
                        label = { Text(tab.label) },
                        colors = NavigationRailItemDefaults.colors(
                            indicatorColor = StreamColors.Surface,
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = StreamColors.Muted,
                            unselectedTextColor = StreamColors.Muted,
                        ),
                    )
                }
                Spacer(Modifier.weight(1f))
            }
            content(Modifier.weight(1f).fillMaxHeight())
        }
    } else{
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("StreamBox", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = { /* Step 9: Search */ }) {
                            Icon(Icons.Outlined.Search, contentDescription = "Search")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = StreamColors.Background,
                    ),
                )
            },
            bottomBar = {
                NavigationBar(containerColor = StreamColors.Background) {
                    Tab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = selected == tab,
                            onClick = { selected = tab },
                            icon = {
                                Icon(if (selected == tab) tab.selectedIcon else tab.icon, contentDescription = null)
                            },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = StreamColors.Surface,
                                selectedIconColor = MaterialTheme.colorScheme.onBackground,
                                selectedTextColor = MaterialTheme.colorScheme.onBackground,
                                unselectedIconColor = StreamColors.Muted,
                                unselectedTextColor = StreamColors.Muted,
                            ),
                        )
                    }
                }
            },
        ) { padding ->
            content(Modifier.padding(padding))
        }
    }
}