package com.example.teamify.domain.model

import java.time.LocalDateTime

data class Message(
    val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val content: String = "",
    val date: LocalDateTime? = null,
    val type: MessageType = MessageType.TEXT
)

enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    FILE
}
