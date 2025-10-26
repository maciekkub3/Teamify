package com.example.teamify.presentation.screens.signupScreen

import com.example.teamify.data.model.AuthState

data class SignupUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val authState: AuthState = AuthState.Unknown
)
