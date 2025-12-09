package com.example.teamify.domain.mapper

import com.example.teamify.domain.model.EventDto
import com.example.teamify.domain.model.Event
import java.time.Instant
import java.time.ZoneId

fun EventDto.toDomain(): Event = Event (
    id = id,
    title = title,
    description = description,
    date = Instant.ofEpochSecond(date.seconds).atZone(ZoneId.systemDefault()).toLocalDate(),
    time = time?.let {
        Instant.ofEpochSecond(it.seconds).atZone(ZoneId.systemDefault()).toLocalTime()
    }
)
