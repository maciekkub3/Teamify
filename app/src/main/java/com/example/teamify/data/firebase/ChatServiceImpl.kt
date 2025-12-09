package com.example.teamify.data.firebase

import com.example.teamify.data.model.exception.AuthException
import com.example.teamify.domain.mapper.toDomain
import com.example.teamify.domain.model.Chat
import com.example.teamify.domain.model.ChatDto
import com.example.teamify.domain.model.Message
import com.example.teamify.domain.model.MessageDto
import com.example.teamify.domain.model.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatService {

    override suspend fun sendMessage(chatId: String, message: String, senderId: String) {
        try {
            val messageData = mapOf(
                "senderId" to senderId,
                "message" to message,
                "timestamp" to FieldValue.serverTimestamp(),
            )
            firestore
                .collection("chats")
                .document(chatId)
                .collection("messages")
                .add(messageData)
                .await()

            val chatRef = firestore.collection("chats").document(chatId)
            chatRef.update(
                mapOf(
                    "lastMessage" to message,
                    "lastMessageTimestamp" to FieldValue.serverTimestamp(),
                    "lastMessageSenderId" to senderId
                )
            ).await()

        } catch (e: Exception) {
            throw AuthException(message = e.message ?: "Failed to send message")
        }
    }

    override suspend fun deleteMessage(chatId: String, messageId: String) {
        try {
            val messageRef = firestore
                .collection("chats")
                .document(chatId)
                .collection("messages")
                .document(messageId)

            messageRef.delete().await()
            println("Message with ID: $messageId deleted successfully.")
        } catch (e: Exception) {
            throw AuthException(message = e.message ?: "Failed to delete message")
        }

    }

    override suspend fun createChatRoom(users: List<String>): String {
        return try {
            val docRef = firestore.collection("chats").document()

            val chatData = mapOf(
                "participants" to users,
                "createdAt" to FieldValue.serverTimestamp(),
                "lastMessage" to "",
            )
            docRef.set(chatData).await()
            docRef.id
        } catch (e: Exception) {
            throw AuthException(message = e.message ?: "Failed to create chat room")
        }

    }

    override suspend fun chatExists(chatId: String): Boolean {
        val doc = firestore.collection("chats").document(chatId).get().await()
        return doc.exists()
    }

    override fun getMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        firestore
            .collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.map { doc ->
                    MessageDto(
                        id = doc.id,
                        senderId = doc.getString("senderId") ?: "",
                        content = doc.getString("message") ?: "",
                        date = doc.getTimestamp("timestamp")
                    ).toDomain()
                } ?: emptyList()
                trySend(messages)
            }
        awaitClose { }
    }

    override suspend fun getUserChats(userId: String): Flow<List<Chat>> = callbackFlow {
        val listener = firestore
            .collection("chats")
            .whereArrayContains("participants", userId)
            .orderBy("lastMessageTimestamp", Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val chats = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ChatDto::class.java)?.toDomain()?.copy(id = doc.id)
                } ?: emptyList()

                trySend(chats)
            }
        awaitClose { listener.remove() }

    }

    override suspend fun getAvailableUsersForChat(currentUserId: String): List<User> {
        try {
            val allUsers = getUsers()
            val userChats = firestore.collection("chats")
                .whereArrayContains("participants", currentUserId)
                .get()
                .await()

            val chattedUserIds = userChats.documents.flatMap { chatDoc ->
                (chatDoc.get("participants") as? List<*>)
                    ?.mapNotNull { it as? String }
                    .orEmpty()
            }.filter { it != currentUserId }

            val availableUsers = allUsers.filter { user ->
                user.uid != currentUserId && user.uid !in chattedUserIds
            }
            return availableUsers
        } catch (e: Exception) {
            throw AuthException(message = e.message ?: "Failed to get available users")
        }
    }

    override suspend fun getUsers(): List<User> {
        try {
            val querySnapshot = firestore
                .collection("users")
                .get()
                .await()
            return querySnapshot.documents.map { document ->
                User(
                    name = document.getString("name") ?: "",
                    email = document.getString("email") ?: "",
                    uid = document.id
                )
            }


        } catch (e: Exception) {
            throw AuthException(message = e.message ?: "Failed to retrieve users")
        }
    }
}
