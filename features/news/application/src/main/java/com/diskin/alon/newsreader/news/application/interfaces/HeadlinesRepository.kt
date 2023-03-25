package com.diskin.alon.newsreader.news.application.interfaces

import androidx.paging.PagingData
import com.diskin.alon.newsreader.news.application.model.Headline
import kotlinx.coroutines.flow.Flow

interface HeadlinesRepository {

    fun getHeadlines(): Flow<PagingData<Headline>>
}