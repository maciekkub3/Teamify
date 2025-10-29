package com.example.teamify.presentation.screens

import com.example.teamify.data.model.AuthState

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val authState: AuthState = AuthState.Unknown
)
