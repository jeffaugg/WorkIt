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
        route = "profile?showEditModal={showEditModal}",
        label = "Profile",
        icon = Icons.Default.AccountCircle
    ) {
        fun createRoute(showEditModal: Boolean) = "profile?showEditModal=$showEditModal"
    }
}


val bottomNavItems = listOf(
    BottomBarScreen.Feed,
    BottomBarScreen.Groups,
    BottomBarScreen.Post,
    BottomBarScreen.Profile,
)
