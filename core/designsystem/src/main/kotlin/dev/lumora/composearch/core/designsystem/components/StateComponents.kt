/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import dev.lumora.composearch.core.designsystem.theme.AppTheme
import dev.lumora.composearch.core.designsystem.theme.Space

/**
 * The three states every data-backed screen has. `QueryContent` (in :core:ui) renders
 * these from a `QueryState`, which is why no screen hand-rolls a spinner or an error row.
 */

/** Full-slot loading indicator — the first-load state. */
@Composable
fun AppLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth().padding(Space.xl),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = AppTheme.colors.primary)
    }
}

/** Failure state with a retry action. [message] is the mapped `AppError.message`. */
@Composable
fun AppErrorState(
    message: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(Space.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Space.md),
    ) {
        Text(
            text = message ?: "Something went wrong",
            style = AppTheme.typography.body,
            color = AppTheme.colors.textSubtle,
            textAlign = TextAlign.Center,
        )
        Button(onClick = onRetry) { Text("Try again") }
    }
}

/** Successful load that returned nothing — distinct from loading and from failure. */
@Composable
fun AppEmptyState(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth().padding(Space.xl),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = AppTheme.typography.body,
            color = AppTheme.colors.textSubtle,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun StatesPreview() {
    AppTheme {
        Column {
            AppLoading()
            AppErrorState(message = "No internet connection", onRetry = {})
            AppEmptyState(message = "Nothing here yet")
        }
    }
}
