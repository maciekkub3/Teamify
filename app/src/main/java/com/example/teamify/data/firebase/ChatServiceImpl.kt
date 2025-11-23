package com.example.teamify.data.firebase

import com.example.teamify.data.model.exception.AuthException
import com.example.teamify.domain.model.Chat
import com.example.teamify.domain.model.ChatDisplay
import com.example.teamify.domain.model.Message
import com.example.teamify.domain.model.User
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): ChatService {

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
                    "lastMessageTimestamp" to FieldValue.serverTimestamp()
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
                "lastMessageTimestamp" to FieldValue.serverTimestamp()
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
                    Message(
                        id = doc.id,
                        senderId = doc.getString("senderId") ?: "",
                        content = doc.getString("message") ?: "",
                        timestamp = doc.getTimestamp("timestamp")
                    )
                } ?: emptyList()
                trySend(messages)
            }
        awaitClose {  }
    }

    override fun getUserChats(userId: String): Flow<List<ChatDisplay>> = callbackFlow {
        val listener = firestore
            .collection("chats")
            .whereArrayContains("participants", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                launch {
                    try {
                        val chats = snapshot?.documents?.mapNotNull { chatDoc ->
                            chatDoc.toObject(Chat::class.java)?.copy(id = chatDoc.id)
                        } ?: emptyList()

                        val allOtherUserIds = chats.flatMap { it.participants }
                            .filter { it.isNotBlank() && it != userId }
                            .distinct()

                        val userMap = if (allOtherUserIds.isNotEmpty()) {
                            val userDocs = firestore.collection("users")
                                .whereIn(FieldPath.documentId(), allOtherUserIds)
                                .get()
                                .await()

                            userDocs.documents.associateBy(
                                { it.id },
                                { it.getString("name") ?: "Unknown" }
                            )
                        } else {
                            emptyMap()
                        }
                        val chatDisplays = chats.map { chat ->
                            val otherNames = chat.participants
                                .filter { it != userId }
                                .map { uid -> userMap[uid] ?: "Unknown" }

                            ChatDisplay(
                                id = chat.id,
                                name = otherNames.joinToString(", "),
                                lastMessage = chat.lastMessage,
                                lastMessageTimestamp = chat.lastMessageTimestamp,
                                participants = chat.participants
                            )
                        }

                        trySend(chatDisplays)
                    } catch (e: Exception) {
                        close(e)
                    }
                }
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
