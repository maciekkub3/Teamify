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

    override suspend fun signIn(email: String, password: String) {
        try {
            firebaseAuth.signInWithEmailAndPassword(email,password).await()
        } catch (e: Exception) {
            throw AuthException(message = e.message ?: "Authentication failed")
        }
    }

    override suspend fun signUp(email: String, password: String) {
        try {
            firebaseAuth.createUserWithEmailAndPassword(email,password).await()
            val userData = mapOf(
                "email" to email,
                "createdAt" to FieldValue.serverTimestamp(),
                "role" to "user"
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

