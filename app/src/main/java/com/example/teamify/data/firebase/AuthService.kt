package com.example.teamify.data.firebase

interface AuthService {
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    fun signOut()
}
