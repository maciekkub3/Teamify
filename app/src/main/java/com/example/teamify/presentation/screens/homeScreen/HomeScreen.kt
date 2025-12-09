package com.example.teamify.presentation.screens.homeScreen

import ProfileImage
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teamify.data.model.User
import com.example.teamify.data.model.UserRole
import com.example.teamify.domain.model.Announcement
import com.example.teamify.domain.model.Event
import com.example.teamify.presentation.screens.announcementScreen.AddAnnouncementDialog
import com.example.teamify.ui.theme.AppTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.collections.take

@Composable
fun HomeScreen(
    state: HomeUiState,
    onLogoutClick: () -> Unit = {},
    onAddAnnouncement: (String, String, String) -> Unit = { _, _, _ -> },
    onUploadFile: (Uri) -> Unit = {},
    onAddEvent: () -> Unit = {},
    onSeeAllClick: () -> Unit = {}
) {
    var addAnnouncementDialog by remember { mutableStateOf(false) }

    if (addAnnouncementDialog) {
        AddAnnouncementDialog(
            onDismiss = { addAnnouncementDialog = false },
            onAddClick = { title, content, priority ->
                onAddAnnouncement(title, content, priority)
                addAnnouncementDialog = false
            }
        )
    }

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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        // Top Greeting
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ProfileImage(
                    name = state.user?.name,
                    profileImageUrl = state.user?.profileImageUrl,
                    modifier = Modifier.size(70.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Hey, ${
                            state.user?.name?.split(" ")?.firstOrNull() ?: ""
                        } \uD83D\uDC4B",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    )
                    Text(
                        text = state.user?.email ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(45.dp) // circle size
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)), // light primary tint
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Upcoming Meetings
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Upcoming Meetings",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),

                    )
                    Text(
                        "See all",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                                .clickable { onSeeAllClick() }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (state.lastEvent != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(35.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = "Meeting",
                                tint = MaterialTheme.colorScheme.tertiary,
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        EventDateTimeDisplay(event = state.lastEvent)
                        Text(
                            text = " - ${state.lastEvent.title}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    Text("No upcoming meetings", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Latest Announcements
        item {
            Column {
                Text(
                    "Latest Announcements",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )

                val lastThree = state.lastAnnouncements?.take(3) ?: emptyList()
                if (lastThree.isEmpty()) {
                    Text("No announcements yet", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        lastThree.forEach { announcement ->
                            AnnouncementItem(announcement = announcement)
                        }
                    }

                }
            }
        }

        // Recent Files
        item {
            Column {
                Text(
                    "Recent File Activity",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )

                val lastFiles = state.lastFiles ?: emptyList()
                if (lastFiles.isEmpty()) {
                    Text("No files yet", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        lastFiles.forEach { file ->
                            FileRow(fileName = file.name, fileType = file.type)
                        }
                    }
                }
            }
        }

        // Quick Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionButton(
                    label = "Upload File",
                    icon = Icons.AutoMirrored.Filled.InsertDriveFile,
                    onClick = { filePickerLauncher.launch("*/*") }
                )
                QuickActionButton(
                    label = "Add Event",
                    icon = Icons.Default.CalendarMonth,
                    onClick = onAddEvent
                )
                QuickActionButton(label = "Post Announcement", icon = Icons.Default.AddAlert) {
                    addAnnouncementDialog = true
                }
            }
        }
    }
}

@Composable
fun FileRow(fileName: String, fileType: String) {
    val (icon, color) = getFileIcon(fileType)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp) // optional, you can remove or adjust
    ) {
        Box(
            modifier = Modifier
                .size(35.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = fileName,
                modifier = Modifier.size(24.dp),
                tint = color
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = fileName,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun priorityColor(priority: String): Color {
    return when (priority) {
        "Low" -> Color(0xFF81C784)        // Green
        "Medium" -> Color(0xFFFFD54F)     // Amber
        "High" -> Color(0xFFFF8A65)       // Orange/Red
        "Critical" -> Color(0xFFE53935)   // Red
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
}


@Composable
fun AnnouncementItem(announcement: Announcement) {

    val priorityBg = priorityColor(announcement.priority)


    Column(modifier = Modifier.padding(14.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = announcement.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = announcement.date.format(DateTimeFormatter.ofPattern("dd/MM")),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = announcement.content,
            maxLines = 1,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis
        )


    }
}
@Composable
fun QuickActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        contentPadding = PaddingValues(12.dp), // adjust padding to your taste
        modifier = Modifier.size(width = 100.dp, height = 100.dp) // square buttons
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(50.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
fun EventDateTimeDisplay(event: Event?) {
    if (event == null) {
        Text(
            text = "No upcoming events",
            style = MaterialTheme.typography.bodyLarge,
        )
        return
    }

    val today = LocalDate.now()

    val formattedDate = event.date.format(DateTimeFormatter.ofPattern("dd/MM"))
    val formattedTime = event.time?.format(DateTimeFormatter.ofPattern("HH:mm"))

    val textToShow = when {
        event.date == today && formattedTime != null -> formattedTime
        formattedTime != null -> "$formattedDate, $formattedTime"
        else -> formattedDate
    }

    Text(
        text = textToShow,
        style = MaterialTheme.typography.bodyLarge,
    )
}


@Composable
fun InfoCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
            .height(80.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

fun getFileIcon(fileType: String): Pair<ImageVector, Color> {
    return when {
        fileType.contains("pdf", ignoreCase = true) -> Icons.Default.PictureAsPdf to Color(
            0xFFFA3F3F
        )
        fileType.contains("image", ignoreCase = true) -> Icons.Default.Image to Color(0xFF3281E8)

        fileType.contains("excel", ignoreCase = true) ||
                fileType.contains("sheet", ignoreCase = true) ||
                fileType.contains("xlsx", ignoreCase = true) -> Icons.Default.TableChart to Color(0xFF5AC05E) // Green

        fileType.contains("word", ignoreCase = true) ||
                fileType.contains("doc", ignoreCase = true) -> Icons.Default.Description to Color(0xFF1860BE) // Word blue

        fileType.contains("zip", ignoreCase = true) -> Icons.Default.FolderZip to Color.Gray
        else -> Icons.Default.InsertDriveFile to Color.Gray
    }
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
            HomeScreen(
                state = HomeUiState(
                    user = User(
                        name = "John Doe",
                        email = "ma@gmail.com",
                        role = UserRole.ADMIN,
                        id = "user123",
                    ),
                    lastEvent = Event(
                        id = "event1",
                        title = "Team Meeting",
                        date = LocalDate.now(),
                        time = LocalTime.now()
                    ),
                )
            )
        }
    }
}
