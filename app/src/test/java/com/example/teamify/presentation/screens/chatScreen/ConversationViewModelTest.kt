package com.example.teamify.presentation.screens.chatScreen

import androidx.lifecycle.SavedStateHandle
import com.example.teamify.MainDispatcherExtension
import com.example.teamify.data.model.User
import com.example.teamify.data.model.UserRole
import com.example.teamify.domain.model.Message
import com.example.teamify.domain.repository.AuthRepository
import com.example.teamify.domain.repository.ChatRepository
import com.example.teamify.domain.repository.FriendsRepository
import com.google.firebase.Timestamp
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class ConversationViewModelTest {

    private val chatRepository: ChatRepository = mockk()
    private val authRepository: AuthRepository = mockk()
    private val friendRepository: FriendsRepository = mockk()

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: ConversationViewModel

    @BeforeEach
    fun setUp() {
        savedStateHandle = SavedStateHandle(
            mapOf(
                "chatId" to "chat123",
                "friendId" to "friend123"
            )
        )
        coEvery { friendRepository.getUserNameBasedOnId("friend123") } returns "Friend Name"

        coEvery { authRepository.getUserId() } returns "currentUserId"
        coEvery { authRepository.getUser() } returns User(
            name = "Current User",
            email = "",
            role = UserRole.WORKER
        )

        coEvery { chatRepository.getMessages("chat123") } returns flowOf(
            listOf(
                Message(id = "msg1", senderId = "currentUserId", content = "Hello", timestamp = Timestamp(123456789, 0)),
                Message(id = "msg2", senderId = "friend123", content = "Hi", timestamp = Timestamp(123456789, 0))
            )
        )

        viewModel = ConversationViewModel(
            chatRepository,
            authRepository,
            friendRepository,
            savedStateHandle
        )
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `test initial state`() = runTest {
        val state = viewModel.state.value
        assertEquals("Friend Name", state.friendName)
        assertEquals(2, state.messages.size)
    }

    @Test
    fun `test observeMessages updates state`() = runTest {
        val firstMessage = viewModel.state.value.messages[0]
        assertEquals("msg1", firstMessage.id)
        assertEquals("Current User", firstMessage.senderName)
        assertEquals("Hello", firstMessage.content)
        assert(firstMessage.isCurrentUser)
        val secondMessage = viewModel.state.value.messages[1]
        assertEquals("msg2", secondMessage.id)
        assertEquals("Friend Name", secondMessage.senderName)
        assertEquals("Hi", secondMessage.content)
        assert(!secondMessage.isCurrentUser)
    }

    @Test
    fun `test onMessageChange updates currentMessage`() = runTest {
        viewModel.onMessageChange("New Message")
        val state = viewModel.state.value
        assertEquals("New Message", state.currentMessage)
    }

    @Test
    fun `test sendMessage creates chat and sends message`() = runTest {
        val newSavedStateHandle = SavedStateHandle(
            mapOf(
                "chatId" to "",
                "friendId" to "friend123"
            )
        )
        val newViewModel = ConversationViewModel(
            chatRepository,
            authRepository,
            friendRepository,
            newSavedStateHandle
        )

        var state = newViewModel.state.value
        assert(state.messages.isEmpty())

        newViewModel.onMessageChange("Hello Friend")

        coEvery { chatRepository.createChatRoom(members = listOf("currentUserId", "friend123")) } returns "newChatId"
        coEvery { chatRepository.sendMessage(chatId = "newChatId", senderId = "currentUserId", message = "Hello Friend") } returns Unit
        coEvery { chatRepository.getMessages("newChatId") } returns flowOf(emptyList())

        newViewModel.sendMessage("friend123")

        coVerify { chatRepository.createChatRoom(members = listOf("currentUserId", "friend123")) }
        coVerify { chatRepository.sendMessage(chatId = "newChatId", senderId = "currentUserId", message = "Hello Friend") }

        assertEquals("", state.currentMessage)
    }

    @Test
    fun `test sendMessage with blank message does nothing`() = runTest {
        viewModel.onMessageChange("   ")
        viewModel.sendMessage("friend123")
        coVerify(exactly = 0) { chatRepository.sendMessage(any(), any(), any()) }
    }

    @Test
    fun `test sendMessage with existing chatId sends message`() = runTest {
        viewModel.onMessageChange("Another Message")
        coEvery { chatRepository.sendMessage(chatId = "chat123", senderId = "currentUserId", message = "Another Message") } returns Unit

        viewModel.sendMessage("friend123")

        coVerify { chatRepository.sendMessage(chatId = "chat123", senderId = "currentUserId", message = "Another Message") }
        val state = viewModel.state.value
        assertEquals("", state.currentMessage)
    }
}
