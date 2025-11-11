package com.example.teamify.presentation.screens.chatScreen

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teamify.domain.model.ChatDisplay
import com.example.teamify.domain.model.User
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    state: ChatUiState,
    onFriendClick: (User) -> Unit,
    onFriendChatClick: (ChatDisplay) -> Unit
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Chat Screen")

        Spacer(modifier = Modifier.height(100.dp))

        Button(
            onClick = {
                showBottomSheet = true
            }
        ) {
            Text("Add new chat")
        }

        Text("Chats :")
        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn {
            items(state.chats.size) { index ->
                val chat = state.chats[index]
                ChatItemDisplay(
                    chat = chat,
                    onFriendChatClick = { onFriendChatClick(chat) },
                )

            }
        }

        if(showBottomSheet) {
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

@Composable
fun ChatItemDisplay(
    chat: ChatDisplay,
    onFriendChatClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onFriendChatClick() }
    ) {
        Text(
            text = chat.name,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = chat.lastMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

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
    friend : User,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFE0F7FA) else Color.White
    val borderColor = if (isSelected) Color(0xFF00838F) else Color.LightGray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
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
                        text = friend.name.first().toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = friend.name,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) Color(0xFF00838F) else Color.Black
            )
        }



        if (isSelected) {
            /*Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color(0xFF00838F)
            )*/
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
            )),
        selectedFriends = setOf("1"),
        onFriendClick = {}
    )}
