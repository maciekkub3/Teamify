package com.example.teamify.data.repository

import android.net.Uri
import com.example.teamify.data.firebase.AuthService
import com.example.teamify.data.model.AuthState
import com.example.teamify.data.model.User
import com.example.teamify.data.model.UserInfo
import com.example.teamify.data.model.UserRole
import com.example.teamify.data.model.exception.AuthException
import com.example.teamify.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val userInfo: UserInfo
) : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unknown)

    override fun streamAuthState(): Flow<AuthState> = _authState.asStateFlow()

    override suspend fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password must not be empty")
            return
        }
        _authState.value = AuthState.Loading
        try {
            authService.signIn(email, password)
            val uid = authService.getUserId() ?: throw AuthException("User ID not found after sign-in")
            val user = authService.getUserFromFirestore(uid)
            _authState.value = AuthState.Authenticated
            userInfo.updateUserInfo(
                email = email,
                role = user.role.name.lowercase(),
                name = user.name.toString(),
                id = uid,
                imageUrl = user.profileImageUrl
            )
        } catch (e: AuthException) {
            _authState.value = AuthState.Error(e.message)

        }
    }

    override suspend fun signUp(email: String, password: String, name: String, imageUri: Uri?) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            _authState.value = AuthState.Error("Email, name and password must not be empty")
            return
        }
        _authState.value = AuthState.Loading
        try {
            authService.signUp(email, password, name, imageUri)
            val userId = authService.getUserId()
            userInfo.updateUserInfo(name, email, "worker", userId ?: "", imageUri.toString())
            _authState.value = AuthState.Authenticated
        } catch (e: AuthException) {
            _authState.value = AuthState.Error(e.message)
        }
    }

    override fun signOut() {
        authService.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    override suspend fun getUser(): User {
        return combine(userInfo.name, userInfo.email, userInfo.role, userInfo.imageUrl) { name, email, roleString, imageUrl ->
            val userRole = when (roleString) {
                "admin" -> UserRole.ADMIN
                "worker" -> UserRole.WORKER
                else -> UserRole.WORKER
            }
            User(
                name = name ?: "",
                email = email ?: "",
                role = userRole,
                id = getUserId(),
                profileImageUrl = imageUrl
            )
        }.first()
    }

    override fun getUserId(): String {
        return authService.getUserId() ?: ""
    }

    override suspend fun getUserById(userId: String): User {

        return authService.getUserFromFirestore(userId)
    }
}
