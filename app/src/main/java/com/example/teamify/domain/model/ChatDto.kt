package com.example.teamify.domain.model

import com.google.firebase.Timestamp

data class ChatDto(
    val id: String = "",
    val name: String = "",
    val lastMessage: String = "",
    val lastMessageTimestamp: Timestamp? = null,
    val lastMessageSenderId: String = "",
    val participants: List<String> = emptyList(),
    val createdAt: Timestamp? = null
)
