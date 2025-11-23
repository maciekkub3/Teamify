package com.example.teamify.presentation.screens.announcementScreen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teamify.data.model.UserRole
import com.example.teamify.domain.model.Announcement
import com.example.teamify.presentation.common.TopBar
import com.example.teamify.ui.theme.AppTheme

@Composable
fun AnnouncementScreen(
    state: AnnouncementUiState,
    onAddAnnouncement: (String, String) -> Unit,
    onBackClick: () -> Unit = { }
) {

    var addAnnouncementDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(title = "Announcements", onBackClick = { onBackClick() })
        },
        bottomBar = {
            if(state.userRole == UserRole.ADMIN) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ){
                    Button(
                        onClick = { addAnnouncementDialog = !addAnnouncementDialog }
                    ) {
                        Text("Add Announcement")
                    }
                }

            }
        },

    ) { paddingValues ->
        AnnouncementList(
            announcements = state.announcements,
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        )
        if(addAnnouncementDialog) {
            AddAnnouncementDialog(
                onDismiss = { addAnnouncementDialog = false },
                onAddClick = { title, content ->
                    onAddAnnouncement(title, content)
                    addAnnouncementDialog = false
                }
            )
        }

    }


}

@Composable
fun AddAnnouncementDialog(
    onDismiss: () -> Unit,
    onAddClick: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("")}
    var content by remember { mutableStateOf("")}

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add Announcement") },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onAddClick(title, content) }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() }
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AnnouncementList(
    announcements: List<Announcement>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(announcements.size) { index ->
            AnnouncementItem(announcement = announcements[index])
        }
    }
}

@Composable
fun AnnouncementItem(
    announcement: Announcement
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = announcement.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider()
                Text(
                    text = announcement.content,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Preview
@Composable
fun AnnouncementItemPreview() {
    AnnouncementItem(
        announcement = Announcement(
            id = "1",
            title = "New Policy Update",
            content = "We have updated our company policies. Please review them at your earliest convenience.",
        )
    )
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
    AppTheme{
        Surface(tonalElevation = 5.dp){
            AnnouncementScreen(
                onAddAnnouncement = {
                        title, content ->
                },
                state = AnnouncementUiState(
                    announcements = listOf(
                        Announcement(
                            id = "1",
                            title = "New Policy Update",
                            content = "We have updated our company policies. Please review them at your earliest convenience.",
                        ),
                        Announcement(
                            id = "2",
                            title = "Upcoming Maintenance",
                            content = "Scheduled maintenance will occur this weekend. Expect downtime from 2 AM to 6 AM on Saturday.",
                        ),
                        Announcement(
                            id = "5",
                            title = "Upcoming Working from Home Day",
                            content = "Scheduled maintenance will occur this weekend. Expect downtime from 2 AM to 6 AM on Saturday.",
                        )
                    ),
                    userRole = UserRole.ADMIN
                )
            )
        }
    }
}
