package com.example.teamify.presentation.Screens.homeScreen

import com.example.teamify.data.model.AuthState

data class HomeUiState(
    val authState: AuthState = AuthState.Unknown
)
