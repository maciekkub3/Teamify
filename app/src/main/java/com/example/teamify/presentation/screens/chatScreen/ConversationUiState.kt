package com.example.teamify.presentation.screens.chatScreen

import com.example.teamify.domain.model.Message
import com.example.teamify.domain.model.User
import com.google.firebase.Timestamp

data class ConversationUiState(
    val messages: List<UiMessage> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentMessage: String = "",
    val friendName: String? = null
)

data class UiMessage(
    val id: String,
    val senderName: String?,
    val content: String,
    val timestamp: Timestamp?
)
