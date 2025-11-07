package com.example.teamify.domain.repository

import com.example.teamify.domain.model.Chat
import com.example.teamify.domain.model.ChatDisplay
import com.example.teamify.domain.model.Message
import com.example.teamify.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendMessage(chatId: String, message: String, senderId: String)
    suspend fun deleteMessage(chatId: String, messageId: String)
    suspend fun createChatRoom(members: List<String>): String
    fun getUserChats(userId: String): Flow<List<ChatDisplay>>
    suspend fun getUsers(): List<User>
    suspend fun getAvailableUsersForChat(currentUserId: String): List<User>
    suspend fun chatExists(chatId: String): Boolean
    suspend fun getMessages(chatId: String): Flow<List<Message>>
}
