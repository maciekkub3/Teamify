package com.example.teamify.presentation.Screens.loginScreen

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
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state

    init {
        viewModelScope.launch {
            authRepository.streamAuthState().collect { authState ->
                Log.d("LoginViewModel", "Auth State: $authState")
                _state.update {
                    it.copy(authState = authState)
                }
            }
        }
    }

    fun onEmailChange(newEmail: String) { _state.update { it.copy(email = newEmail) } }
    fun onPasswordChange(newPassword: String) { _state.update { it.copy(password = newPassword) } }

    fun login() {
        viewModelScope.launch {
            authRepository.signIn(
                email = state.value.email,
                password = state.value.password
            )
        }
    }
}
