package com.example.teamify.presentation.screens.calendarScreen

import java.time.LocalDate
import java.time.LocalDateTime

data class AddEventUiState (
    val eventTitle: String = "",
    val eventDescription: String = "",
    val eventDate: LocalDateTime = LocalDateTime.now(),
    val eventTime: LocalDateTime? = null,
    val validationError: String? = null
)

sealed class AddEventUiEvent {
    data class ShowError(val message: String) : AddEventUiEvent()
    data object Success : AddEventUiEvent()
}
