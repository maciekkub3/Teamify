package com.example.teamify.data.repository

import com.example.teamify.domain.repository.FriendsRepository
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

class FriendsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): FriendsRepository {
    override suspend fun getUserNameBasedOnId(userId: String): String? {
        return try {
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            document.getString("name")
        } catch (e: Exception) {
            return e.toString()
        }
    }
}
