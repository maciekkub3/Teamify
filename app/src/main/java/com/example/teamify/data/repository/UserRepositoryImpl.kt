package com.example.teamify.data.repository

import com.example.teamify.domain.repository.UserRepository
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): UserRepository {
    override suspend fun getUserImageUrl(userId: String): String? {
        if (userId.isBlank()) return null // prevent invalid document reference
        val doc = firestore.collection("users")
            .document(userId)
            .get()
            .await()
        return doc.getString("imageUrl")
    }

    override suspend fun getUserNames(userIds: List<String>): Map<String, String> {
        if(userIds.isEmpty()) return emptyMap()

        val documents = firestore.collection("users")
            .whereIn(FieldPath.documentId(), userIds)
            .get()
            .await()

        return documents.associate { doc ->
            doc.id to (doc.getString("name") ?: "Unknown")
        }
    }
}
