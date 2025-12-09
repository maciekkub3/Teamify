package com.example.teamify.presentation.screens.chatScreen

import androidx.compose.foundation.lazy.items
import ProfileImage
import android.content.res.Configuration
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teamify.data.model.User
import com.example.teamify.data.model.UserRole
import com.example.teamify.ui.theme.AppTheme
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun ConversationScreen(
    state: ConversationUiState,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                friend = state.friend,
                onBackClick = onBackClick
            )
        },

        bottomBar = {
            Surface(
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = state.currentMessage,
                        onValueChange = onMessageChange,
                        placeholder = { Text("Type a message…") },
                        modifier = Modifier
                            .weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        enabled = state.currentMessage.isNotBlank(),
                        onClick = onSendClick,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (state.currentMessage.isNotBlank())
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.ArrowUpward,
                            contentDescription = "Send",
                            tint = if (state.currentMessage.isNotBlank())
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.rotate(45f)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        val listState = rememberLazyListState()

        LaunchedEffect(state.messages.size) {
            listState.animateScrollToItem(state.messages.size)
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.messages) { message ->

                val bubbleColor =
                    if (message.isCurrentUser)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant

                val textColor =
                    if (message.isCurrentUser)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurface

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (message.isCurrentUser)
                        Arrangement.End
                    else
                        Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = bubbleColor,
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomEnd = if (message.isCurrentUser) 0.dp else 16.dp,
                                    bottomStart = if (message.isCurrentUser) 16.dp else 0.dp
                                )
                            )
                            .padding(12.dp)
                            .widthIn(max = 260.dp)
                    ) {
                        Text(
                            text = message.content,
                            color = textColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopBar(friend: User, onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                Icons.Default.ArrowBackIosNew,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        ProfileImage(
            name = friend.name,
            profileImageUrl = friend.profileImageUrl,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))


        Text(
            text = friend.name,
            style = MaterialTheme.typography.titleMedium
        )


    }
}

@Composable
fun MessageBubble(message: UiMessage) {
    val bubbleColor = if (message.isCurrentUser)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.surfaceVariant

    val textColor = if (message.isCurrentUser)
        MaterialTheme.colorScheme.onPrimary
    else
        MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 270.dp)
                .background(
                    bubbleColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .shadow(2.dp, shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.content,
                color = textColor
            )

            message.date?.let { time ->
                Text(
                    text = time.toLocalTime().toString(),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.End),
                    color = textColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun MessageInputBar(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = message,
            onValueChange = onMessageChange,
            placeholder = { Text("Type a message…") },
            shape = RoundedCornerShape(22.dp),
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onSendClick,
            enabled = message.isNotBlank(),
            modifier = Modifier
                .size(48.dp)
                .background(
                    if (message.isNotBlank())
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = "Send",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.rotate(45f)
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
fun ConversationScreenPreview() {
    AppTheme {
        Surface(tonalElevation = 5.dp) {
            ConversationScreen(
                state = ConversationUiState(

                    messages = listOf(
                        UiMessage(
                            id = "1",
                            senderName = "John Doe",
                            content = "current user!",
                            date = LocalDateTime.now(),
                            isCurrentUser = true
                        ),
                        UiMessage(
                            id = "2",
                            senderName = "You",
                            content = "other user!",
                            date = LocalDateTime.now(),
                            isCurrentUser = false
                        ),
                        UiMessage(
                            id = "2",
                            senderName = "You",
                            content = "other user!",
                            date = LocalDateTime.now(),
                            isCurrentUser = false
                        ),
                        UiMessage(
                            id = "2",
                            senderName = "You",
                            content = "other user!",
                            date = LocalDateTime.now(),
                            isCurrentUser = true
                        ),
                        UiMessage(
                            id = "2",
                            senderName = "You",
                            content = "other user!",
                            date = LocalDateTime.now(),
                            isCurrentUser = false
                        ),
                        UiMessage(
                            id = "2",
                            senderName = "You",
                            content = "other user!",
                            date = LocalDateTime.now(),
                            isCurrentUser = true
                        ),
                        UiMessage(
                            id = "2",
                            senderName = "You",
                            content = "other user!",
                            date = LocalDateTime.now(),
                            isCurrentUser = false
                        ),
                        UiMessage(
                            id = "2",
                            senderName = "You",
                            content = "other user!",
                            date = LocalDateTime.now(),
                            isCurrentUser = false
                        ),
                        UiMessage(
                            id = "2",
                            senderName = "You",
                            content = "other user!",
                            date = LocalDateTime.now(),
                            isCurrentUser = true
                        ),
                        UiMessage(
                            id = "2",
                            senderName = "You",
                            content = "other user!",
                            date = LocalDateTime.now(),
                            isCurrentUser = false
                        ),
                        UiMessage(
                            id = "2",
                            senderName = "You",
                            content = "other user!",
                            date = LocalDateTime.now(),
                            isCurrentUser = false
                        ),
                        UiMessage(
                            id = "2",
                            senderName = "You",
                            content = "other user!",
                            date = LocalDateTime.now(),
                            isCurrentUser = true
                        ),
                        UiMessage(
                            id = "2",
                            senderName = "You",
                            content = "other user!",
                            date = LocalDateTime.now(),
                            isCurrentUser = false
                        )
                    ),

                    currentMessage = "jhniu",
                    friend = User(
                        name = "John Doe",
                        email = "sadas",
                        role = UserRole.WORKER,
                        profileImageUrl = "",
                        id = "friend1"
                    )
                ),
                onMessageChange = {},
                onSendClick = {},
                onBackClick = {}
            )
        }
    }

}
