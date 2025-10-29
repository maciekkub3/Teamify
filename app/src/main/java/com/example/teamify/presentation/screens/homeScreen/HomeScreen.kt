package com.example.teamify.presentation.screens.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun HomeScreen(
    state: HomeUiState,
    onLogoutClick: () -> Unit,
    onChatClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onAnnouncementClick: () -> Unit,

    ) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("name: ${state.user?.name}")
        Text("email: ${state.user?.email}")
        Text("role: ${state.user?.role}")


        Button(
            onClick = { onLogoutClick() }
        ) {
            Text("Logout")
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { onChatClick() }) { Text("Chats") }
            Button(onClick = { onCalendarClick() }) { Text("Calendar") }
            Button(onClick = { onAnnouncementClick() }) { Text("Announcement") }

        }
    }
}
