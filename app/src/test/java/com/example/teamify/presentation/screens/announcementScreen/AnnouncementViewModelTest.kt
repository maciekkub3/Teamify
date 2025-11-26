package com.example.teamify

import com.example.teamify.data.model.User
import com.example.teamify.data.model.UserRole
import com.example.teamify.domain.model.Announcement
import com.example.teamify.domain.repository.AnnouncementRepository
import com.example.teamify.domain.repository.AuthRepository
import com.example.teamify.presentation.screens.announcementScreen.AnnouncementViewModel
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class AnnouncementViewModelTest {

    private val announcementRepository: AnnouncementRepository = mockk()
    private val authRepository: AuthRepository = mockk()


    private lateinit var viewModel: AnnouncementViewModel

    @BeforeEach
    fun setUp() {

        coEvery { authRepository.getUser() } returns User(
            name = "",
            email = "",
            role = UserRole.ADMIN
        )

        coEvery { announcementRepository.getAnnouncements() } returns flowOf(
            listOf(
                Announcement(id = "1", title = "Title 1", content = "Content 1"),
                Announcement(id = "2", title = "Title 2", content = "Content 2")
            )
        )

        viewModel = AnnouncementViewModel(
            announcementRepository,
            authRepository
        )
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `test user role is updated on init`() = runTest {
        val state = viewModel.state.value
        assert(state.userRole == UserRole.ADMIN)
    }

    @Test
    fun `test announcement is updated on init`() = runTest {
        val state = viewModel.state.value
        assert(state.announcements.size == 2)
    }

    @Test
    fun `test deleteAnnouncement calls repository`() = runTest {
        val announcementId = "1"
        coEvery { announcementRepository.deleteAnnouncement(announcementId) } returns Unit
        viewModel.deleteAnnouncement(announcementId)
        coVerify { announcementRepository.deleteAnnouncement(announcementId) }
    }

    @Test
    fun `test addAnnouncement calls repository`() = runTest {
        val title = "New Title"
        val content = "New Content"
        coEvery { announcementRepository.postAnnouncement(title, content) } returns Unit
        viewModel.addAnnouncement(title, content)
        coVerify { announcementRepository.postAnnouncement(title, content) }
    }

}
