package com.example.teamify.data.repository
import com.example.teamify.domain.mapper.toDomain
import com.example.teamify.domain.model.Announcement
import com.example.teamify.domain.model.AnnouncementDto
import com.example.teamify.domain.repository.AnnouncementRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AnnouncementRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): AnnouncementRepository {

    override suspend fun postAnnouncement(title: String, content: String, priority: String) {
        try {
            val docRef = firestore.collection("announcements").document()

            val announcementData = mapOf(
                "title" to title,
                "content" to content,
                "timestamp" to FieldValue.serverTimestamp(),
                "priority" to priority
            )
            docRef.set(announcementData).await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAnnouncement(announcementId: String) {
        try {
            firestore.collection("announcements")
                .document(announcementId)
                .delete()
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAnnouncements(): Flow<List<Announcement>> = callbackFlow {
        firestore.collection("announcements")
            .orderBy("timestamp", Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val announcements = snapshot?.documents?.map { doc ->
                    AnnouncementDto(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        content = doc.getString("content") ?: "",
                        date = doc.getTimestamp("timestamp"),
                        priority = doc.getString("priority") ?: ""
                    ).toDomain()
                } ?: emptyList()

                trySend(announcements)
            }
        awaitClose { }
    }

}
