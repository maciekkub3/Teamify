package com.example.teamify.domain.repository

import android.net.Uri
import com.example.teamify.domain.model.File
import kotlinx.coroutines.flow.Flow

interface FileRepository {

    suspend fun uploadFile(uri: Uri): String
    suspend fun deleteFile(fileId: String)
    suspend fun getAllFiles(): Flow<List<File>>
    suspend fun getFile(fileId: String): File?
    suspend fun downloadBytes(fileUrl: String): ByteArray
}
