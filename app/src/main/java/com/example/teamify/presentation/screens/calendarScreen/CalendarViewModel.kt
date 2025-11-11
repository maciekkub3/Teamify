package com.example.teamify.presentation.screens.calendarScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamify.data.model.UserInfo
import com.example.teamify.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    ) : ViewModel() {

    private val _state = MutableStateFlow(CalendarUiState())
    val state: StateFlow<CalendarUiState> = _state.asStateFlow()


    init {
        viewModelScope.launch {
            _state.update {
                it.copy(user = authRepository.getUser())
            }
        }
    }
}
