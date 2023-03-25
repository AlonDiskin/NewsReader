package com.diskin.alon.newsreader.news.presentation.viewmodel

import com.diskin.alon.newsreader.news.application.model.Headline
import com.diskin.alon.newsreader.news.presentation.model.UiHeadline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import javax.inject.Inject

private const val dateFormat = "d MMM, HH:mm"

class UiHeadlineMapper @Inject constructor() {

    suspend fun map(headline: Headline): UiHeadline {
        return withContext(Dispatchers.Default) {
            UiHeadline(
                headline.id,
                headline.title,
                DateTime(headline.published).toString(dateFormat),
                headline.imageUrl,
                headline.sourceName
            )
        }
    }
}