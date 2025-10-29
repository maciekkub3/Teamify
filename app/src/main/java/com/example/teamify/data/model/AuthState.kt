package com.example.teamify.data.model

sealed class AuthState{
    object Authenticated: AuthState()
    object Unauthenticated: AuthState()
    object Loading : AuthState()
    data class Error(val message: String): AuthState()
    object Unknown: AuthState()
}

enum class UserRole{ ADMIN, WORKER, }

data class User(
    val name: String,
    val email: String,
    val role: UserRole
)
