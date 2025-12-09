package com.example.teamify.presentation.screens.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamify.domain.repository.AnnouncementRepository
import com.example.teamify.domain.repository.AuthRepository
import com.example.teamify.domain.repository.CalendarRepository
import com.example.teamify.domain.repository.FileRepository
import com.example.teamify.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val calendarRepository: CalendarRepository,
    private val announcementRepository: AnnouncementRepository,
    private val userRepository: UserRepository,
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state

    init {
        viewModelScope.launch {
            val user = authRepository.getUser()
            _state.update { it.copy(user = user) }

            val profileUrl = userRepository.getUserImageUrl(user.id)
            if (profileUrl != null) {
                _state.update {
                    it.copy(user = it.user?.copy(profileImageUrl = profileUrl))
                }
            }
        }
        viewModelScope.launch {
            authRepository.streamAuthState().collect { authState ->
                _state.update {
                    it.copy(authState = authState)
                }
            }
        }
        viewModelScope.launch {
            calendarRepository.getEvents().collect { events ->
                val now = LocalDateTime.now()

                val upcoming = events
                    .map { event ->
                        val dateTime = if (event.time != null)
                            LocalDateTime.of(event.date, event.time)
                        else
                            LocalDateTime.of(event.date, LocalTime.MIN)

                        event to dateTime
                    }
                    .filter { (_, dateTime) -> dateTime >= now }
                    .minByOrNull { (_, dateTime) -> dateTime }
                    ?.first

                _state.update { it.copy(lastEvent = upcoming) }
            }
        }
        viewModelScope.launch {
            announcementRepository.getAnnouncements().collect { announcement ->
                _state.update {
                    it.copy(lastAnnouncements = announcement.take(2))
                }
            }
        }

        viewModelScope.launch {
            fileRepository.getAllFiles().collect { files ->
                _state.update {
                    it.copy(lastFiles =  files.take(3))
                }
            }
        }
    }

    fun logout() {
        authRepository.signOut()
    }


}
