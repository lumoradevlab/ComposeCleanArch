package dev.roshana.presentation.articleUi

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.roshana.domain.models.article.Article
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun ArticleUI(article: Article, modifier: Modifier = Modifier, onClick: (Int) -> Unit) {

    Card(
        modifier = modifier
            .animateContentSize()
            .padding(8.dp)
            .clickable {
                onClick(article.hashCode())
            },
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp
    ) {
        Row {


            /*ImageCard(
                imageLink = article.url!!, modifier = Modifier
                    .fillMaxWidth(0.35f)
            )*/

            ArticleInfo(
                article = article,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            )
        }
    }
}


@Composable
fun ArticleInfo(
    article: Article,
    modifier: Modifier = Modifier,
    showExtraInfo: Boolean = true,
    alignment: Alignment.Horizontal = Alignment.Start
) {

    Column(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Text(
            text = "Article Title : ${article.author}",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.h6,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))

    }
}