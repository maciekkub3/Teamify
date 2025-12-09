package com.example.teamify.presentation.screens

import android.net.Uri
import com.example.teamify.data.model.AuthState

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val selectedImageUri: Uri? = null,
    val authState: AuthState = AuthState.Unknown
)
