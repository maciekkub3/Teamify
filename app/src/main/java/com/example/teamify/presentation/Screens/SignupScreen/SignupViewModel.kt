package com.example.teamify.presentation.Screens.SignupScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamify.data.firebase.AuthState
import com.example.teamify.data.firebase.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): ViewModel() {

    private val _state = MutableStateFlow(SignupUiState())
    val state: StateFlow<SignupUiState> = _state

    val authState: StateFlow<AuthState> = firebaseAuth.authState

    fun onEmailChange(newEmail: String) { _state.update { it.copy(email = newEmail) } }
    fun onPasswordChange(newPassword: String) { _state.update { it.copy(password = newPassword) } }

    fun register() {
        viewModelScope.launch {
            firebaseAuth.signup(
                email = state.value.email,
                password = state.value.password
            )

            authState.collect { authState ->
                Log.d("SignupViewModel", "Auth State: $authState")
            }
        }
    }
}
