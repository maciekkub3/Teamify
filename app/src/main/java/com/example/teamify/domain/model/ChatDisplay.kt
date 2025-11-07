package com.example.teamify.domain.model

import com.google.firebase.Timestamp

data class ChatDisplay(
    val id: String = "",
    val name: String = "",
    val lastMessage: String = "",
    val lastMessageTimestamp: Timestamp? = null,
    val participants: List<String> = emptyList()
)
