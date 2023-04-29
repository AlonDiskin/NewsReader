package com.diskin.alon.newsreader.news.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diskin.alon.newsreader.news.application.model.Headline
import com.diskin.alon.newsreader.news.application.usecase.BrowseHeadlinesUseCase
import com.diskin.alon.newsreader.news.presentation.viewmodel.HeadlinesViewModel
import com.diskin.alon.newsreader.news.presentation.viewmodel.UiHeadlineMapper
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(instrumentedPackages = ["androidx.loader.content"])
class HeadlinesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Test subject
    private lateinit var viewModel: HeadlinesViewModel

    // Collaborators
    private val browseHeadlinesUseCase: BrowseHeadlinesUseCase = mockk()
    private val headlineMapper: UiHeadlineMapper = mockk()

    // Stub data
    private val headlines: List<Headline> = listOf(mockk(),mockk(),mockk())
    private val headlinesFlow: Flow<PagingData<Headline>> = flow { emit(PagingData.from(headlines)) }

    @Before
    fun setUp() {
        // Stub mocked collaborators
        every { browseHeadlinesUseCase.execute() } returns headlinesFlow
        coEvery { headlineMapper.map(any()) } returns mockk()

        // Init subject
        viewModel = HeadlinesViewModel(browseHeadlinesUseCase, headlineMapper)
    }

    @Test
    fun providePagedHeadlinesStreamToViewObservers_WhenCreated() {
        // Given

        // When
        viewModel.headlines.observeForever {  }

        // Then
        assertThat(viewModel.headlines.value).isNotNull()
    }
}