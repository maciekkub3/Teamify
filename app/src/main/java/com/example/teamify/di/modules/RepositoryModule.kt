package com.example.teamify.di.modules

import android.content.Context
import com.example.teamify.data.firebase.AuthService
import com.example.teamify.data.firebase.ChatService
import com.example.teamify.data.firebase.ChatServiceImpl
import com.example.teamify.data.firebase.FirebaseAuthServiceImpl
import com.example.teamify.data.model.UserInfo
import com.example.teamify.data.repository.AnnouncementRepositoryImpl
import com.example.teamify.data.repository.AuthRepositoryImpl
import com.example.teamify.data.repository.CalendarRepositoryImpl
import com.example.teamify.data.repository.ChatRepositoryImpl
import com.example.teamify.data.repository.FileRepositoryImpl
import com.example.teamify.data.repository.FriendsRepositoryImpl
import com.example.teamify.data.repository.UserRepositoryImpl
import com.example.teamify.domain.repository.AnnouncementRepository
import com.example.teamify.domain.repository.AuthRepository
import com.example.teamify.domain.repository.CalendarRepository
import com.example.teamify.domain.repository.ChatRepository
import com.example.teamify.domain.repository.FileRepository
import com.example.teamify.domain.repository.FriendsRepository
import com.example.teamify.domain.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
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
    fun provideFirebaseStorage(): FirebaseStorage = Firebase.storage

    @Provides
    @Singleton
    fun provideAuthService(): AuthService = FirebaseAuthServiceImpl(Firebase.firestore)

    @Provides
    @Singleton
    fun provideFileRepository(
        authRepository: AuthRepository,
        @ApplicationContext context: Context
    ): FileRepository = FileRepositoryImpl(Firebase.storage, Firebase.firestore, authRepository, context)

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository = UserRepositoryImpl(Firebase.firestore)

    @Provides
    @Singleton
    fun provideChatService(): ChatService = ChatServiceImpl(Firebase.firestore)

    @Provides
    @Singleton
    fun provideChatRepository(
        chatService: ChatService,
        authRepository: AuthRepository
    ): ChatRepository = ChatRepositoryImpl(chatService, authRepository)

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

    @Provides
    @Singleton
    fun provideAnnouncementRepository(): AnnouncementRepository = AnnouncementRepositoryImpl(Firebase.firestore)

    @Provides
    @Singleton
    fun provideFriendsRepository(): FriendsRepository = FriendsRepositoryImpl(Firebase.firestore)

    @Provides
    @Singleton
    fun provideCalendarRepository(): CalendarRepository = CalendarRepositoryImpl(Firebase.firestore)


}
