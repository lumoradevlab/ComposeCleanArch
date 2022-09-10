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
import dev.roshana.presentation.navigation.Articles
import dev.roshana.presentation.navigation.Locations
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun MainNavigation(navHostController: NavHostController, modifier: Modifier = Modifier) {

    NavHost(
        navController = navHostController,
        startDestination = Articles.ARTICLESLIST, modifier = modifier
    ) {
        /*articleGraph(navHostController = navHostController)
        locationGraph(navHostController = navHostController)*/

        composable(Articles.ARTICLESLIST) {
            ArticlesListScreen()
        }

        composable(Locations.LOCATIONLIST) {
            LocationScreen()
        }
    }
}