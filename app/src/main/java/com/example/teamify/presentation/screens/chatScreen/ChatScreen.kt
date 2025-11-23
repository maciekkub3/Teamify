package com.example.teamify.presentation.screens.chatScreen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
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
import com.example.teamify.domain.model.ChatDisplay
import com.example.teamify.domain.model.User
import com.example.teamify.presentation.common.TopBar
import com.example.teamify.ui.theme.AppTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    state: ChatUiState,
    onFriendClick: (User) -> Unit,
    onFriendChatClick: (ChatDisplay) -> Unit,
    onBackClick: () -> Unit = { }
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(title = "Chats", onBackClick = { onBackClick() })
        },
        bottomBar = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                onClick = {
                    showBottomSheet = true
                }
            ) {
                Text("Add new chat")
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column {
                LazyColumn {
                    items(state.chats.size) { index ->
                        val chat = state.chats[index]
                        ChatItemDisplay(
                            chat = chat,
                            onFriendChatClick = { onFriendChatClick(chat) },
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
}

@Composable
fun ChatItemDisplay(
    chat: ChatDisplay,
    onFriendChatClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = chat.name.first().toString().uppercase(),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onFriendChatClick() }
        ) {
            Text(
                text = chat.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = chat.lastMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

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
                        ChatDisplay(
                            id = "1",
                            name = "Chat with Alice",
                            lastMessage = "See you tomorrow!"
                        ),
                        ChatDisplay(
                            id = "2",
                            name = "Chat with Bob",
                            lastMessage = "Don't forget the meeting."
                        ),
                        ChatDisplay(
                            id = "1",
                            name = "Chat with Alice",
                            lastMessage = "See you tomorrow!"
                        ),
                        ChatDisplay(
                            id = "2",
                            name = "Chat with Bob",
                            lastMessage = "Don't forget the meeting."
                        ),
                        ChatDisplay(
                            id = "1",
                            name = "Chat with Alice",
                            lastMessage = "See you tomorrow!"
                        ),
                        ChatDisplay(
                            id = "2",
                            name = "Chat with Bob",
                            lastMessage = "Don't forget the meeting."
                        ),
                        ChatDisplay(
                            id = "1",
                            name = "Chat with Alice",
                            lastMessage = "See you tomorrow!"
                        ),
                        ChatDisplay(
                            id = "2",
                            name = "Chat with Bob",
                            lastMessage = "Don't forget the meeting."
                        ),
                        ChatDisplay(
                            id = "1",
                            name = "Chat with Alice",
                            lastMessage = "See you tomorrow!"
                        ),
                        ChatDisplay(
                            id = "2",
                            name = "Chat with Bob",
                            lastMessage = "Don't forget the meeting."
                        ),
                        ChatDisplay(
                            id = "1",
                            name = "Chat with Alice",
                            lastMessage = "See you tomorrow!"
                        ),
                        ChatDisplay(
                            id = "2",
                            name = "Chat with Bob",
                            lastMessage = "Don't forget the meeting."
                        ),
                        ChatDisplay(
                            id = "1",
                            name = "Chat with Alice",
                            lastMessage = "See you tomorrow!"
                        ),
                        ChatDisplay(
                            id = "2",
                            name = "Chat with Bob",
                            lastMessage = "Don't forget the meeting."
                        ),
                        ChatDisplay(
                            id = "1",
                            name = "Chat with Alice",
                            lastMessage = "See you tomorrow!"
                        ),
                        ChatDisplay(
                            id = "2",
                            name = "Chat with Bob",
                            lastMessage = "Don't forget the meeting."
                        )
                    ),
                    friends = listOf(
                        User(
                            uid = "1",
                            name = "John Doe",
                            email = ""
                        )
                    )
                ),
                onFriendClick = { },
                onFriendChatClick = { },
                onBackClick = { }
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
            chat = ChatDisplay(
                id = "1",
                name = "Chat with Alice",
                lastMessage = "See you tomorrow!"
            ),
            onFriendChatClick = {}
        )
        ChatItemDisplay(
            chat = ChatDisplay(
                id = "1",
                name = "Chat with Alice",
                lastMessage = "See you tomorrow! See you tomorrow! See you tomorrowSee you tomorrow! See you tomorrow! ! See you tomorrow! See you tomorrow! See you tomorrow! See you tomorrow! See you tomorrow! "
            ),
            onFriendChatClick = {}
        )
    }


}
