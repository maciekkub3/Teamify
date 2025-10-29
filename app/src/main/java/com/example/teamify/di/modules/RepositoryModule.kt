package com.example.teamify.di.modules

import android.content.Context
import com.example.teamify.data.firebase.AuthService
import com.example.teamify.data.firebase.FirebaseAuthServiceImpl
import com.example.teamify.data.model.UserInfo
import com.example.teamify.data.repository.AuthRepositoryImpl
import com.example.teamify.domain.repository.AuthRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthService(): AuthService = FirebaseAuthServiceImpl(Firebase.firestore)

    @Provides
    @Singleton
    fun provideAuthRepository(
        authService: AuthService,
        userInfo: UserInfo
    ): AuthRepository = AuthRepositoryImpl(authService, userInfo)

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideUserRole(@ApplicationContext context: Context): UserInfo {
        return UserInfo(context)
    }
}
