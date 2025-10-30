package com.example.teamify.data.firebase

import android.util.Log.e
import com.example.teamify.data.model.exception.AuthException
import com.example.teamify.domain.model.Chat
import com.example.teamify.domain.model.ChatDisplay
import com.example.teamify.domain.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): ChatService {
    override suspend fun sendMessage(chatId: String, message: String, senderId: String) {
        try {
            val messagesCollection = firestore
                .collection("chats")
                .document(chatId)
                .collection("messages")

            val messageData = mapOf(
                "senderId" to senderId,
                "message" to message,
                "timestamp" to FieldValue.serverTimestamp()
            )
            val messageRef = messagesCollection.add(messageData).await()

            firestore.collection("chats")
                .document(chatId)
                .update(
                    mapOf(
                        "lastMessage" to message,
                        "lastMessageTimestamp" to FieldValue.serverTimestamp()
                    )
                ).await()
            println("Message sent with ID: ${messageRef.id}")

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

    override suspend fun createChatRoom(members: List<String>){
        try {
            val chatData = mapOf(
                "participants" to members,
                "createdAt" to FieldValue.serverTimestamp(),
                "lastMessage" to "",
                "lastMessageTimestamp" to FieldValue.serverTimestamp()
            )
            val chatRef = firestore
                .collection("chats")
                .add(chatData)
                .await()
            println("Chat room created with ID: ${chatRef.id}")
        } catch (e: Exception) {
            throw AuthException(message = e.message ?: "Failed to create chat room")
        }

    }

    override suspend fun getUserChats(userId: String): List<ChatDisplay> {
        try {
            val querySnapshot = firestore
                .collection("chats")
                .whereArrayContains("participants", userId)
                .get()
                .await()

            val chats = querySnapshot.documents.mapNotNull { chatDoc ->
                val chat = chatDoc.toObject(Chat::class.java)?.copy(id = chatDoc.id)
                chat
            }
            val currentUserId = userId

            return chats.map { chat ->
                val otherUserIds = chat.participants.filter { it != currentUserId }

                // Load other users’ names
                val otherUsers = otherUserIds.mapNotNull { uid ->
                    val userDoc = firestore.collection("users").document(uid).get().await()
                    userDoc.getString("name")
                }

                val chatName = otherUsers.joinToString(", ")

                ChatDisplay(
                    id = chat.id,
                    name = chatName,
                    lastMessage = chat.lastMessage,
                    lastMessageTimestamp = chat.lastMessageTimestamp
                )
            }
        } catch (e: Exception) {
            throw AuthException(message = e.message ?: "Failed to retrieve user chats")
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

