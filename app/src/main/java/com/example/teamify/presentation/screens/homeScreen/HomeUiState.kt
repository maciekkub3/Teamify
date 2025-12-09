package com.example.teamify.presentation.screens.homeScreen

import com.example.teamify.data.model.AuthState
import com.example.teamify.data.model.User
import com.example.teamify.domain.model.Announcement
import com.example.teamify.domain.model.Event
import com.example.teamify.domain.model.File

data class HomeUiState(
    val authState: AuthState = AuthState.Unknown,
    val user: User? = null,
    val lastEvent: Event? = null,
    val lastAnnouncements: List<Announcement>? = null,
    val lastChat: Pair<String, String>? = null,
    val lastFiles: List<File>? = null
)

