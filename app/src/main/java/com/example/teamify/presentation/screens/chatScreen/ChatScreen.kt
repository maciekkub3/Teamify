package com.example.teamify.presentation.screens.chatScreen

import ProfileImage
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teamify.domain.model.Chat
import com.example.teamify.domain.model.ChatWithUserInfo
import com.example.teamify.domain.model.User
import com.example.teamify.ui.theme.AppTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    state: ChatUiState,
    onFriendClick: (User) -> Unit = {},
    onFriendChatClick: (Chat) -> Unit = {},
) {

    var searchQuery by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }


        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Text(
                        text = "Messages",
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
                        IconButton(onClick = { showBottomSheet = true }) {
                            Icon(
                                imageVector = Icons.Default.AddComment,
                                contentDescription = "Add Chat",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search chats") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )


                Spacer(modifier = Modifier.height(16.dp))

                val filteredChats = if (searchQuery.isBlank()) {
                    state.chats
                } else {
                    state.chats.filter { chat ->
                        chat.otherUserName.contains(searchQuery, ignoreCase = true) ||
                                chat.chat.lastMessage.contains(searchQuery, ignoreCase = true)
                    }
                }

                LazyColumn {
                    items(filteredChats.size) { index ->
                        val chat = filteredChats[index]
                        ChatItemDisplay(
                            chat = chat,
                            onFriendChatClick = { onFriendChatClick(chat.chat) },
                        )
                    }
                }

                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showBottomSheet = false },
                        sheetState = sheetState,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {

                            FriendsList(
                                friends = state.friends,
                                onFriendClick = { user ->
                                    onFriendClick(user)
                                }
                            )

                            Button(
                                onClick = {
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showBottomSheet = false
                                        }
                                    }
                                }
                            ) {
                                Text("Close")
                            }
                        }
                    }
                }
            }
        }
}

@Composable
fun ChatItemDisplay(
    chat: ChatWithUserInfo,
    onFriendChatClick: () -> Unit
) {
    val timestamp = chat.chat.lastMessageTimestamp
    val currentDate = LocalDate.now()

    val formattedTimestamp = remember(timestamp) {
        timestamp?.let {
            if (it.toLocalDate() == currentDate) {
                it.format(DateTimeFormatter.ofPattern("HH:mm"))
            } else {
                it.format(DateTimeFormatter.ofPattern("dd/MM"))
            }
        } ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFriendChatClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            ProfileImage(
                name = chat.otherUserName,
                profileImageUrl = chat.otherUserPhotoUrl,
                modifier = Modifier.size(50.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f) // take remaining space
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = chat.otherUserName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formattedTimestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Text(
                    text = chat.chat.lastMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )

                HorizontalDivider(
                    modifier = Modifier.padding(top = 12.dp),
                    thickness = 1.dp,
                    color = Color.Gray.copy(alpha = 0.3f)
                )
            }
        }
    }
}
@Composable
fun GroupFriendsList(
    friends: List<User>,
    selectedFriends: Set<String>,
    onFriendClick: (User) -> Unit
) {
    LazyColumn {
        items(friends.size) { index ->
            val friend = friends[index]
            val isSelected = selectedFriends.contains(friend.uid)
            FriendItem(
                friend = friend,
                isSelected = isSelected,
                onClick = { onFriendClick(friend) }
            )
        }
    }
}

@Composable
fun FriendsList(
    friends: List<User>,
    onFriendClick: (User) -> Unit
) {
    LazyColumn {
        items(friends.size) { index ->
            val friend = friends[index]
            FriendItem(
                friend = friend,
                isSelected = false,
                onClick = { onFriendClick(friend) }
            )
        }
    }
}


@Composable
fun FriendItem(
    friend: User,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = friend.name.first().toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = friend.name,
                style = MaterialTheme.typography.bodyLarge,
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
fun ChatScreenPreview() {
    AppTheme {
        Surface(tonalElevation = 5.dp) {
            ChatScreen(
                state = ChatUiState(
                    chats = listOf(
                        ChatWithUserInfo(
                            chat = Chat(
                                id = "1",
                                name = "Chat with Alice",
                                lastMessage = "See you tomorrow!",
                                lastMessageTimestamp = LocalDateTime.now()
                            ),
                            otherUserName = "Alice",
                            otherUserPhotoUrl = null
                        ),
                        ChatWithUserInfo(
                            chat = Chat(
                                id = "2",
                                name = "Chat with Bob",
                                lastMessage = "Don't forget the meeting."
                            ),
                            otherUserName = "Bob",
                            otherUserPhotoUrl = null
                        )
                    ),
                    friends = listOf(
                        User(
                            uid = "1",
                            name = "John Doe",
                            email = "")
                    )
                )
            )


        }
    }
}


@Preview
@Composable
fun ItemFriendPreview() {
    FriendItem(
        friend = User(
            uid = "1",
            name = "John Doe",
            email = "dasda@gmail.com"
        ),
        isSelected = true,
        onClick = {}
    )
}

@Preview
@Composable
fun FriendListPreview() {
    GroupFriendsList(
        friends = listOf(
            User(
                uid = "1",
                name = "John Doe",
                email = "dasda@gmail.com"
            ),
            User(
                uid = "1",
                name = "John Doe",
                email = "dasda@gmail.com"
            ),
            User(
                uid = "1",
                name = "John Doe",
                email = "dasda@gmail.com"
            )
        ),
        selectedFriends = setOf("1"),
        onFriendClick = {}
    )
}

@Preview
@Composable
fun ChatItemDisplayPreview() {
    Column() {
        ChatItemDisplay(
            chat = ChatWithUserInfo(
                chat = Chat(
                    id = "1",
                    name = "Chat with Alice",
                    lastMessage = "See you tomorrow!",
                    lastMessageTimestamp = LocalDateTime.now()
                ),
                otherUserName = "Alice",
                otherUserPhotoUrl = null
            ),
            onFriendChatClick = {}
        )
    }


}
