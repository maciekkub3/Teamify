package com.example.teamify.presentation.Screens.loginScreen

import com.example.teamify.data.model.AuthState

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val authState: AuthState = AuthState.Unknown
)
