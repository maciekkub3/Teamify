package com.example.teamify.presentation.screens.announcementScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamify.domain.repository.AnnouncementRepository
import com.example.teamify.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnnouncementViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AnnouncementUiState())
    val state: StateFlow<AnnouncementUiState> = _state.asStateFlow()


    init {
        viewModelScope.launch {
            _state.update {
                it.copy(userRole = authRepository.getUser().role)
            }

            announcementRepository.getAnnouncements().collect { announcements ->
                _state.update {
                    it.copy(announcements = announcements)
                }
            }
        }
    }

    fun addAnnouncement(title: String, content: String) {
        viewModelScope.launch {
            announcementRepository.postAnnouncement(title, content)
        }
    }

    fun deleteAnnouncement(announcementId: String) {
        viewModelScope.launch {
            announcementRepository.deleteAnnouncement(announcementId)
        }
    }


}
