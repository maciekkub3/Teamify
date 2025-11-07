package com.example.teamify.domain.model

import com.google.firebase.Timestamp

data class Chat (
    val id: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageTimestamp: Timestamp? = null,
)

data class Message(
    val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val content: String = "",
    val timestamp: Timestamp? = null,
    val type: MessageType = MessageType.TEXT
)

enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    FILE
}
