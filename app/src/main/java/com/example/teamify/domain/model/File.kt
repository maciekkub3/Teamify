package com.example.teamify.domain.model

import java.time.LocalDateTime

data class File(
    val id: String = "",
    val name: String = "",
    val url: String = "",
    val uploadedBy: String = "",
    val uploadedAt: LocalDateTime = LocalDateTime.MIN,
    val size: Long = 0L,
    val type: String = ""
)
