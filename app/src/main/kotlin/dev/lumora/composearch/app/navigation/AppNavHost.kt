/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.lumora.composearch.app.ui.articles.ArticleDetailScreen
import dev.lumora.composearch.app.ui.articles.ArticleListScreen
import dev.lumora.composearch.app.ui.settings.SettingsScreen

/**
 * The single NavHost. Destinations are the @Serializable types from [Routes.kt], so
 * `composable<ArticleDetailRoute>` and `toRoute()` replace string paths and manual
 * argument parsing entirely.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = ArticlesRoute,
        modifier = modifier,
    ) {
        composable<ArticlesRoute> {
            ArticleListScreen(
                // Cross-destination jumps are passed down as lambdas, never by handing a
                // screen the NavController — that is what keeps a screen independently
                // previewable and movable into a feature module later.
                onOpenArticle = { article -> navController.navigate(ArticleDetailRoute(article.url)) },
            )
        }

        composable<SettingsRoute> {
            SettingsScreen()
        }

        composable<ArticleDetailRoute> { backStackEntry ->
            val route: ArticleDetailRoute = backStackEntry.toRoute()
            ArticleDetailScreen(url = route.url)
        }
    }
}
