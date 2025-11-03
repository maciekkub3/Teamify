package com.example.teamify.data.repository

import com.example.teamify.data.firebase.ChatService
import com.example.teamify.domain.model.Chat
import com.example.teamify.domain.model.ChatDisplay
import com.example.teamify.domain.model.User
import com.example.teamify.domain.repository.ChatRepository
import jakarta.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatService: ChatService
) : ChatRepository {

    override suspend fun sendMessage(chatId: String, message: String, senderId: String) {
        chatService.sendMessage(chatId, message, senderId)
    }

    override suspend fun deleteMessage(chatId: String, messageId: String) {
        chatService.deleteMessage(chatId, messageId)
    }

    override suspend fun createChatRoom(members: List<String>) {
        chatService.createChatRoom(members)
    }

    override suspend fun getUserChats(userId: String): List<ChatDisplay> {
        return chatService.getUserChats(userId)
    }

    override suspend fun getUsers(): List<User> {
        return chatService.getUsers()
    }

    override suspend fun getAvailableUsersForChat(currentUserId: String): List<User> {
        return chatService.getAvailableUsersForChat(currentUserId)
    }
}
