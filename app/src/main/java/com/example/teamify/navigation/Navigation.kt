package com.example.teamify.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.teamify.presentation.screens.loginScreen.LoginScreen
import androidx.compose.runtime.getValue
import androidx.navigation.toRoute
import com.example.teamify.data.model.AuthState
import com.example.teamify.domain.model.User
import com.example.teamify.presentation.screens.AuthViewModel
import com.example.teamify.presentation.screens.announcementScreen.AnnouncementScreen
import com.example.teamify.presentation.screens.announcementScreen.AnnouncementViewModel
import com.example.teamify.presentation.screens.calendarScreen.CalendarScreen
import com.example.teamify.presentation.screens.calendarScreen.CalendarViewModel
import com.example.teamify.presentation.screens.chatScreen.ChatScreen
import com.example.teamify.presentation.screens.chatScreen.ChatViewModel
import com.example.teamify.presentation.screens.chatScreen.ConversationScreen
import com.example.teamify.presentation.screens.chatScreen.ConversationViewModel
import com.example.teamify.presentation.screens.homeScreen.HomeScreen
import com.example.teamify.presentation.screens.homeScreen.HomeViewModel
import com.example.teamify.presentation.screens.signupScreen.SignupScreen


@Composable
fun Navigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = LoginRoute
    ) {
        composable<HomeRoute> {
            val viewModel: HomeViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            HomeScreen(
                state = state,
                onLogoutClick = { viewModel.logout() },
                onChatClick = { navController.navigate(ChatRoute) },
                onCalendarClick = { navController.navigate(CalendarRoute) },
                onAnnouncementClick = { navController.navigate(AnnouncementRoute) }
            )
            LaunchedEffect(state.authState) {
                if (state.authState == AuthState.Unauthenticated) {
                    navController.navigate(LoginRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            }
        }
        composable<LoginRoute> {
            val viewModel: AuthViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            LoginScreen(
                state = state,
                onEmailChange = { viewModel.onEmailChange(it) },
                onPasswordChange = { viewModel.onPasswordChange(it) },
                onLoginClick = { viewModel.login() },
                onRegisterClick = {
                    navController.navigate(SignupRoute) {
                        popUpTo(SignupRoute) { inclusive = true }
                    }
                }
            )
            LaunchedEffect(state.authState) {
                if (state.authState is AuthState.Authenticated) {
                    navController.navigate(HomeRoute) {
                        popUpTo(HomeRoute) { inclusive = true }
                    }
                }
            }
        }
        composable<SignupRoute> {
            val viewModel: AuthViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            SignupScreen(
                state = state,
                onEmailChange = { viewModel.onEmailChange(it) },
                onPasswordChange = { viewModel.onPasswordChange(it) },
                onRegisterClick = { viewModel.register() },
                onLoginClick = {
                    navController.navigate(LoginRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                },
                onNameChange = { viewModel.onNameChange(it) }
            )
            LaunchedEffect(state.authState) {
                if (state.authState is AuthState.Authenticated) {
                    navController.navigate(HomeRoute) {
                        popUpTo(HomeRoute) { inclusive = true }
                    }
                }
            }
        }
        composable<ChatRoute> {
            val viewModel: ChatViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val currentUserId = viewModel.getCurrentUserId()
            ChatScreen(
                state = state,
                onFriendClick = { friend ->
                    navController.navigate(ConversationRoute(friendId = friend.uid))
                },
                onFriendChatClick = { chat ->
                    navController.navigate(
                        ConversationRoute(
                            chatId = chat.id,
                            friendId = chat.participants.first { it != currentUserId }
                        ))
                }
            )
        }
        composable<CalendarRoute> {
            val viewModel: CalendarViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            CalendarScreen(
                state = state
            )
        }
        composable<AnnouncementRoute> {
            val viewModel: AnnouncementViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            AnnouncementScreen(
                state = state,
                onAddAnnouncement = { title, content ->
                    viewModel.addAnnouncement(title, content)
                },
            )
        }
        composable<ConversationRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ConversationRoute>()
            val friendId = route.friendId ?: ""

            val viewModel: ConversationViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            ConversationScreen(
                state = state,
                onMessageChange = { viewModel.onMessageChange(it) },
                onSendClick = { viewModel.sendMessage(friendId) }
            )
        }
    }
}

