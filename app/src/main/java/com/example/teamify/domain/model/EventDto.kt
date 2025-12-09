package com.example.teamify.domain.model

import com.google.firebase.Timestamp


data class EventDto(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: Timestamp,
    val time: Timestamp?
)
