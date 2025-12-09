package com.example.teamify.presentation.screens.chatScreen

import com.example.teamify.data.model.User
import com.example.teamify.data.model.UserRole
import com.example.teamify.domain.model.Message
import com.example.teamify.domain.repository.AuthRepository
import com.example.teamify.domain.repository.ChatRepository
import com.example.teamify.domain.repository.FriendsRepository
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

class ConversationViewModelTest {

    private val chatRepository: ChatRepository = mockk()
    private val authRepository: AuthRepository = mockk()
    private val friendRepository: FriendsRepository = mockk()

    private lateinit var viewModel: ConversationViewModel

    private val userId = "currentUserId"
    private val friendName = "friend123"
    private val chatId = "chat123"
    private val friendDisplayName = "Friend Name"
    private val currentUserDisplayName = "Current User"
    private val messageId1 = "msg1"
    private val messageId2 = "msg2"
    private val messageContent1 = "Hello"
    private val messageContent2 = "Hi"
    private val newMessageContent = "New Message"
    private val helloFriendMessage = "Hello Friend"
    private val newChatId = "newChatId"
    private val anotherMessageContent = "Another Message"
    private val friend = User(
        name = friendDisplayName,
        email = "",
        role = UserRole.WORKER,
        id = friendName,
    )

    @BeforeEach
    fun setUp() {
        coEvery { friendRepository.getUserNameBasedOnId(friendName) } returns friendDisplayName

        coEvery { authRepository.getUserId() } returns userId
        coEvery { authRepository.getUser() } returns User(
            name = currentUserDisplayName,
            email = "",
            role = UserRole.WORKER,
            id = userId,
        )

        // 🔥 Missing mock — this caused the crash
        coEvery { authRepository.getUserById(friendName) } returns User(
            name = friendDisplayName,
            email = "",
            role = UserRole.WORKER,
            id = friendName
        )

        coEvery { chatRepository.getMessages(chatId) } returns flowOf(
            listOf(
                Message(id = messageId1, senderId = userId, content = messageContent1),
                Message(id = messageId2, senderId = friendName, content = messageContent2)
            )
        )

        viewModel = ConversationViewModel(
            chatId = chatId,
            friendId = friendName,
            chatRepository,
            authRepository,
        )
    }


    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `test initial state`() = runTest {
        val state = viewModel.state.value
        assertEquals(2, state.messages.size)
    }

    @Test
    fun `test observeMessages updates state`() = runTest {
        val firstMessage = viewModel.state.value.messages[0]
        assertEquals(messageId1, firstMessage.id)
        assertEquals(currentUserDisplayName, firstMessage.senderName)
        assertEquals(messageContent1, firstMessage.content)
        assert(firstMessage.isCurrentUser)
        val secondMessage = viewModel.state.value.messages[1]
        assertEquals(messageId2, secondMessage.id)
        assertEquals(friendDisplayName, secondMessage.senderName)
        assertEquals(messageContent2, secondMessage.content)
        assert(!secondMessage.isCurrentUser)
    }

    @Test
    fun `test onMessageChange updates currentMessage`() = runTest {
        viewModel.onMessageChange(newMessageContent)
        val state = viewModel.state.value
        assertEquals(newMessageContent, state.currentMessage)
    }

    @Test
    fun `test sendMessage creates chat and sends message`() = runTest {
        val newViewModel = ConversationViewModel(
            chatId = "",
            friendId = friendName,
            chatRepository,
            authRepository,
        )

        val state = newViewModel.state.value
        assert(state.messages.isEmpty())

        newViewModel.onMessageChange(helloFriendMessage)

        coEvery { chatRepository.createChatRoom(members = listOf(userId, friendName)) } returns newChatId
        coEvery { chatRepository.sendMessage(chatId = newChatId, senderId = userId, message = helloFriendMessage) } returns Unit
        coEvery { chatRepository.getMessages(newChatId) } returns flowOf(emptyList())

        newViewModel.sendMessage()

        coVerify { chatRepository.createChatRoom(members = listOf(userId, friendName)) }
        coVerify { chatRepository.sendMessage(chatId = newChatId, senderId = userId, message = helloFriendMessage) }

        assertEquals("", state.currentMessage)
    }

    @Test
    fun `test sendMessage with blank message does nothing`() = runTest {
        viewModel.onMessageChange("   ")
        viewModel.sendMessage()
        coVerify(exactly = 0) { chatRepository.sendMessage(any(), any(), any()) }
    }

    @Test
    fun `test sendMessage with existing chatId sends message`() = runTest {
        viewModel.onMessageChange(anotherMessageContent)
        coEvery { chatRepository.sendMessage(chatId = chatId, senderId = userId, message = anotherMessageContent) } returns Unit

        viewModel.sendMessage()

        coVerify { chatRepository.sendMessage(chatId = chatId, senderId = userId, message = anotherMessageContent) }
        val state = viewModel.state.value
        assertEquals("", state.currentMessage)
    }
}
