package com.example.teamify.presentation.screens.calendarScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamify.domain.repository.CalendarRepository
import com.google.firebase.Timestamp
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

@HiltViewModel(assistedFactory = AddEventViewModel.Factory::class)
class AddEventViewModel @AssistedInject constructor(
    private val calendarRepository: CalendarRepository,
    @Assisted("day") private val day: LocalDate? = null,
) : ViewModel() {

    private val _state = MutableStateFlow(AddEventUiState())
    val state: StateFlow<AddEventUiState> = _state.asStateFlow()

    private val _event = MutableSharedFlow<AddEventUiEvent>()
    val event = _event.asSharedFlow()

    init {
        val date = (day ?: LocalDate.now()).atStartOfDay()
        _state.update { it.copy(eventDate = date) }
    }

    fun addEvent(
        eventTitle: String,
        eventDescription: String,
        eventDate: LocalDateTime,
        eventTime: LocalDateTime?
    ) {
        val combinedDateTime = eventTime?.let {
            LocalDateTime.of(eventDate.toLocalDate(), it.toLocalTime())
        } ?: eventDate

        if (combinedDateTime.isBefore(LocalDateTime.now())) {
            viewModelScope.launch {
                _event.emit(AddEventUiEvent.ShowError("Event date cannot be in the past"))
            }
            return
        }
        viewModelScope.launch {
            val date = Timestamp(
                Date.from(combinedDateTime.atZone(ZoneId.systemDefault()).toInstant())
            )
            val time = eventTime?.let {
                Timestamp(Date.from(it.atZone(ZoneId.systemDefault()).toInstant()))
            }
            calendarRepository.postEvent(
                title = eventTitle,
                description = eventDescription,
                date = date,
                time = time
            )

        }
    }

    fun updateEventTitle(title: String) {
        _state.update { it.copy(eventTitle = title) }
    }
    fun updateEventDescription(description: String) {
        _state.update { it.copy(eventDescription = description) }
    }
    fun updateEventDate(date: LocalDateTime) {
        _state.update { it.copy(eventDate = date) }
    }
    fun updateEventTime(time: LocalDateTime?) {
        _state.update { it.copy(eventTime = time) }
    }



    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("day") day: LocalDate? = null,
        ): AddEventViewModel
    }
}

