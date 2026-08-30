package dev.lumora.composearch.core.model

/**
 * A news article — the example domain model this template is built around.
 *
 * Pure Kotlin: no Retrofit, no Gson, no Android. The wire shape (`ArticleDto`) and
 * the DTO→model mapper both live in :core:network, because a mapper references a DTO
 * and putting it here would force `model → network` and a dependency cycle.
 *
 * Derivable logic belongs on the model as testable properties — [hasImage] and
 * [displayAuthor] below are the pattern to follow, not the exhaustive list.
 */
data class Article(
    val id: String,
    val title: String,
    val description: String?,
    val url: String,
    val imageUrl: String?,
    val publishedAt: String?,
    val sourceName: String?,
    val author: String?,
) {
    /** Whether the list/detail UI can render a thumbnail for this article. */
    val hasImage: Boolean get() = !imageUrl.isNullOrBlank()

    /** Author if the feed supplied one, else the source, else a neutral fallback. */
    val displayAuthor: String
        get() = author?.takeIf { it.isNotBlank() }
            ?: sourceName?.takeIf { it.isNotBlank() }
            ?: "Unknown"
}
