package com.example.teamify.presentation.screens.announcementScreen

import androidx.lifecycle.ViewModel
import com.example.teamify.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AnnouncementViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(AnnouncementUiState())
    val state: StateFlow<AnnouncementUiState> = _state.asStateFlow()

}
