package com.example.teamify.presentation.screens.FileScreen

import com.example.teamify.domain.model.File

data class FileUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val files: List<File> = emptyList(),
)
