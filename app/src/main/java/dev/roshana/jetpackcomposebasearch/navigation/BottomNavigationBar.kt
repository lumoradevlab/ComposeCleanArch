package dev.roshana.jetpackcomposebasearch.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

/** PariSa;
coding and smoking ;)
 **/

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomNavigationBar(navHostController: NavHostController) {

    val bottomScreens = listOf(
        NavigationItem.ArticlesScreen,
        NavigationItem.LocationsScreen,
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        backgroundColor = Color.LightGray

    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            bottomScreens.map {
                val isSelected = navHostController
                    .currentBackStackEntryAsState().value?.destination?.route == it.screen_route
                val animatedWeight by animateFloatAsState(targetValue = if (isSelected) 1.5f else 1f)

                CustomBottomNavItem(
                    screen = it,
                    isSelected = isSelected,
                    modifier = Modifier
                        .weight(animatedWeight)
                        .clickable {
                            navHostController.navigate(
                                it.screen_route
                            ) {
                                restoreState = true
                                launchSingleTop = true

                                val destination = navHostController.graph.findStartDestination().id
                                popUpTo(destination) { saveState = true }
                            }
                        }
                )
            }

        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun CustomBottomNavItem(
    modifier: Modifier = Modifier,
    screen: NavigationItem,
    isSelected: Boolean,
) {
    val animatedIconSize by animateDpAsState(
        targetValue = if (isSelected) 30.dp else 25.dp,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .height(if (isSelected) 50.dp else 50.dp)
                .background(
                    color = MaterialTheme.colors.onBackground,
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(horizontal = 5.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(id = screen.icon), contentDescription = "bottom Bar Icon",

                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxHeight()
                    .padding(horizontal = 5.dp)
                    .alpha(if (isSelected) 1f else .7f)
                    .size(animatedIconSize),
                tint = if (isSelected) MaterialTheme.colors.primary else Color.Gray
            )

            if (isSelected) {
                Text(
                    text = screen.title,
                    modifier = Modifier.padding(start = 8.dp, end = 10.dp),
                    maxLines = 1,
                    color = MaterialTheme.colors.primary
                )
            }
        }
    }
}