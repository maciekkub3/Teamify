package com.example.teamify.domain.mapper

import com.example.teamify.domain.model.Announcement
import com.example.teamify.domain.model.AnnouncementDto
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun AnnouncementDto.toDomain(): Announcement = Announcement(
    id = id,
    title = title,
    content = content,
    date = date?.toDate()?.let {
        Instant.ofEpochMilli(it.time)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    } ?: LocalDate.MIN,
    priority = priority
)
