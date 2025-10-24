package com.example.teamify.data.repository

import com.example.teamify.data.firebase.AuthService
import com.example.teamify.data.model.AuthState
import com.example.teamify.data.model.exception.AuthException
import com.example.teamify.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService
): AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unknown)

    override fun streamAuthState(): Flow<AuthState> = _authState.asStateFlow()

    override suspend fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password must not be empty")
            return
        }

        _authState.value = AuthState.Loading

        try {
            authService.signIn(email,password)
            _authState.value = AuthState.Authenticated
        } catch (e: AuthException) {
            _authState.value = AuthState.Error(e.message)
        }
    }

    override suspend fun signUp(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password must not be empty")
            return
        }

        _authState.value = AuthState.Loading

        try {
            authService.signUp(email,password)
            _authState.value = AuthState.Authenticated
        } catch (e: AuthException) {
            _authState.value = AuthState.Error(e.message)
        }
    }

    override fun signOut() {
        authService.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}
