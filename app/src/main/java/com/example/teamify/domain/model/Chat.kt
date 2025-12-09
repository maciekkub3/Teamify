package com.example.teamify.domain.model

import java.time.LocalDateTime

data class Chat(
    val id: String = "",
    val name: String = "",
    val lastMessage: String = "",
    val lastMessageTimestamp: LocalDateTime? = null,
    val lastMessageSenderId: String = "",
    val participants: List<String> = emptyList(),
    val createdAt: LocalDateTime? = null
)

data class ChatWithUserInfo(
    val chat: Chat,
    val otherUserName: String,
    val otherUserPhotoUrl: String?
)
