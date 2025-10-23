package com.example.teamify.data.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuth @Inject constructor() {

    private val auth = Firebase.auth
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    suspend fun login(email: String, password: String) {

        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password must not be empty")
            return
        }

        _authState.value = AuthState.Loading

        try {
            auth.signInWithEmailAndPassword(email,password).await()
            _authState.value = AuthState.Authenticated
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Unknown error")
        }

    }

    suspend fun signup(email: String, password: String) {

        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password must not be empty")
            return
        }

        _authState.value = AuthState.Loading

        try {
            auth.createUserWithEmailAndPassword(email,password).await()

            val user = auth.currentUser ?: throw Exception("User not found after sign-up")

            val userData = mapOf(
                "email" to user.email,
                "createdAt" to FieldValue.serverTimestamp(),
                "role" to "user"
            )
            Firebase.firestore.collection("users").document(user.uid).set(userData).await()

            _authState.value = AuthState.Authenticated

        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Sign-up failed")
        }

    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
        }
        Log.d("FirebaseAuth", "checkAuthStatus: ${authState.value}")
    }

}

sealed class AuthState{
    object Authenticated: AuthState()
    object Unauthenticated: AuthState()
    object Loading : AuthState()
    data class Error(val message: String): AuthState()
}
