package com.example.teamify.presentation.screens.calendarScreen

import com.example.teamify.domain.model.Event

data class CalendarUiState(
    val events: List<Event> = emptyList(),
)
