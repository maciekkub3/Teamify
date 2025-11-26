package com.example.teamify.presentation.screens.chatScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamify.domain.repository.AuthRepository
import com.example.teamify.domain.repository.ChatRepository
import com.example.teamify.domain.repository.FriendsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val friendRepository: FriendsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ConversationUiState())
    val state: StateFlow<ConversationUiState> = _state

    private var chatId = savedStateHandle["chatId"] ?: ""
    private val friendId = savedStateHandle["friendId"] ?: ""

    init {
        viewModelScope.launch {
            val friendName = friendRepository.getUserNameBasedOnId(friendId)
            _state.update { it.copy(friendName = friendName) }
        }
        if (chatId.isNotEmpty()) {
            observeMessages(chatId)
        }
    }

    private fun observeMessages(id: String) {

        viewModelScope.launch {
            val currentUserId = authRepository.getUserId()
            val currentUserName = authRepository.getUser().name
            val friendName = friendRepository.getUserNameBasedOnId(friendId)

            chatRepository.getMessages(id).collect { messages ->
                val uiMessages = messages.map { msg ->
                    val senderName = if (msg.senderId == currentUserId) currentUserName else friendName
                    UiMessage(
                        id = msg.id,
                        senderName = senderName,
                        content = msg.content,
                        timestamp = msg.timestamp,
                        isCurrentUser = msg.senderId == currentUserId
                    )
                }
                _state.update { it.copy(messages = uiMessages) }
            }
        }
    }


    fun onMessageChange(message: String) {
        _state.update { it.copy(currentMessage = message) }
    }

    fun sendMessage(friendId: String) {
        val message = state.value.currentMessage
        if (message.isBlank()) return

        viewModelScope.launch {
            val currentUserId = authRepository.getUserId()

            if (chatId.isEmpty()) {
                chatId = chatRepository.createChatRoom(members = listOf(currentUserId, friendId))
                observeMessages(chatId)
            }
            chatRepository.sendMessage(
                chatId = chatId,
                senderId = currentUserId,
                message = message
            )
            _state.update { it.copy(currentMessage = "") }

        }
    }
}
