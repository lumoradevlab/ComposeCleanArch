/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dev.lumora.composearch.app.navigation.AppNavHost
import dev.lumora.composearch.app.navigation.BottomBar

/**
 * The app shell: one Scaffold, one bottom bar, one NavHost. Everything else is a
 * destination.
 */
@Composable
fun ComposeCleanArchApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Scaffold(
        modifier = modifier,
        bottomBar = { BottomBar(navController) },
    ) { padding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(padding),
        )
    }
}
