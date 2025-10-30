package com.example.teamify.data.firebase

import com.example.teamify.data.model.exception.AuthException
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): AuthService {

    private val firebaseAuth = Firebase.auth

    override suspend fun getUserId(): String? {
        return firebaseAuth.currentUser?.uid
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

    override suspend fun getCurrentUserNameAndRole(): Pair<String?, String?> {
        val currentUser = firebaseAuth.currentUser ?: return Pair(null, null)
        return try {
            val document = firestore.collection("users")
                .document(currentUser.uid)
                .get()
                .await()
            val name = document.getString("name")
            val role = document.getString("role")
            Pair(name, role)
        } catch (e: Exception) {
            Pair(null, null)
        }
    }
}

