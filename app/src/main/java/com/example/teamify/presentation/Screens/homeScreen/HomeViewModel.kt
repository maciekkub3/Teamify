package com.example.teamify.presentation.Screens.homeScreen

import androidx.lifecycle.ViewModel
import com.example.teamify.data.firebase.AuthState
import com.example.teamify.data.firebase.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state

    val authState: StateFlow<AuthState> = firebaseAuth.authState

    fun logout() {
        firebaseAuth.signout()
    }
}
