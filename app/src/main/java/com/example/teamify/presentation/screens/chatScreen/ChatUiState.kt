package com.example.teamify.presentation.screens.chatScreen

import com.example.teamify.domain.model.ChatWithUserInfo
import com.example.teamify.domain.model.User

data class ChatUiState(
    val chats : List<ChatWithUserInfo> = emptyList(),
    val friends : List<User> = emptyList(),
    val selectedFriends : Set<String> = emptySet()
)
