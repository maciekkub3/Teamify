package com.example.teamify.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class Event (
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: LocalDate,
    val time: LocalTime? = null
)
