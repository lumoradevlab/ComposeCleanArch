/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.app.ui.articles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.lumora.composearch.app.hooks.useHeadlines
import dev.lumora.composearch.core.designsystem.components.AppEmptyState
import dev.lumora.composearch.core.designsystem.theme.AppTheme
import dev.lumora.composearch.core.designsystem.theme.Radius
import dev.lumora.composearch.core.designsystem.theme.Space
import dev.lumora.composearch.core.ui.QueryContent

/**
 * Article detail, resolved from the already-cached list by [url].
 *
 * This is the cache paying off: navigating here re-runs the same `useHeadlines()` hook
 * under the same key, so it serves from memory instead of refetching. When a real
 * detail endpoint exists, swap in a `useArticle(url)` hook — the screen barely changes.
 */
@Composable
fun ArticleDetailScreen(
    url: String,
    modifier: Modifier = Modifier,
) {
    val headlines = useHeadlines()

    QueryContent(headlines, modifier) { articles ->
        val article = articles.firstOrNull { it.id == url }
        if (article == null) {
            AppEmptyState(message = "That article is no longer available")
            return@QueryContent
        }
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(Space.lg),
            verticalArrangement = Arrangement.spacedBy(Space.md),
        ) {
            if (article.hasImage) {
                AsyncImage(
                    model = article.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(HERO_HEIGHT)
                        .clip(RoundedCornerShape(Radius.md)),
                )
            }
            Text(
                text = article.title,
                style = AppTheme.typography.h3,
                color = AppTheme.colors.textDefault,
            )
            Text(
                text = article.displayAuthor,
                style = AppTheme.typography.caption,
                color = AppTheme.colors.textSubtle,
            )
            article.description?.let { description ->
                Text(
                    text = description,
                    style = AppTheme.typography.body,
                    color = AppTheme.colors.textDefault,
                )
            }
        }
    }
}

private val HERO_HEIGHT = 200.dp
