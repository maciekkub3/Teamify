package com.example.teamify.presentation.screens.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamify.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state

    init {
        viewModelScope.launch {
            authRepository.streamAuthState().collect { authState ->
                _state.update {
                    it.copy(authState = authState)
                }
            }
        }
        viewModelScope.launch {
            _state.update {
                it.copy(user = authRepository.getUser())
            }
        }
    }

    fun logout() {
        authRepository.signOut()
    }
}
