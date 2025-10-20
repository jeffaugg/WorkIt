package pies3.workit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.data.Group
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import pies3.workit.ui.components.AppBottomBar
import pies3.workit.ui.theme.WorkItTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            WorkItTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                    AppBottomBar(navController = navController)
                }) { innerPadding ->
                    NavHost(navController = navController, startDestination = "feed", modifier = Modifier.padding(innerPadding)) {
                        composable("feed") { FeedScreen() }
                        composable("groups") { GroupsScreen() }
                        composable("post") { PostScreen() }
                        composable("profile") { ProfileScreen() }
                    }
                }
            }
        }
    }
}



@Composable
fun FeedScreen() {
    Text("Welcome, User")
}

@Composable
fun GroupsScreen() {
    Text("Welcome, User")
}

@Composable
fun PostScreen() {
    Text("Welcome, User")
}

@Composable
fun ProfileScreen() {
    Text("Welcome, User")
}