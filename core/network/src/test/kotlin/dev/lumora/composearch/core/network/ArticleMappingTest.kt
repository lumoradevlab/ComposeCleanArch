package dev.lumora.composearch.core.network

import dev.lumora.composearch.core.network.data.article.ArticleDto
import dev.lumora.composearch.core.network.data.article.ArticleListResponseDto
import dev.lumora.composearch.core.network.data.article.SourceDto
import dev.lumora.composearch.core.network.data.article.toModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Mappers are where a loose backend meets a strict domain model, so they are worth
 * testing: a dropped-vs-blank-row decision is a real UI bug.
 */
class ArticleMappingTest {

    @Test
    fun `maps a complete article`() {
        val model = ArticleDto(
            author = "Ada",
            title = "Title",
            description = "Desc",
            url = "https://example.com/a",
            urlToImage = "https://example.com/a.png",
            publishedAt = "2024-01-01T00:00:00Z",
            source = SourceDto(id = "tc", name = "TechCrunch"),
        ).toModel()

        assertEquals("https://example.com/a", model?.id)
        assertEquals("Ada", model?.author)
        assertEquals("TechCrunch", model?.sourceName)
    }

    @Test
    fun `drops entries with no url or no title`() {
        assertNull(ArticleDto(title = "Title", url = null).toModel())
        assertNull(ArticleDto(title = " ", url = "https://example.com/a").toModel())
    }

    @Test
    fun `blank optional fields become null rather than empty strings`() {
        val model = ArticleDto(title = "T", url = "https://e.com", description = "", urlToImage = "")
            .toModel()
        assertNull(model?.description)
        assertNull(model?.imageUrl)
    }

    @Test
    fun `a null articles list maps to an empty list, not a crash`() {
        assertEquals(emptyList<Any>(), ArticleListResponseDto(articles = null).toModel())
    }

    @Test
    fun `malformed entries are skipped but good ones survive`() {
        val list = ArticleListResponseDto(
            articles = listOf(
                ArticleDto(title = "Good", url = "https://e.com/1"),
                ArticleDto(title = null, url = "https://e.com/2"),
            ),
        ).toModel()
        assertEquals(1, list.size)
        assertEquals("Good", list.first().title)
    }
}
