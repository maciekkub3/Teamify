package com.example.teamify.domain.mapper

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId

fun Timestamp.toLocalDateTime(): LocalDateTime =
    this.toDate().toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

fun LocalDateTime.toTimestamp(): Timestamp =
    Timestamp(this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000, 0)
