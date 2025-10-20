package pies3.workit.ui.components
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState


data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val navItems = listOf(
    BottomNavItem(
        label = "Feed",
        icon = Icons.Default.Home,
        route = "feed"
    ),
    BottomNavItem(
        label = "Groups",
        icon = Icons.Default.List,
        route = "groups"
    ),
    BottomNavItem(
        label = "Post",
        icon = Icons.Default.AddCircle,
        route = "post"
    ),
    BottomNavItem(
        label = "Profile",
        icon = Icons.Default.AccountCircle,
        route = "profile"
    )
)

@Composable
fun AppBottomBar(navController: NavController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {

        navItems.forEach { item ->

            NavigationBarItem(
                selected = (currentRoute == item.route),

                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(text = item.label)
                }
            )
        }
    }
}