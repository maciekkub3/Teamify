package com.example.teamify.data.repository

import com.example.teamify.domain.mapper.toDomain
import com.example.teamify.domain.model.Event
import com.example.teamify.domain.model.EventDto
import com.example.teamify.domain.repository.CalendarRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CalendarRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CalendarRepository {
    override suspend fun postEvent(title: String, description: String, date: Timestamp, time: Timestamp?) {
        try {
            val docRef = firestore.collection("events").document()
            val eventData = mapOf(
                "title" to title,
                "description" to description,
                "date" to date,
                "time" to time
            )
            docRef.set(eventData).await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteEvent(eventId: String) {
        try {
            firestore.collection("events")
                .document(eventId)
                .delete()
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getEvents(): Flow<List<Event>> = callbackFlow {
        firestore.collection("events")
            .orderBy("date", Direction.ASCENDING) // order by date
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val events = snapshot?.documents?.map { doc ->
                    EventDto(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        date = doc.getTimestamp("date") ?: Timestamp.now(),
                        time = doc.getTimestamp("time"),
                    ).toDomain()
                } ?: emptyList()
                trySend(events)
            }
        awaitClose {}
    }
}
