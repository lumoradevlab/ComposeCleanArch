package dev.roshana.jetpackcomposebasearch.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun MainNavigation(navHostController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navHostController, startDestination = NavigationItem.ArticlesScreen.screen_route) {
        composable(NavigationItem.ArticlesScreen.screen_route) {

        }
        composable(NavigationItem.LocationsScreen.screen_route) {

        }
    }
}