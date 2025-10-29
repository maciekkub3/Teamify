package com.example.teamify.presentation.screens.homeScreen

import com.example.teamify.data.model.AuthState
import com.example.teamify.data.model.User

data class HomeUiState(
    val authState: AuthState = AuthState.Unknown,
    val user: User? = null
)

