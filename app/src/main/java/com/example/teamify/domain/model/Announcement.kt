package com.example.teamify.domain.model

import com.google.firebase.Timestamp
import java.time.LocalDate

data class Announcement(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val date: LocalDate = LocalDate.MIN,
    val priority: String = ""
)
