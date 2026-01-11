package pies3.workit.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object GroupFeed : Screen("group_feed/{groupId}/{groupName}") {
        fun createRoute(groupId: String, groupName: String) = "group_feed/$groupId/$groupName"
    }
    data object PostDetail : Screen("post_detail/{postId}") {
        fun createRoute(postId: String) = "post_detail/$postId"
    }
}