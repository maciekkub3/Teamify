package com.example.teamify.domain.repository

import com.example.teamify.domain.model.Announcement
import kotlinx.coroutines.flow.Flow

interface AnnouncementRepository {
    suspend fun postAnnouncement(title: String, content: String, priority: String)
    suspend fun deleteAnnouncement(announcementId: String)
    suspend fun getAnnouncements(): Flow<List<Announcement>>
}
