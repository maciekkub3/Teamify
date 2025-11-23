package com.example.teamify.data.firebase

import com.example.teamify.data.model.User

interface AuthService {
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String, name: String)
    fun signOut()
    fun getUserId(): String?
    suspend fun getUserFromFirestore(userId: String): User
}
