package com.example.teamify.domain.repository

import com.example.teamify.data.model.AuthState
import com.example.teamify.data.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun streamAuthState(): Flow<AuthState>
    suspend fun signUp(email: String, password: String, name: String)
    suspend fun signIn(email: String, password: String)
    fun signOut()
    suspend fun getUser(): User
    fun getUserId(): String
}
