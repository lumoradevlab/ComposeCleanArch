/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.network.data.article

import com.google.gson.annotations.SerializedName
import dev.lumora.composearch.core.model.Article

/**
 * Wire shapes for the articles endpoint, plus the DTO→model mappers.
 *
 * DTOs and their mappers live on the data side (:core:network), never in :core:model —
 * a mapper references a DTO, so putting it in the domain module would force
 * `model → network` and a dependency cycle.
 *
 * Be defensive: one unexpected field can fail the whole response, so everything the
 * server might omit is nullable here and normalised in [toModel].
 */
data class ArticleListResponseDto(
    @SerializedName("status") val status: String? = null,
    @SerializedName("totalResults") val totalResults: Int? = null,
    @SerializedName("articles") val articles: List<ArticleDto>? = null,
)

data class ArticleDto(
    @SerializedName("author") val author: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("urlToImage") val urlToImage: String? = null,
    @SerializedName("publishedAt") val publishedAt: String? = null,
    @SerializedName("source") val source: SourceDto? = null,
)

data class SourceDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
)

/**
 * Maps one wire article to the domain model, or null when it is unusable.
 *
 * The url doubles as the identity (NewsAPI sends no id), so an entry without a url or
 * a title is dropped rather than surfaced as a blank row.
 */
fun ArticleDto.toModel(): Article? {
    val articleUrl = url?.takeIf { it.isNotBlank() } ?: return null
    val articleTitle = title?.takeIf { it.isNotBlank() } ?: return null
    return Article(
        id = articleUrl,
        title = articleTitle,
        description = description?.takeIf { it.isNotBlank() },
        url = articleUrl,
        imageUrl = urlToImage?.takeIf { it.isNotBlank() },
        publishedAt = publishedAt,
        sourceName = source?.name,
        author = author,
    )
}

/** Maps the list body, dropping malformed entries. */
fun ArticleListResponseDto.toModel(): List<Article> =
    articles.orEmpty().mapNotNull { it.toModel() }
