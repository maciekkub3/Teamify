package com.example.teamify.domain.mapper

import com.example.teamify.domain.model.Message
import com.example.teamify.domain.model.MessageDto

fun MessageDto.toDomain(): Message = Message(
    id = id,
    chatId = chatId,
    senderId = senderId,
    content = content,
    type = type,
    date = date?.toLocalDateTime()
)

