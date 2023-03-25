package com.diskin.alon.newsreader.news.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.diskin.alon.newsreader.news.application.model.Headline
import com.diskin.alon.newsreader.news.data.BuildConfig
import javax.inject.Inject

class HeadlinesPagingSource @Inject constructor(
    private val api: NewsApi,
    private val responseMapper: NewsApiArticlesMapper,
    private val networkErrorHandler: NetworkErrorHandler
) : PagingSource<Int,Headline>() {

    override fun getRefreshKey(state: PagingState<Int, Headline>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Headline> {
        return try {
            val page = params.key ?: NEWS_API_FIRST_PAGE_INDEX
            val apiResponse = api.getHeadlines(BuildConfig.NEWS_API_KEY, NEWS_API_SOURCES,page)
            val articles = responseMapper.map(apiResponse.articles)
            val prevKey = null

            val nextKey = if (page == NEWS_API_LAST_PAGE_INDEX) null else page + 1

            LoadResult.Page(articles,prevKey,nextKey)

        } catch (error: Throwable) {
            LoadResult.Error(networkErrorHandler.handle(error))
        }
    }
}