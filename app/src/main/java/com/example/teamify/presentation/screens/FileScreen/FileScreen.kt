package com.example.teamify.presentation.screens.FileScreen

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teamify.domain.model.File

import com.example.teamify.ui.theme.AppTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.core.net.toUri
import com.example.teamify.presentation.screens.homeScreen.getFileIcon

@Composable
fun FileScreen(
    state: FileUiState,
    onUploadFile: (Uri) -> Unit,
    onDeleteFile: (String) -> Unit,
    onFileClick: (String) -> Unit = {}
) {
    var uploading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current


    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            uploading = true
            errorMessage = null
            coroutineScope.launch {
                try {
                    onUploadFile(uri)
                } catch (e: Exception) {
                    errorMessage = e.message
                } finally {
                    uploading = false
                }
            }
        }
    }

    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("All") }

    val availableTypes = listOf("All") +
            state.files.map { it.type.ifBlank { "Unknown" } }
                .distinct()
                .sorted()

    Column(modifier = Modifier.padding(16.dp)) {

        // HEADER: Title + Upload Button
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Files",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { filePickerLauncher.launch("*/*") }) {
                    Icon(
                        imageVector = Icons.Default.UploadFile,
                        contentDescription = "Upload File",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // FILTER DROPDOWN
        if (state.files.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(selectedType)
                }

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth()) {
                    availableTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // FILTERED FILE LIST
            val filteredFiles = if (selectedType == "All") state.files else state.files.filter { it.type.ifBlank { "Unknown" } == selectedType }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredFiles) { file ->
                    FileItem(
                        file = file,
                        onClick = { onFileClick(file.url) },
                        onDelete = {
                            coroutineScope.launch {
                                try {
                                    onDeleteFile(file.id)
                                } catch (e: Exception) {
                                    errorMessage = e.message
                                }
                            }
                        },
                        onDownload = { url, name -> downloadFile(context, url, name) }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No files available.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (uploading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        if (errorMessage != null) Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(12.dp))
    }
}




@Composable
fun FileItem(
    file: File,
    onClick: (String) -> Unit = {},
    onDelete: () -> Unit,
    onDownload: (String, String) -> Unit = { _, _ -> }
) {
    val (icon, color) = getFileIcon(file.type)

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(file.url) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // FILE ICON
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // FILE INFO
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Uploaded by ${file.uploadedBy}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = file.uploadedAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy • HH:mm")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ACTION BUTTONS
            Row {
                IconButton(onClick = { onDownload(file.url, file.name) }) {
                    Icon(
                        Icons.Default.Download,
                        contentDescription = "Download"
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

fun downloadFile(context: Context, fileUrl: String, fileName: String) {
    val request = android.app.DownloadManager.Request(fileUrl.toUri())
        .setTitle(fileName)
        .setDescription("Downloading...")
        .setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setAllowedOverMetered(true)
        .setDestinationInExternalPublicDir(
            android.os.Environment.DIRECTORY_DOWNLOADS,
            fileName
        )

    val dm = context.getSystemService(android.app.DownloadManager::class.java)
    dm.enqueue(request)

    Toast.makeText(context, "Downloading $fileName...", Toast.LENGTH_SHORT).show()
}


@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Normal Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
fun ChatScreenPreview() {
    AppTheme {
        Surface(tonalElevation = 5.dp) {
            FileScreen(
                state = FileUiState(
                    files = listOf(
                        File(
                            id = "1",
                            name = "Project_Plan.pdf",
                            uploadedBy = "Alice",
                            uploadedAt = LocalDate.now().atTime(LocalTime.now())
                        ),
                        File(
                            id = "2",
                            name = "Meeting_Notes.docx",
                            uploadedBy = "Bob",
                            uploadedAt = LocalDate.now().atTime(LocalTime.now())
                        ),
                        File(
                            id = "3",
                            name = "Design_Mockup.png",
                            uploadedBy = "Charlie",
                            uploadedAt = LocalDate.now().atTime(LocalTime.now())
                        )
                    )
                ),
                onUploadFile = {},
                onDeleteFile = {}
            )
        }
    }
}
