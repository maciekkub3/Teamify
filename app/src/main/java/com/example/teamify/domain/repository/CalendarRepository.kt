package com.example.teamify.domain.repository

import com.example.teamify.domain.model.Event
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow

interface CalendarRepository {
    suspend fun postEvent(title: String, description: String, date: Timestamp, time: Timestamp?)
    suspend fun deleteEvent(eventId: String)
    suspend fun getEvents(): Flow<List<Event>>
}
