package com.example.teamify.data.firebase

interface AuthService {
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String, name: String)
    fun signOut()
    suspend fun getCurrentUserNameAndRole(): Pair<String?, String?>
    fun getUserId(): String?
    suspend fun getUserNameBasedOnId(userId: String): String?

}
