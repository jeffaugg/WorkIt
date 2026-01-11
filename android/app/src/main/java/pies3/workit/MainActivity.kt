package pies3.workit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import pies3.workit.ui.components.AppBottomBar
import pies3.workit.ui.features.auth.login.LoginScreen
import pies3.workit.ui.features.auth.register.RegisterScreen
import pies3.workit.ui.features.feed.FeedScreen
import pies3.workit.ui.features.feed.GroupFeedScreen
import pies3.workit.ui.features.groups.GroupsScreen
import pies3.workit.ui.features.post.EditPostScreen
import pies3.workit.ui.features.post.PostDetailScreen
import pies3.workit.ui.features.post.PostScreen
import pies3.workit.ui.features.profile.ProfileScreen
import pies3.workit.ui.features.splash.SplashScreen
import pies3.workit.ui.navigation.BottomBarScreen
import pies3.workit.ui.navigation.Screen
import pies3.workit.ui.navigation.bottomNavItems
import pies3.workit.ui.theme.WorkItTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            val systemTheme = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemTheme) }

            WorkItTheme(darkTheme = isDarkTheme) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar = currentRoute in bottomNavItems.map { it.route }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            AppBottomBar(navController = navController)
                        }
                    }) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Splash.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(
                            route = Screen.Splash.route,
                            exitTransition = { fadeOut(animationSpec = tween(500)) }
                        ) {
                            SplashScreen(
                                onAnimationEnd = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Splash.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(
                            route = Screen.Login.route,
                            enterTransition = {
                                fadeIn(animationSpec = tween(1000, delayMillis = 300))
                            },
                            exitTransition = {
                                slideOutHorizontally(
                                    targetOffsetX = { -it / 3 },
                                    animationSpec = tween(300)
                                ) + fadeOut(animationSpec = tween(300))
                            },
                            popEnterTransition = {
                                slideInHorizontally(
                                    initialOffsetX = { -it / 3 },
                                    animationSpec = tween(300)
                                ) + fadeIn(animationSpec = tween(300))
                            }
                        ) {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate(BottomBarScreen.Feed.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                },
                                onRegisterClick = {
                                    navController.navigate(Screen.Register.route)
                                }
                            )
                        }
                        composable(
                            route = Screen.Register.route,
                            enterTransition = {
                                slideInHorizontally(
                                    initialOffsetX = { it / 3 },
                                    animationSpec = tween(300)
                                ) + fadeIn(animationSpec = tween(300))
                            },
                            popExitTransition = {
                                slideOutHorizontally(
                                    targetOffsetX = { it / 3 },
                                    animationSpec = tween(300)
                                ) + fadeOut(animationSpec = tween(300))
                            }
                        ) {
                            RegisterScreen(
                                onRegisterSuccess = {
                                    navController.navigate(BottomBarScreen.Profile.createRoute(true)) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                },
                                onLoginClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(BottomBarScreen.Feed.route) { backStackEntry ->
                            val shouldRefresh = backStackEntry.savedStateHandle.get<Boolean>("refresh") ?: false
                            
                            FeedScreen(
                                onPostClick = { postId ->
                                    navController.navigate(Screen.PostDetail.createRoute(postId))
                                },
                                shouldRefresh = shouldRefresh,
                                onRefreshHandled = {
                                    backStackEntry.savedStateHandle.remove<Boolean>("refresh")
                                }
                            )
                        }
                        composable("groups") {
                            GroupsScreen(
                                onGroupClick = { groupId, groupName ->
                                    navController.navigate(Screen.GroupFeed.createRoute(groupId, groupName))
                                }
                            )
                        }
                        composable(
                            route = Screen.GroupFeed.route,
                            arguments = listOf(
                                navArgument("groupId") { type = NavType.StringType },
                                navArgument("groupName") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
                            val groupName = backStackEntry.arguments?.getString("groupName") ?: ""
                            val shouldRefresh = backStackEntry.savedStateHandle.get<Boolean>("refresh") ?: false
                            
                            GroupFeedScreen(
                                groupId = groupId,
                                groupName = groupName,
                                onNavigateBack = { navController.popBackStack() },
                                onPostClick = { postId ->
                                    navController.navigate(Screen.PostDetail.createRoute(postId))
                                },
                                shouldRefresh = shouldRefresh,
                                onRefreshHandled = {
                                    backStackEntry.savedStateHandle.remove<Boolean>("refresh")
                                }
                            )
                        }
                        composable(
                            route = Screen.PostDetail.route,
                            arguments = listOf(
                                navArgument("postId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val postId = backStackEntry.arguments?.getString("postId") ?: ""
                            PostDetailScreen(
                                postId = postId,
                                onNavigateBack = { navController.popBackStack() },
                                onPostDeleted = {
                                    val previousRoute = navController.previousBackStackEntry?.destination?.route
                                    navController.popBackStack()
                                    navController.currentBackStackEntry?.savedStateHandle?.set("refresh", true)
                                },
                                onEditClick = { id ->
                                    navController.navigate(Screen.EditPost.createRoute(id))
                                }
                            )
                        }
                        composable(
                            route = Screen.EditPost.route,
                            arguments = listOf(
                                navArgument("postId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val postId = backStackEntry.arguments?.getString("postId") ?: ""
                            EditPostScreen(
                                postId = postId,
                                onNavigateBack = { navController.popBackStack() },
                                onPostUpdated = {
                                    navController.popBackStack()
                                    navController.currentBackStackEntry?.savedStateHandle?.set("refresh", true)
                                }
                            )
                        }
                        composable("post") {
                            PostScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onPostCreated = {
                                    navController.navigate(BottomBarScreen.Feed.route) {
                                        popUpTo(BottomBarScreen.Feed.route) { 
                                            inclusive = true 
                                        }
                                    }
                                    navController.currentBackStackEntry?.savedStateHandle?.set("refresh", true)
                                }
                            )
                        }
                        composable(
                            route = BottomBarScreen.Profile.route,
                            arguments = listOf(navArgument("showEditModal") {
                                type = NavType.BoolType
                                defaultValue = false
                            })
                        ) {
                            val showEditModal = it.arguments?.getBoolean("showEditModal") ?: false
                            ProfileScreen(
                                isDarkTheme = isDarkTheme,
                                onThemeChange = { isDarkTheme = it },
                                showEditModal = showEditModal,
                                onModalShown = {
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("showEditModal", false)
                                },
                                onLogout = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}