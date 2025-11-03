package com.example.teamify.presentation.screens.chatScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
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
    onMessageChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(text = state.friend?.name ?: "null" )

        LazyColumn {
            items(state.messages.size) { index ->
                val message = state.messages[index]
                Text(text = "${message.senderId}: ${message.content}")
            }
        }

        TextField(
            value = state.currentMessage,
            onValueChange = { onMessageChange(it) },
            label = { Text("Type a message") }
        )

    }
}

@Preview
@Composable
fun ConversationScreenPreview() {
    ConversationScreen(
        state = ConversationUiState(
            friend = User(
                uid = "1",
                name = "John Doe",
                email = "maciek.k20"
            ),
            messages = listOf(
                Message(
                    senderId = "1",
                    content = "Hello!"
                ),
                Message(
                    senderId = "2",
                    content = "Hi there!"
                )
            ),
            currentMessage = "jhniu"
        ),
        onMessageChange = {}
    )
}
