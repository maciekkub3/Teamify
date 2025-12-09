package com.example.teamify.domain.repository

interface UserRepository {
    suspend fun getUserImageUrl(userId: String): String?
    suspend fun getUserNames(userIds: List<String>): Map<String, String>
}
