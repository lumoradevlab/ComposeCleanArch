package dev.roshana.data.network.models.dto

import com.google.gson.annotations.SerializedName

data class ArticleDto(
    @SerializedName("source") var source: SourceDto? = SourceDto(),
    @SerializedName("author") var author: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("urlToImage") var urlToImage: String? = null,
    @SerializedName("publishedAt") var publishedAt: String? = null,
    @SerializedName("content") var content: String? = null
)
