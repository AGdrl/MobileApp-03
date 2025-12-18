package com.example.mnews.repository

import com.example.mnews.api.RetrofitInstance
import com.example.mnews.db.ArticleDatabase
import com.example.mnews.models.Article

class NewsRepository(
    val db: ArticleDatabase
) {

    // EU-focused breaking news
    suspend fun getBreakingNews(
        countryCode: String = "de",
        pageNumber: Int = 1
    ) =
        RetrofitInstance.api.getBreakingNews(
            countryCode = countryCode,
            pageNumber = pageNumber
        )

    // Global search (country not supported by 'everything' endpoint)
    suspend fun searchNews(
        searchQuery: String,
        pageNumber: Int = 1
    ) =
        RetrofitInstance.api.searchForNews(
            searchQuery = searchQuery,
            pageNumber = pageNumber
        )

    suspend fun upsert(article: Article) =
        db.getArticleDao().upsert(article)

    fun getSavedNews() =
        db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) =
        db.getArticleDao().deleteArticle(article)
}
