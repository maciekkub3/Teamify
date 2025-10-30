package com.example.teamify.presentation.screens.chatScreen

import com.example.teamify.domain.model.Chat
import com.example.teamify.domain.model.ChatDisplay
import com.example.teamify.domain.model.User

data class ChatUiState(
    val chats : List<ChatDisplay> = emptyList(),
    val friends : List<User> = emptyList()
)
