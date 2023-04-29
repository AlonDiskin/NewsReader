package com.diskin.alon.newsreader.news.application.usecase

import androidx.paging.PagingData
import com.diskin.alon.newsreader.news.application.interfaces.HeadlinesRepository
import com.diskin.alon.newsreader.news.application.model.Headline
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BrowseHeadlinesUseCase @Inject constructor(
    private val headlinesRepo: HeadlinesRepository
){
    fun execute(): Flow<PagingData<Headline>> {
        return headlinesRepo.getHeadlines()
    }
}