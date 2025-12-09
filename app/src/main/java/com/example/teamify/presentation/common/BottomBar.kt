package com.example.teamify.presentation.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Announcement
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onHomeClick: () -> Unit,
    onChatClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onAnnouncementClick: () -> Unit,
    onFilesClick: () -> Unit
) {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, onHomeClick, route = "HomeRoute"),
        BottomNavItem("Chat", Icons.Default.Chat, onChatClick, route = "ChatRoute"),
        BottomNavItem("Calendar", Icons.Default.CalendarMonth, onCalendarClick, route = "CalendarRoute"),
        BottomNavItem("Announcements", Icons.Default.Announcement, onAnnouncementClick, route = "AnnouncementRoute"),
        BottomNavItem("Files", Icons.Default.Folder, onFilesClick, route = "FileRoute")
    )

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                val tint by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = tint,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(4.dp)
                        .clickable { item.onClick() }
                )
            }
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val route: String
)

@Preview
@Composable
fun BottomBarPreview() {
    Surface {
        Scaffold(
            bottomBar = { BottomNavigationBar(
                onChatClick = { },
                onCalendarClick = { },
                onAnnouncementClick = { },
                onHomeClick = { },
                onFilesClick = { },
                currentRoute = null
            ) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
            ) {

            }
        }
    }
}
