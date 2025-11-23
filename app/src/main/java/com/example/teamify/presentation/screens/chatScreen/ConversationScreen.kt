package com.example.teamify.presentation.screens.chatScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ConversationScreen(
    state: ConversationUiState,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ,

        ) {
        Text(
            text = state.friendName?: "xd",
            style = typography.titleLarge,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.padding(8.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(state.messages.size) { index ->
                val message = state.messages[index]

                Row(
                    horizontalArrangement = if (message.isCurrentUser) Arrangement.End else Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp) // space between messages
                ) {

                    Text(
                        text = message.content,
                        modifier = Modifier
                            .background(
                                if (message.isCurrentUser) Color(0xFF00838F) else Color.Gray,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(10.dp)
                            .widthIn(max = 250.dp) // makes narrow chat bubbles
                    )
                }
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
                    content = "current user!",
                    timestamp = null,
                    isCurrentUser = true
                ),
                UiMessage(
                    id = "2",
                    senderName = "You",
                    content = "other user!",
                    timestamp = null,
                    isCurrentUser = false
                )
            ),

            currentMessage = "jhniu"
        ),
        onMessageChange = {},
        onSendClick = {}
    )
}
