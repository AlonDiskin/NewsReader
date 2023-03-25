package com.diskin.alon.newsreader.news.data.remote

import com.diskin.alon.newsreader.news.application.model.Headline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import javax.inject.Inject

class NewsApiArticlesMapper @Inject constructor() {

    suspend fun map(articles: List<NewsApiArticle>): List<Headline> {
        return withContext(Dispatchers.Default) {
            articles.map { article ->
                Headline(
                    article.url,
                    DateTime(article.publishedAt).millis,
                    article.title,
                    article.source.name,
                    article.url,
                    article.urlToImage ?: "",
                    false
                )
            }
        }
    }
}