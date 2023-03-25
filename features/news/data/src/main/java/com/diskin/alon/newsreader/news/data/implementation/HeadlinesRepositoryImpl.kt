package com.diskin.alon.newsreader.news.data.implementation

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diskin.alon.newsreader.news.application.interfaces.HeadlinesRepository
import com.diskin.alon.newsreader.news.application.model.Headline
import com.diskin.alon.newsreader.news.data.remote.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeadlinesRepositoryImpl @Inject constructor(
    private val api: NewsApi,
    private val responseMapper: NewsApiArticlesMapper,
    private val networkErrorHandler: NetworkErrorHandler
) : HeadlinesRepository {

    override fun getHeadlines(): Flow<PagingData<Headline>> {
        return Pager(
            config = PagingConfig(
                pageSize = NEWS_API_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                HeadlinesPagingSource(api,
                    responseMapper,
                    networkErrorHandler
                )
            }
        ).flow
    }
}