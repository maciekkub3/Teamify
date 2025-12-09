package com.example.teamify.presentation.screens.calendarScreen

import com.example.teamify.domain.model.Event
import com.example.teamify.domain.model.EventDto

data class CalendarUiState(
    val events: List<Event> = emptyList(),
)
