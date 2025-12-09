package com.example.teamify.domain.model

import com.google.firebase.Timestamp

data class AnnouncementDto(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val date: Timestamp? = null,
    val priority: String = "",
)
