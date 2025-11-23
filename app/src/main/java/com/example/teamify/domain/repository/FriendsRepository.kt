package com.example.teamify.domain.repository

interface FriendsRepository {
    suspend fun getUserNameBasedOnId(userId: String): String?
}
