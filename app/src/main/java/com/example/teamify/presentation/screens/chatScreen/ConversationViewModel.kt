package com.example.teamify.presentation.screens.chatScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamify.domain.repository.AuthRepository
import com.example.teamify.domain.repository.ChatRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ConversationViewModel.Factory::class)
class ConversationViewModel @AssistedInject constructor(
    @Assisted("chatId") private var chatId: String,
    @Assisted("friendId") private val friendId: String,
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ConversationUiState())
    val state: StateFlow<ConversationUiState> = _state

    init {
        viewModelScope.launch {
            val friend = authRepository.getUserById(friendId)
            _state.update { it.copy(friend = friend) }
        }
        if (chatId.isNotEmpty()) {
            observeMessages(chatId)
        }
    }

    private fun observeMessages(id: String) {

        viewModelScope.launch {
            val currentUserId = authRepository.getUserId()
            val currentUserNameDeferred = async { authRepository.getUser().name }
            val friendNameDeferred = async { authRepository.getUserById(friendId).name }
            val (currentUserName, friendName) = listOf(currentUserNameDeferred, friendNameDeferred).awaitAll()

            chatRepository.getMessages(id).collect { messages ->
                val uiMessages = messages.map { msg ->
                    val senderName = if (msg.senderId == currentUserId) currentUserName else friendName
                    UiMessage(
                        id = msg.id,
                        senderName = senderName,
                        content = msg.content,
                        date = msg.date,
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

    fun sendMessage() {
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

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("chatId") chatId: String,
            @Assisted("friendId") friendId: String,
        ): ConversationViewModel
    }
}
