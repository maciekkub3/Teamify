package com.example.teamify.domain.repository

import com.example.teamify.domain.model.Chat
import com.example.teamify.domain.model.ChatDto
import com.example.teamify.domain.model.ChatWithUserInfo
import com.example.teamify.domain.model.Message
import com.example.teamify.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendMessage(chatId: String, message: String, senderId: String)
    suspend fun deleteMessage(chatId: String, messageId: String)
    suspend fun createChatRoom(members: List<String>): String
    suspend fun getUserChats(userId: String): Flow<List<ChatWithUserInfo>>
    suspend fun getUsers(): List<User>
    suspend fun getAvailableUsersForChat(currentUserId: String): List<User>
    suspend fun chatExists(chatId: String): Boolean
    suspend fun getMessages(chatId: String): Flow<List<Message>>
}
