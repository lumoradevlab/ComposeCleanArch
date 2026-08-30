package dev.lumora.composearch.app.ui.articles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.lumora.composearch.app.hooks.useHeadlines
import dev.lumora.composearch.core.designsystem.components.AppEmptyState
import dev.lumora.composearch.core.designsystem.theme.AppTheme
import dev.lumora.composearch.core.designsystem.theme.Radius
import dev.lumora.composearch.core.designsystem.theme.Space
import dev.lumora.composearch.core.model.Article
import dev.lumora.composearch.core.ui.QueryContent

/**
 * The headlines list — and the whole point of this template in one screen.
 *
 * Note what is NOT here: no ViewModel, no `uiState` class, no `LaunchedEffect { load() }`,
 * no loading boolean, no error string. The hook owns fetching and caching; QueryContent
 * owns loading/error; this composable only describes the success case.
 */
@Composable
fun ArticleListScreen(
    onOpenArticle: (Article) -> Unit,
    modifier: Modifier = Modifier,
) {
    val headlines = useHeadlines()

    QueryContent(headlines, modifier) { articles ->
        if (articles.isEmpty()) {
            AppEmptyState(message = "No headlines right now")
            return@QueryContent
        }
        LazyColumn(
            contentPadding = PaddingValues(Space.lg),
            verticalArrangement = Arrangement.spacedBy(Space.md),
        ) {
            items(articles, key = { it.id }) { article ->
                ArticleRow(article = article, onClick = { onOpenArticle(article) })
            }
        }
    }
}

/**
 * One row. Split out because a composable over ~250 lines is a god screen — child
 * components stay previewable and cheap to recompose.
 */
@Composable
private fun ArticleRow(
    article: Article,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(Radius.md),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
    ) {
        Row(
            modifier = Modifier.padding(Space.md),
            horizontalArrangement = Arrangement.spacedBy(Space.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (article.hasImage) {
                AsyncImage(
                    model = article.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(THUMBNAIL_SIZE)
                        .clip(RoundedCornerShape(Radius.sm)),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(Space.xs)) {
                Text(
                    text = article.title,
                    style = AppTheme.typography.h6,
                    color = AppTheme.colors.textDefault,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = article.displayAuthor,
                    style = AppTheme.typography.caption,
                    color = AppTheme.colors.textSubtle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private val THUMBNAIL_SIZE = 72.dp
