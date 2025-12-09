package com.example.teamify.presentation.screens.FileScreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamify.domain.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.core.net.toUri

@HiltViewModel
class FileViewModel @Inject constructor(
    private val fileRepository: FileRepository
): ViewModel() {

    private val _state = MutableStateFlow(FileUiState())
    val state: StateFlow<FileUiState> = _state

    init {
        viewModelScope.launch {
            fileRepository.getAllFiles().collect { files ->
                _state.update {
                    it.copy(files = files)
                }
            }
        }
    }

    fun uploadFile(uri: Uri) {
        viewModelScope.launch {
            fileRepository.uploadFile(uri)
        }
    }

    fun deleteFile(fileId: String) {
        viewModelScope.launch {
            fileRepository.deleteFile(fileId)
        }
    }

    fun openFile(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = url.toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No app found to open this file", Toast.LENGTH_SHORT).show()
        }
    }

}
