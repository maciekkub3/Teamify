package com.example.teamify.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.teamify.presentation.Screens.SignupScreen.SignupScreen
import com.example.teamify.presentation.Screens.SignupScreen.SignupViewModel
import com.example.teamify.presentation.Screens.loginScreen.LoginScreen
import com.example.teamify.presentation.Screens.loginScreen.LoginViewModel
import androidx.compose.runtime.getValue
import com.example.teamify.data.model.AuthState
import com.example.teamify.presentation.Screens.homeScreen.HomeScreen
import com.example.teamify.presentation.Screens.homeScreen.HomeViewModel


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
                onLogoutClick = { viewModel.logout() }
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
            val viewModel : LoginViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            LoginScreen(
                state = state,
                onEmailChange = { viewModel.onEmailChange(it) },
                onPasswordChange = { viewModel.onPasswordChange(it) },
                onLoginClick = { viewModel.login() },
                onRegisterClick = { navController.navigate(SignupRoute) {
                    popUpTo(SignupRoute) { inclusive = true }
                } }
            )
            LaunchedEffect(state.authState) {
                if (state.authState == AuthState.Authenticated) {
                    navController.navigate(HomeRoute) {
                        popUpTo(HomeRoute) { inclusive = true }
                    }
                }
            }
        }
        composable<SignupRoute> {
            val viewModel : SignupViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            SignupScreen(
                state = state,
                onEmailChange = { viewModel.onEmailChange(it) },
                onPasswordChange = { viewModel.onPasswordChange(it) },
                onRegisterClick = { viewModel.register() },
                onLoginClick = { navController.navigate(LoginRoute) {
                    popUpTo(LoginRoute) { inclusive = true }
                } },
                onNameChange = { viewModel.onNameChange(it) }
            )
            LaunchedEffect(state.authState) {
                if (state.authState == AuthState.Authenticated) {
                    navController.navigate(HomeRoute) {
                        popUpTo(HomeRoute) { inclusive = true }
                    }
                }
            }
        }
    }
}

