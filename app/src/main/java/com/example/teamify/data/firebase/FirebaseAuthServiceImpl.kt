package com.example.teamify.data.firebase

import android.net.Uri
import android.util.Log.e
import com.example.teamify.data.model.User
import com.example.teamify.data.model.UserRole
import com.example.teamify.data.model.exception.AuthException
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

    class FirebaseAuthServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): AuthService {

    private val firebaseAuth = Firebase.auth

    override fun getUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override suspend fun getUserFromFirestore(userId: String): User {
        return try {
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            val name = document.getString("name") ?: ""
            val email = document.getString("email") ?: ""
            val roleString = document.getString("role")?.uppercase() ?: ""
            val imageUrl = document.getString("imageUrl")

            val role = try {
                UserRole.valueOf(roleString)
            } catch (e: IllegalArgumentException) {
                UserRole.WORKER // default role if invalid
            }

            User(
                name = name,
                email = email,
                role = role,
                id = userId,
                profileImageUrl = imageUrl
            )
        } catch (e: Exception) {
            throw AuthException(message = e.message ?: "Failed to fetch user data")
        }
    }

    override suspend fun signIn(email: String, password: String) {
        try {
            firebaseAuth.signInWithEmailAndPassword(email,password).await()
        } catch (e: Exception) {
            throw AuthException(message = e.message ?: "Authentication failed")
        }
    }

    override suspend fun signUp(email: String, password: String, name: String, uri: Uri?) {

        try {
            firebaseAuth.createUserWithEmailAndPassword(email,password).await()


            val userId = firebaseAuth.currentUser!!.uid

            val imageUrl = if (uri != null) {
                val storageRef = Firebase.storage.reference.child("users/$userId/profile.jpg")
                storageRef.putFile(uri).await()
                storageRef.downloadUrl.await().toString()
            } else {
                null
            }

            val userData = mapOf(
                "name" to name.replaceFirstChar { it.uppercase() },
                "email" to email,
                "createdAt" to FieldValue.serverTimestamp(),
                "role" to "worker",
                "imageUrl" to imageUrl
            )

            firestore.collection("users")
                .document(userId)
                .set(userData)
                .await()
        } catch (e: Exception) {
            throw AuthException(message = e.message ?: "Authentication failed")
        }
    }
    override fun signOut() {
        firebaseAuth.signOut()
    }
}
