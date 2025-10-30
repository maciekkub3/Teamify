package com.example.teamify.domain.repository

import com.example.teamify.domain.model.Chat
import com.example.teamify.domain.model.ChatDisplay
import com.example.teamify.domain.model.User

interface ChatRepository {
    suspend fun sendMessage(chatId: String, message: String, senderId: String)
    suspend fun deleteMessage(chatId: String, messageId: String)
    suspend fun createChatRoom(members: List<String>)
    suspend fun getUserChats(userId: String): List<ChatDisplay>
    suspend fun getUsers(): List<User>
}
