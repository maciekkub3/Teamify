package com.example.teamify.presentation.screens.chatScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamify.domain.repository.AuthRepository
import com.example.teamify.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state.asStateFlow()


    init {
        viewModelScope.launch {
            val userId = authRepository.getUserId()
            val userChats = chatRepository.getUserChats(userId)
            _state.update { it.copy( chats = userChats) }
        }
        viewModelScope.launch {
            val userId = authRepository.getUserId()
            val users = chatRepository.getAvailableUsersForChat(userId)
            _state.update { it.copy( friends = users)}
        }

    }

    fun onFriendToggled(userId: String) {
        val selectedFriends = _state.value.selectedFriends.toMutableSet()
        if (selectedFriends.contains(userId)) {
            selectedFriends.remove(userId)
            _state.update { it.copy(selectedFriends = selectedFriends) }
        } else {
            selectedFriends.add(userId)
            _state.update { it.copy(selectedFriends = selectedFriends) }
        }
    }
}
