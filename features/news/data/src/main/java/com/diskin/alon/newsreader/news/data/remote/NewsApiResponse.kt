package com.diskin.alon.newsreader.news.data.remote

data class NewsApiResponse(val totalResults: Int,
                           val articles: List<NewsApiArticle>)