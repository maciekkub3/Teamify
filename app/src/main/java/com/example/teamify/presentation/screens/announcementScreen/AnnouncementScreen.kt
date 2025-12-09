package com.example.teamify.presentation.screens.announcementScreen

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teamify.data.model.UserRole
import com.example.teamify.domain.model.Announcement
import com.example.teamify.ui.theme.AppTheme
import java.time.LocalDate

@Composable
fun AnnouncementScreen(
    state: AnnouncementUiState,
    onAddAnnouncement: (String, String, String) -> Unit,
    onRemove: (Announcement) -> Unit = { }
) {

    var addAnnouncementDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = "Announcements",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(45.dp) // circle size
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)), // light primary tint
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { addAnnouncementDialog = !addAnnouncementDialog }) {
                    Icon(
                        imageVector = Icons.Default.AddAlert,
                        contentDescription = "Add Announcement",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }

        if(state.announcements.isNotEmpty()) {
            var expanded by remember { mutableStateOf(false) }
            var selectedPriority by remember { mutableStateOf("All") }

            val availablePriorities = listOf("All") +
                    state.announcements.map { it.priority }
                        .filter { it.isNotBlank() }
                        .distinct()
                        .sortedByDescending { priorityOrder[it] ?: 0 }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedPriority)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    availablePriorities.forEach { priority ->
                        DropdownMenuItem(
                            text = { Text(priority) },
                            onClick = {
                                selectedPriority = priority
                                expanded = false
                            }
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(8.dp))


            AnnouncementList(
                announcements = if (selectedPriority == "All") {
                    state.announcements
                } else {
                    state.announcements.filter { it.priority == selectedPriority }
                },
                modifier = Modifier.fillMaxSize(),
                onRemove = { onRemove(it) }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No announcements available.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }


    }


    if (addAnnouncementDialog) {
        AddAnnouncementDialog(
            onDismiss = { addAnnouncementDialog = false },
            onAddClick = { title, content, priority ->
                onAddAnnouncement(title, content, priority)
                addAnnouncementDialog = false
            }
        )
    }


}


@Composable
fun AddAnnouncementDialog(
    onDismiss: () -> Unit,
    onAddClick: (String, String, String) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add Announcement") },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier
                        .height(150.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Priority", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(4.dp))

                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(priority)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("Low", "Medium", "High", "Critical").forEach { level ->
                            DropdownMenuItem(
                                text = { Text(level) },
                                onClick = {
                                    priority = level
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAddClick(title, content, priority) },
                enabled = title.isNotBlank() && content.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private val priorityOrder = mapOf(
    "Critical" to 4,
    "High" to 3,
    "Medium" to 2,
    "Low" to 1,
    "" to 0
)

@Composable
fun AnnouncementList(
    announcements: List<Announcement>,
    modifier: Modifier = Modifier,
    onRemove: (Announcement) -> Unit
) {
    val sorted = announcements.sortedByDescending {
        priorityOrder[it.priority] ?: 0
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(sorted.size) { index ->
            AnnouncementItem(
                announcement = sorted[index],
                onRemove = { onRemove(it) }
            )
        }
    }
}

@Composable
fun AnnouncementItem(
    announcement: Announcement,
    onRemove: (Announcement) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentItem by rememberUpdatedState(announcement)
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onRemove(currentItem)
                    Toast.makeText(context, "Announcement deleted", Toast.LENGTH_SHORT).show()
                }

                else -> return@rememberSwipeToDismissBoxState false
            }
            true
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = { DismissBackground(dismissState) },
        enableDismissFromStartToEnd = false
    ) {
        AnnouncementCard(announcement = announcement)
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
fun AnnouncementCard(
    announcement: Announcement,
) {
    val priorityBg = priorityColor(announcement.priority)
    val dateText = announcement.date.toString()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {

            // HEADER: title + priority badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = announcement.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Box(
                    modifier = Modifier
                        .background(priorityBg.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = announcement.priority.ifBlank { "No priority" },
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = priorityBg
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // CONTENT
            Text(
                text = announcement.content,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // DATE BADGE
            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = dateText,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.2f)
        else -> Color.Transparent
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Icon(
            Icons.Default.Delete,
            contentDescription = "Delete Announcement",
            tint = Color.Red
        )
    }
}

@Preview
@Composable
fun PreviewAnnouncementCard() {
    AppTheme {
        Surface(tonalElevation = 5.dp) {
            AnnouncementCard(
                announcement = Announcement(
                    id = "1",
                    title = "System Maintenance Scheduled",
                    content = "Our systems will undergo maintenance on Saturday from 1 AM to 5 AM. Please save your work accordingly.",
                    date = LocalDate.now(),
                    priority = "High"
                )
            )
        }
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
fun AnnouncementScreenPreview() {
    AppTheme {
        Surface(tonalElevation = 5.dp) {
            AnnouncementScreen(
                onAddAnnouncement = { title, content, priority ->
                },
                state = AnnouncementUiState(
                    announcements = listOf(
                        Announcement(
                            id = "1",
                            title = "New Policy Update",
                            content = "We have updated our company policies. Please review them at your earliest convenience.",
                            date = LocalDate.now()

                        ),
                        Announcement(
                            id = "2",
                            title = "Upcoming Maintenance",
                            content = "Scheduled maintenance will occur this weekend. Expect downtime from 2 AM to 6 AM on Saturday.",
                            date = LocalDate.now()
                        ),
                        Announcement(
                            id = "5",
                            title = "Upcoming Working from Home Day",
                            content = "Scheduled maintenance will occur this weekend. Expect downtime from 2 AM to 6 AM on Saturday.",
                            date = LocalDate.now()
                        )
                    ),
                    userRole = UserRole.ADMIN
                )
            )
        }
    }
}
