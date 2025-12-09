package com.example.teamify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.teamify.navigation.AnnouncementRoute
import com.example.teamify.navigation.CalendarRoute
import com.example.teamify.navigation.ChatRoute
import com.example.teamify.navigation.HomeRoute
import com.example.teamify.navigation.Navigation
import com.example.teamify.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.example.teamify.navigation.FileRoute
import com.example.teamify.presentation.common.BottomNavigationBar

val bottomNavRoutes = listOf(
    HomeRoute,
    ChatRoute,
    AnnouncementRoute,
    CalendarRoute,
    FileRoute
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route?.substringAfterLast(".")
            val showBottomBar = currentRoute in bottomNavRoutes.map { it.route }

            WindowCompat.setDecorFitsSystemWindows(window, false)
            AppTheme {
                Surface(tonalElevation = 5.dp) {

                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize(),
                        bottomBar = {
                            if (showBottomBar) {
                                BottomNavigationBar(
                                    currentRoute = currentRoute,

                                    onChatClick = {
                                        navController.navigate(ChatRoute) {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    },
                                    onCalendarClick = {
                                        navController.navigate(CalendarRoute) {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    },
                                    onAnnouncementClick = {
                                        navController.navigate(AnnouncementRoute) {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    },
                                    onHomeClick = {
                                        navController.navigate(HomeRoute) {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    },
                                    onFilesClick = {
                                        navController.navigate(FileRoute) {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    },
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            Navigation(navController = navController)
                        }
                    }

                }
            }
        }
    }
}
