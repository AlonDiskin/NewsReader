package com.diskin.alon.newsreader.news.application

import androidx.paging.PagingData
import com.diskin.alon.newsreader.news.application.interfaces.HeadlinesRepository
import com.diskin.alon.newsreader.news.application.model.Headline
import com.diskin.alon.newsreader.news.application.usecase.BrowseHeadlinesUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import org.junit.Before
import org.junit.Test

class BrowseHeadlinesUseCaseTest {

    // Test subject
    private lateinit var useCase: BrowseHeadlinesUseCase

    // Collaborators
    private val headlinesRepo: HeadlinesRepository = mockk()

    @Before
    fun setUp() {
        useCase = BrowseHeadlinesUseCase(headlinesRepo)
    }

    @Test
    fun returnHeadlinesPaging_WhenExecuted() {
        // Given
        val paging = mockk<Flow<PagingData<Headline>>>()
        every { headlinesRepo.getHeadlines() } returns paging

        // When
        val actual = useCase.execute()

        // Then
        verify(exactly = 1) { headlinesRepo.getHeadlines() }
        assertThat(paging).isEqualTo(actual)
    }
}