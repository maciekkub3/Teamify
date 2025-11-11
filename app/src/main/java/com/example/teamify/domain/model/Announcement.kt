package com.example.teamify.domain.model

import com.google.firebase.Timestamp

data class Announcement(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val timestamp: Timestamp? = null
)
