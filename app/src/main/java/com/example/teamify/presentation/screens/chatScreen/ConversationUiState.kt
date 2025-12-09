package com.example.teamify.presentation.screens.chatScreen

import com.example.teamify.data.model.User
import com.example.teamify.data.model.UserRole
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime

data class ConversationUiState(
    val messages: List<UiMessage> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentMessage: String = "",
    val friend: User = User(id = "", name = "", email = "", profileImageUrl = null, role = UserRole.WORKER)
)

data class UiMessage(
    val id: String,
    val senderName: String?,
    val content: String,
    val date: LocalDateTime?,
    val isCurrentUser: Boolean
)
