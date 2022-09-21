package dev.roshana.jetpackcomposebasearch

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dev.roshana.jetpackcomposebasearch.navigation.BottomNavigationBar
import dev.roshana.jetpackcomposebasearch.navigation.MainNavigation
import dev.roshana.presentation.navigation.Articles
import dev.roshana.presentation.navigation.Locations
import dev.roshana.presentation.welcomeUi.WelcomeViewModel

/** PariSa;
coding and smoking ;)
 **/

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

    val welcomeViewModel: WelcomeViewModel = hiltViewModel()
    val startScreen = welcomeViewModel.startDestination


    Scaffold(
        bottomBar = {
            if (showNavBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { padding ->

        MainNavigation(
            navHostController = navController,
            modifier = Modifier.padding(padding),
            startDestination = startScreen.value
        )
    }
}
