package pies3.workit.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector


sealed class BottomBarScreen(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    object Feed : BottomBarScreen(
        route = "feed",
        label = "Feed",
        icon = Icons.Default.Home
    )

    object Groups : BottomBarScreen(
        route = "groups",
        label = "Groups",
        icon = Icons.Default.List
    )

    object Post : BottomBarScreen(
        route = "post",
        label = "Post",
        icon = Icons.Default.AddCircle
    )

    object Profile : BottomBarScreen(
        route = "profile",
        label = "Profile",
        icon = Icons.Default.AccountCircle
    )
}


val bottomNavItems = listOf(
    BottomBarScreen.Feed,
    BottomBarScreen.Groups,
    BottomBarScreen.Post,
    BottomBarScreen.Profile,
)


sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main_graph")
}