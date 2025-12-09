package com.example.teamify.domain.mapper

import com.example.teamify.domain.model.Chat
import com.example.teamify.domain.model.ChatDto


fun ChatDto.toDomain(): Chat = Chat(
    id = id,
    name = name,
    lastMessage = lastMessage,
    lastMessageTimestamp = lastMessageTimestamp?.toLocalDateTime(),
    lastMessageSenderId = lastMessageSenderId,
    participants = participants,
)

