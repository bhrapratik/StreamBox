package com.streambox.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.streambox.core.model.Content
import com.streambox.mobile.ui.home.HomeScreen
import com.streambox.mobile.ui.profile.ProfileScreen
import com.streambox.mobile.ui.search.SearchScreen
import com.streambox.mobile.ui.theme.StreamColors

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
        val contentModifier = Modifier.padding(padding)
        when (selected) {
            Tab.Home -> HomeScreen(onContentClick = onContentClick, modifier = contentModifier)
            Tab.Search -> SearchScreen(onContentClick = onContentClick, modifier = contentModifier)
            Tab.Profile -> ProfileScreen(modifier = contentModifier)
            else -> Box(contentModifier.fillMaxSize(), Alignment.Center) {
                Text("${selected.label} – coming soon")
            }
        }
    }
}