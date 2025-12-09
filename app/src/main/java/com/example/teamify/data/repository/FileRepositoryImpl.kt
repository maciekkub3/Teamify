package com.example.teamify.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.teamify.domain.mapper.toLocalDateTime
import com.example.teamify.domain.model.File
import com.example.teamify.domain.repository.AuthRepository
import com.example.teamify.domain.repository.FileRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

class FileRepositoryImpl @Inject constructor (
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context

): FileRepository {

    private val filesCollection = firestore.collection("files")


    override suspend fun uploadFile(uri: Uri): String {

        val fileId = filesCollection.document().id
        val fileName = uri.getFileName(context)

        val uploadedBy = authRepository.getUser().name

        val storageRef = storage.reference
            .child("team_files/$fileId/$fileName")

        val uploadResult = storageRef.putFile(uri).await()

        val metadata = uploadResult.metadata
        val size = metadata?.sizeBytes ?: 0L
        val type = metadata?.contentType ?: "unknown"

        val downloadUrl = storageRef.downloadUrl.await().toString()

        val data = mapOf(
            "name" to fileName,
            "url" to downloadUrl,
            "uploadedBy" to uploadedBy,
            "timestamp" to Timestamp.now(),
            "size" to size,
            "type" to type
        )

        filesCollection.document(fileId).set(data).await()

        return downloadUrl
    }

    override suspend fun deleteFile(fileId: String) {
        try {

            filesCollection.document(fileId).delete().await()

            val folderRef = storage.reference.child("team_files/$fileId")
            folderRef.listAll().await().items.forEach {
                it.delete().await()
            }

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllFiles(): Flow<List<File>> = callbackFlow {
        val listener = filesCollection
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                val files = snapshot?.documents?.map { doc ->
                    File(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        url = doc.getString("url") ?: "",
                        uploadedBy = doc.getString("uploadedBy") ?: "",
                        uploadedAt = doc.getTimestamp("timestamp")?.toLocalDateTime() ?: LocalDateTime.now(),
                        size = doc.getLong("size") ?: 0L,
                        type = doc.getString("type") ?: "unknown"
                    )
                } ?: emptyList()

                trySend(files)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getFile(fileId: String): File? {
        val doc = filesCollection.document(fileId).get().await()

        if (!doc.exists()) return null

        return File(
            id = doc.id,
            name = doc.getString("name") ?: "",
            url = doc.getString("url") ?: "",
            uploadedBy = doc.getString("uploadedBy") ?: "",
            uploadedAt = doc.getTimestamp("timestamp")?.toLocalDateTime() ?: LocalDateTime.now(),
            size = doc.getLong("size") ?: 0L,
            type = doc.getString("type") ?: "unknown"
        )
    }

    override suspend fun downloadBytes(fileUrl: String): ByteArray {
        val ref = storage.getReferenceFromUrl(fileUrl)
        return ref.getBytes(Long.MAX_VALUE).await()
    }

    private fun Uri.getFileName(context: Context): String {
        if (scheme == "content") {
            val cursor = context.contentResolver.query(this, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        return it.getString(nameIndex)
                    }
                }
            }
        }
        // fallback to last path segment if not content
        return lastPathSegment ?: "unnamed"
    }
}


