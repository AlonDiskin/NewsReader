package com.diskin.alon.newsreader.news.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.diskin.alon.newsreader.news.application.usecase.BrowseHeadlinesUseCase
import com.diskin.alon.newsreader.news.presentation.model.UiHeadline
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HeadlinesViewModel @Inject constructor(
    browseHeadlinesUseCase: BrowseHeadlinesUseCase,
    private val headlineMapper: UiHeadlineMapper
) : ViewModel() {

    val headlines: LiveData<PagingData<UiHeadline>> = browseHeadlinesUseCase.execute()
        .map{ it.map(headlineMapper::map) }
        .cachedIn(viewModelScope)
        .asLiveData()
}