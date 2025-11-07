package com.example.teamify.presentation.screens.chatScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.teamify.domain.model.Message
import com.example.teamify.domain.model.User

@Composable
fun ConversationScreen(
    state: ConversationUiState,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(text = state.friendName?: "xd")

        LazyColumn {
            items(state.messages.size) { index ->
                val message = state.messages[index]
                Text(text = "${message.senderName}: ${message.content}")
            }
        }

        Row(

        ) {
            TextField(
                value = state.currentMessage,
                onValueChange = { onMessageChange(it) },
                label = { Text("Type a message") }
            )
            Button(
                onClick = { onSendClick() }
            ) {
                Text("Send")
            }
        }


    }
}

@Preview
@Composable
fun ConversationScreenPreview() {
    ConversationScreen(
        state = ConversationUiState(

            messages = listOf(
                UiMessage(
                    id = "1",
                    senderName = "John Doe",
                    content = "Hello!",
                    timestamp = null
                ),
                UiMessage(
                    id = "2",
                    senderName = "You",
                    content = "Hi there!",
                    timestamp = null
                )
            ),

            currentMessage = "jhniu"
        ),
        onMessageChange = {},
        onSendClick = {}
    )
}
