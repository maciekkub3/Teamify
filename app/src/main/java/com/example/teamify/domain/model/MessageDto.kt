package com.example.teamify.domain.model

import com.google.firebase.Timestamp


data class MessageDto(
    val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val content: String = "",
    val date: Timestamp? = Timestamp.now(),
    val type: MessageType = MessageType.TEXT
)
