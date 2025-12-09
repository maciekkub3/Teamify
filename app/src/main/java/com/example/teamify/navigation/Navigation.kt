package com.example.teamify.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.teamify.presentation.screens.loginScreen.LoginScreen
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.toRoute
import com.example.teamify.data.model.AuthState
import com.example.teamify.presentation.screens.AuthViewModel
import com.example.teamify.presentation.screens.FileScreen.FileScreen
import com.example.teamify.presentation.screens.FileScreen.FileViewModel
import com.example.teamify.presentation.screens.announcementScreen.AnnouncementScreen
import com.example.teamify.presentation.screens.announcementScreen.AnnouncementViewModel
import com.example.teamify.presentation.screens.calendarScreen.AddEventScreen
import com.example.teamify.presentation.screens.calendarScreen.AddEventViewModel
import com.example.teamify.presentation.screens.calendarScreen.CalendarScreen
import com.example.teamify.presentation.screens.calendarScreen.CalendarViewModel
import com.example.teamify.presentation.screens.chatScreen.ChatScreen
import com.example.teamify.presentation.screens.chatScreen.ChatViewModel
import com.example.teamify.presentation.screens.chatScreen.ConversationScreen
import com.example.teamify.presentation.screens.chatScreen.ConversationViewModel
import com.example.teamify.presentation.screens.homeScreen.HomeScreen
import com.example.teamify.presentation.screens.homeScreen.HomeViewModel
import com.example.teamify.presentation.screens.signupScreen.SignupScreen
import java.time.LocalDate


@Composable
fun Navigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        composable<HomeRoute> {
            val viewModel: HomeViewModel = hiltViewModel()
            val announcementViewModel: AnnouncementViewModel = hiltViewModel()
            val fileViewModel: FileViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            HomeScreen(
                state = state,
                onLogoutClick = { viewModel.logout() },
                onAddAnnouncement = { title, content, priority ->
                    announcementViewModel.addAnnouncement(title, content, priority)
                },
                onAddEvent = { navController.navigate(AddEventRoute(date = LocalDate.now().toString())) },
                onUploadFile = { fileViewModel.uploadFile(it) },
                onSeeAllClick = { navController.navigate(CalendarRoute) },
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
                onNameChange = { viewModel.onNameChange(it) },
                onUriChange = { viewModel.onImageUriChange(it) },
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
                },
            )
        }
        composable<CalendarRoute> {
            val viewModel: CalendarViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            CalendarScreen(
                state = state,
                onDayClick = { date ->
                    navController.navigate(AddEventRoute(date = date.toString()))
                },

            )
        }
        composable<AnnouncementRoute> {
            val viewModel: AnnouncementViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            AnnouncementScreen(
                state = state,
                onAddAnnouncement = { title, content, priority ->
                    viewModel.addAnnouncement(title, content, priority)
                },
                onRemove = { viewModel.deleteAnnouncement(it.id) }
            )
        }
        composable<ConversationRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ConversationRoute>()
            val viewModel = hiltViewModel<ConversationViewModel, ConversationViewModel.Factory> {
                it.create(
                    chatId = route.chatId ?: "",
                    friendId = route.friendId ?: ""
                )
            }

            val state by viewModel.state.collectAsStateWithLifecycle()
            ConversationScreen(
                state = state,
                onMessageChange = { viewModel.onMessageChange(it) },
                onSendClick = { viewModel.sendMessage() },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable<AddEventRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<AddEventRoute>()
            val viewModel = hiltViewModel<AddEventViewModel, AddEventViewModel.Factory> {
                it.create(
                    day = route.date?.let { LocalDate.parse(it) } ?: LocalDate.now()
                )
            }
            val state by viewModel.state.collectAsStateWithLifecycle()
            val event by viewModel.event.collectAsStateWithLifecycle(initialValue = null)
            AddEventScreen(
                state = state,
                event = event,
                onSave = {
                    viewModel.addEvent(
                        eventTitle = state.eventTitle,
                        eventDescription = state.eventDescription,
                        eventDate = state.eventDate,
                        eventTime = state.eventTime
                    )
                    navController.popBackStack()
                },
                onTitleChange = { viewModel.updateEventTitle(it) },
                onDescriptionChange = { viewModel.updateEventDescription(it) },
                onDateSelected = { viewModel.updateEventDate(it) },
                onTimeSelected = { viewModel.updateEventTime(it) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<FileRoute> {
            val viewModel: FileViewModel = hiltViewModel()
            val context = LocalContext.current
            val state by viewModel.state.collectAsStateWithLifecycle()
            FileScreen(
                state = state,
                onUploadFile = { viewModel.uploadFile(it) },
                onDeleteFile = { viewModel.deleteFile(it) },
                onFileClick = { viewModel.openFile(context = context, url = it) }
            )
        }
    }
}
