package dev.roshana.jetpackcomposebasearch.navigation

import androidx.annotation.DrawableRes
import dev.roshana.jetpackcomposebasearch.R

sealed class NavigationItem(
    var title: String,
    var screen_route: String,
    @DrawableRes var icon: Int
) {
    object ArticlesScreen : NavigationItem(
        title = "article",
        screen_route = Articles.ARTICLESLIST,
        icon = R.drawable.ic_baseline_settings
    )

    object LocationsScreen : NavigationItem(
        title = "locations",
        screen_route = Locations.LOCATIONLIST,
        R.drawable.ic_baseline_add_location
    )
}
