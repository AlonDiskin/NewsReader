package com.diskin.alon.newsreader.news.presentation

import com.diskin.alon.newsreader.news.application.model.Headline
import com.diskin.alon.newsreader.news.presentation.model.UiHeadline
import com.diskin.alon.newsreader.news.presentation.viewmodel.UiHeadlineMapper
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UiHeadlineMapperTest {

    // Test subject
    private lateinit var mapper: UiHeadlineMapper

    @Before
    fun setUp() {
        mapper = UiHeadlineMapper()
    }

    @Test
    fun mapToUiHeadline() = runTest {
        // Given
        val headline = Headline(
            "id_1",
            DateTime(2023,4,10,12,15).millis,
            "title_1",
            "source_1",
            "source_1_url",
            "image_1_url",
            false
        )
        val expected = UiHeadline(
            headline.id,
            headline.title,
            "10 Apr, 12:15",
            headline.imageUrl,
            headline.sourceName
        )

        mockkStatic(Dispatchers::Default)
        every { Dispatchers.Default } returns StandardTestDispatcher(testScheduler)

        // When
        val actual = mapper.map(headline)

        // Then
        assertThat(actual).isEqualTo(expected)
    }
}