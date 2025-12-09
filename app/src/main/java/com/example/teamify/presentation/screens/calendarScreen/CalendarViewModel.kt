package com.example.teamify.presentation.screens.calendarScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamify.data.model.UserInfo
import com.example.teamify.domain.repository.AuthRepository
import com.example.teamify.domain.repository.CalendarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val calendarRepository: CalendarRepository
    ) : ViewModel() {

    private val _state = MutableStateFlow(CalendarUiState())
    val state: StateFlow<CalendarUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            calendarRepository.getEvents().collect { events ->
                val upcomingEvents = events.filter { event ->
                    val eventDateTime = event.time?.let { LocalDateTime.of(event.date, it) }
                        ?: event.date.atStartOfDay()

                    eventDateTime.isAfter(LocalDateTime.now())
                }

                _state.update { it.copy(events = upcomingEvents) }
            }
        }
    }
}
