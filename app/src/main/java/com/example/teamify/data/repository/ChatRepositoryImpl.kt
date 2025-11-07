package com.example.teamify.data.repository

import com.example.teamify.data.firebase.ChatService
import com.example.teamify.domain.model.ChatDisplay
import com.example.teamify.domain.model.Message
import com.example.teamify.domain.model.User
import com.example.teamify.domain.repository.ChatRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ChatRepositoryImpl @Inject constructor(
    private val chatService: ChatService
) : ChatRepository {

    override suspend fun sendMessage(chatId: String, message: String, senderId: String) {
        chatService.sendMessage(chatId, message, senderId)
    }

    override suspend fun deleteMessage(chatId: String, messageId: String) {
        chatService.deleteMessage(chatId, messageId)
    }

    override suspend fun createChatRoom(members: List<String>): String {
        return chatService.createChatRoom(members)
    }

    override fun getUserChats(userId: String): Flow<List<ChatDisplay>> {
        return chatService.getUserChats(userId)
    }

    override suspend fun getUsers(): List<User> {
        return chatService.getUsers()
    }

    override suspend fun getAvailableUsersForChat(currentUserId: String): List<User> {
        return chatService.getAvailableUsersForChat(currentUserId)
    }

    override suspend fun chatExists(chatId: String): Boolean {
        return chatService.chatExists(chatId)
    }

    override suspend fun getMessages(chatId: String): Flow<List<Message>> {
        return chatService.getMessages(chatId)
    }
}
