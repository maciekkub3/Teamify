package com.example.teamify.data.model

sealed class AuthState{
    object Authenticated: AuthState()
    object Unauthenticated: AuthState()
    object Loading : AuthState()
    data class Error(val message: String): AuthState()
    object Unknown: AuthState()
}
