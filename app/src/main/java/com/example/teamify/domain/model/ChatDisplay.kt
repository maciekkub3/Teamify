package com.example.teamify.domain.model

data class ChatDisplay(
    val id: String = "",
    val name: String = "",
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = 0L
)
