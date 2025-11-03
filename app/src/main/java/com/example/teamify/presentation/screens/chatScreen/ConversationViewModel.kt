package com.example.teamify.presentation.screens.chatScreen

import android.R.id.message
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class ConversationViewModel @Inject constructor(

): ViewModel() {

    private val _state = MutableStateFlow(ConversationUiState())
    val state: StateFlow<ConversationUiState> = _state

    fun onMessageChange(message: String) { _state.update { it.copy(currentMessage = message) } }


}
