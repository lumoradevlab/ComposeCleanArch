package dev.roshana.jetpackcomposebasearch.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.roshana.presentation.articleUi.ArticlesListScreen
import dev.roshana.presentation.locationUi.LocationScreen
import dev.roshana.presentation.navigation.Screens
import dev.roshana.presentation.welcomeUi.WelcomeScreen
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun MainNavigation(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String
) {

    NavHost(
        navController = navHostController,
        startDestination = startDestination, modifier = modifier
    ) {
        /*articleGraph(navHostController = navHostController)
        locationGraph(navHostController = navHostController)*/

        composable(Screens.WelcomeScreen.route) {
            WelcomeScreen(navHostController = navHostController)
        }

        composable(Screens.ArticleListScreen.route) {
            ArticlesListScreen()
        }

        composable(Screens.LocationScreen.route) {
            LocationScreen()
        }
    }
}