package com.example.teamify.presentation.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamify.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    init {
        viewModelScope.launch {
            authRepository.streamAuthState().collect { authState ->
                Log.d("ViewModel", "Auth State: $authState")
                _state.update { it.copy(authState = authState) }
            }
        }
    }

    fun onEmailChange(newEmail: String) { _state.update { it.copy(email = newEmail) } }
    fun onNameChange(newName: String) { _state.update { it.copy(name = newName) } }
    fun onPasswordChange(newPassword: String) { _state.update { it.copy(password = newPassword) } }

    fun register() {
        viewModelScope.launch {
            authRepository.signUp(
                email = state.value.email,
                password = state.value.password,
                name = state.value.name
            )
        }
    }

    fun login() {
        viewModelScope.launch {
            authRepository.signIn(
                email = state.value.email,
                password = state.value.password
            )
        }
    }
}
