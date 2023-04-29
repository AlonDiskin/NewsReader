package com.diskin.alon.newsreader.news.data.remote

data class NewsApiArticle(val url: String,
                          val title: String,
                          val publishedAt: String,
                          val urlToImage: String?,
                          val source: Source
) {

    data class Source(val name: String)
}