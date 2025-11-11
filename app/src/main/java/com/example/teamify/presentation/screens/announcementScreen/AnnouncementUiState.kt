package com.example.teamify.presentation.screens.announcementScreen

import com.example.teamify.data.model.User
import com.example.teamify.data.model.UserRole
import com.example.teamify.domain.model.Announcement

data class AnnouncementUiState(
    val announcements: List<Announcement> = emptyList(),
    val userRole: UserRole = UserRole.WORKER,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
