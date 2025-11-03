package com.example.teamify.presentation.screens.chatScreen

import com.example.teamify.domain.model.Message
import com.example.teamify.domain.model.User

data class ConversationUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val friend: User ? = null,
    val currentMessage: String = ""
)
