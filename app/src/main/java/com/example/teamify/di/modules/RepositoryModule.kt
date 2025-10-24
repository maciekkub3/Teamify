package com.example.teamify.di.modules

import com.example.teamify.data.firebase.AuthService
import com.example.teamify.data.firebase.FirebaseAuthServiceImpl
import com.example.teamify.data.repository.AuthRepositoryImpl
import com.example.teamify.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthService(): AuthService = FirebaseAuthServiceImpl()

    @Provides
    @Singleton
    fun provideAuthRepository(
        authService: AuthService
    ): AuthRepository = AuthRepositoryImpl(authService)
}
