/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Domain logic is plain Kotlin, so it tests without an emulator — that is the whole
 * reason :core:model has no Android dependencies. Test the logic that would cost you
 * if it were wrong, not every getter.
 */
class ArticleTest {

    private fun article(
        imageUrl: String? = null,
        sourceName: String? = null,
        author: String? = null,
    ) = Article(
        id = "1",
        title = "Title",
        description = null,
        url = "https://example.com/a",
        imageUrl = imageUrl,
        publishedAt = null,
        sourceName = sourceName,
        author = author,
    )

    @Test
    fun `hasImage is false for null or blank urls`() {
        assertFalse(article(imageUrl = null).hasImage)
        assertFalse(article(imageUrl = "  ").hasImage)
        assertTrue(article(imageUrl = "https://example.com/i.png").hasImage)
    }

    @Test
    fun `displayAuthor prefers author, then source, then a fallback`() {
        assertEquals("Ada", article(author = "Ada", sourceName = "Wired").displayAuthor)
        assertEquals("Wired", article(author = null, sourceName = "Wired").displayAuthor)
        assertEquals("Wired", article(author = "  ", sourceName = "Wired").displayAuthor)
        assertEquals("Unknown", article().displayAuthor)
    }
}
