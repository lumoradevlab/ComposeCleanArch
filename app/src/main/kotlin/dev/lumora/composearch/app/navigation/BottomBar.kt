/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlin.reflect.KClass

/**
 * The bottom navigation bar. Its tab table is typed as [TopLevelRoute], so adding a tab
 * means adding an entry here and nothing else — and a non-top-level destination cannot
 * be listed by mistake.
 *
 * The bar hides itself on any destination that isn't a tab (e.g. article detail), which
 * is why detail screens get the full height without each one knowing about the shell.
 */
@Composable
fun BottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val isTopLevel = TOP_LEVEL_TABS.any { tab ->
        currentDestination?.hierarchy?.any { it.hasRoute(tab.route) } == true
    }
    if (!isTopLevel) return

    NavigationBar {
        TOP_LEVEL_TABS.forEach { tab ->
            val selected = currentDestination?.hierarchy?.any { it.hasRoute(tab.route) } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(tab.destination) {
                        // Standard tab behaviour: one instance per tab, state preserved,
                        // and back from any tab returns to the start destination.
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) },
            )
        }
    }
}

/** One bottom-bar tab. [route] is the class used for selection matching. */
private data class Tab(
    val destination: TopLevelRoute,
    val route: KClass<out TopLevelRoute>,
    val label: String,
    val icon: ImageVector,
)

private val TOP_LEVEL_TABS = listOf(
    Tab(ArticlesRoute, ArticlesRoute::class, "Headlines", Icons.AutoMirrored.Filled.List),
    Tab(SettingsRoute, SettingsRoute::class, "Settings", Icons.Filled.Settings),
)
