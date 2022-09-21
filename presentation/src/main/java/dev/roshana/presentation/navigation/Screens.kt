package dev.roshana.presentation.navigation

import dev.roshana.presentation.navigation.Articles.ARTICLESLIST
import dev.roshana.presentation.navigation.Locations.LOCATIONLIST
import dev.roshana.presentation.navigation.Welcome.WELCOMEGRAPH

/** PariSa;
coding and smoking ;)
 **/

sealed class Screens(val route: String) {
    object WelcomeScreen : Screens(route = WELCOMEGRAPH)
    object ArticleListScreen : Screens(route = ARTICLESLIST)
    object LocationScreen : Screens(route = LOCATIONLIST)
}
