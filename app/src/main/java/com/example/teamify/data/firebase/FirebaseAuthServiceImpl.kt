package com.example.teamify.data.firebase

import com.example.teamify.data.model.User
import com.example.teamify.data.model.UserRole
import com.example.teamify.data.model.exception.AuthException
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): AuthService {

    private val firebaseAuth = Firebase.auth

    override fun getUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override suspend fun getUserNameBasedOnId(userId: String): String? {
        return try {
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            document.getString("name")
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getUserFromFirestore(userId: String): User {
        return try {
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            val name = document.getString("name") ?: ""
            val email = document.getString("email") ?: ""
            val role = document.getString("role") ?: ""
            User(
                name = name,
                email = email,
                role = UserRole.valueOf(role.uppercase())
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

    override suspend fun signUp(email: String, password: String, name: String) {
        try {
            firebaseAuth.createUserWithEmailAndPassword(email,password).await()
            val userData = mapOf(
                "name" to name,
                "email" to email,
                "createdAt" to FieldValue.serverTimestamp(),
                "role" to "worker"
            )
            firestore.collection("users")
                .document(firebaseAuth.currentUser!!.uid)
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

