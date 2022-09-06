package dev.roshana.jetpackcomposebasearch

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dev.roshana.presentation.navigation.Articles
import dev.roshana.jetpackcomposebasearch.navigation.BottomNavigationBar
import dev.roshana.presentation.navigation.Locations
import dev.roshana.jetpackcomposebasearch.navigation.MainNavigation

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun BaseContent() {
    val navController = rememberAnimatedNavController()

    val bottomScreens = listOf(
        Articles.ARTICLESLIST,
        Locations.LOCATIONLIST
    )

    val showNavBar = navController
        .currentBackStackEntryAsState().value?.destination?.route in bottomScreens

    Scaffold(
        backgroundColor = Color.White,
        bottomBar = {
            if (showNavBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { padding ->

        MainNavigation(navHostController = navController, modifier = Modifier.padding(padding))
    }
}
