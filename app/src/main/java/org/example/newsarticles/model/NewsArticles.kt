package org.example.newsarticles.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class NewsArticles(
    @SerializedName("articles")
    val articles: List<Article?>? = listOf(),
    @SerializedName("status")
    val status: String? = ""
)