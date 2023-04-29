package com.diskin.alon.newsreader.news.data

import androidx.paging.PagingSource.*
import com.diskin.alon.newsreader.news.data.remote.NewsApiArticlesMapper
import com.diskin.alon.newsreader.news.application.model.Headline
import com.diskin.alon.newsreader.news.data.remote.*
import com.google.common.truth.Truth.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class HeadlinesPagingSourceTest {

    // Test subject
    private lateinit var pagingSource: HeadlinesPagingSource

    // Collaborators
    private val api: NewsApi = mockk()
    private val responseMapper: NewsApiArticlesMapper = mockk()
    private val networkErrorHandler: NetworkErrorHandler = mockk()

    @Before
    fun setUp() {
        // Init subject under test
        pagingSource = HeadlinesPagingSource(api, responseMapper, networkErrorHandler)
    }

    @Test
    fun loadFirstPage_AndPrepareNextPageData_WhenPagingRefreshed()  = runTest {
        // Given
        val loadSize = 20
        val refreshParams = LoadParams.Refresh<Int>(null,loadSize,false)
        val apiResponse = mockk<NewsApiResponse>()
        val apiArticles = mockk<List<NewsApiArticle>>()
        val apiArticlesSize = 90
        val expectedHeadlines = mockk<List<Headline>>()
        val expectedNextPageKey = NEWS_API_FIRST_PAGE_INDEX + 1

        coEvery { api.getHeadlines(BuildConfig.NEWS_API_KEY, NEWS_API_SOURCES,
            NEWS_API_FIRST_PAGE_INDEX) } returns apiResponse
        coEvery { apiResponse.articles } returns apiArticles
        coEvery { apiResponse.totalResults } returns apiArticlesSize
        coEvery { responseMapper.map(apiArticles) } returns expectedHeadlines

        // When
        val actualLoadResult  = pagingSource.load(refreshParams) as LoadResult.Page<Int,Headline>

        // Then
        assertThat(actualLoadResult.data).isEqualTo(expectedHeadlines)
        assertThat(actualLoadResult.nextKey).isEqualTo(expectedNextPageKey)
    }

    @Test
    fun loadNextPageUntilMaxPagesLimitReached_WhenPageAppended()  = runTest{
        // Given
        val loadSize = 20
        val lastPageParams = LoadParams.Append(NEWS_API_LAST_PAGE_INDEX,loadSize,false)
        val apiResponse = mockk<NewsApiResponse>()
        val apiArticles = mockk<List<NewsApiArticle>>()
        val apiArticlesSize = 40
        val expectedHeadlines = mockk<List<Headline>>()
        val expectedNextPageKey = null

        coEvery { api.getHeadlines(BuildConfig.NEWS_API_KEY, NEWS_API_SOURCES,
            lastPageParams.key) } returns apiResponse
        coEvery { apiResponse.articles } returns apiArticles
        coEvery { apiResponse.totalResults } returns apiArticlesSize
        coEvery { responseMapper.map(apiArticles) } returns expectedHeadlines

        // When
        val actualLoadResult  = pagingSource.load(lastPageParams) as LoadResult.Page<Int,Headline>

        // Then
        assertThat(actualLoadResult.data).isEqualTo(expectedHeadlines)
        assertThat(actualLoadResult.nextKey).isEqualTo(expectedNextPageKey)
    }

    @Test
    fun returnFailureResultWithHandledError_WhenLoadFail() = runTest{
        // Given
        val loadError = mockk<Throwable>()
        val mappedError = mockk<Throwable>()
        val loadParams = LoadParams.Append(2,20,false)

        coEvery { api.getHeadlines(BuildConfig.NEWS_API_KEY, NEWS_API_SOURCES,
            loadParams.key) } throws loadError
        coEvery { networkErrorHandler.handle(loadError) } returns mappedError

        // When
        val actualLoadResult  = pagingSource.load(loadParams) as LoadResult.Error<Int,Headline>

        assertThat(actualLoadResult.throwable).isEqualTo(mappedError)
    }
}