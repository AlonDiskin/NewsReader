package com.diskin.alon.newsreader.news.presentation

import com.diskin.alon.newsreader.news.application.model.Headline
import com.diskin.alon.newsreader.news.presentation.model.UiHeadline

fun createUiHeadlines(): List<UiHeadline> {
    return listOf(
        UiHeadline(
            "id_1",
            "title_1",
            "date_1",
            "image_url_1",
            "source_1"
        ),
        UiHeadline(
            "id_2",
            "title_2",
            "date_2",
            "image_url_2",
            "source_2"
        ),
        UiHeadline(
            "id_3",
            "title_3",
            "date_3",
            "image_url_3",
            "source_3"
        )
    )
}