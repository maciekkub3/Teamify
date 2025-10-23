package com.example.teamify.presentation.Screens.SignupScreen

data class SignupUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
