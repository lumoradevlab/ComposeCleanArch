/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.lumora.composearch.core.datastore.ThemeMode
import dev.lumora.composearch.core.designsystem.theme.AppTheme
import dev.lumora.composearch.core.designsystem.theme.Space
import dev.lumora.composearch.core.ui.theme.useThemeMode

/**
 * Settings — demonstrates the *client*-state hook (`useThemeMode`) as the counterpart
 * to the server-state hooks. Same call shape, different backing store: DataStore
 * instead of the QueryClient, because a persisted user choice is not server state.
 */
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val theme = useThemeMode()

    Column(
        modifier = modifier.padding(Space.lg),
        verticalArrangement = Arrangement.spacedBy(Space.sm),
    ) {
        Text(
            text = "Appearance",
            style = AppTheme.typography.h4,
            color = AppTheme.colors.textDefault,
            modifier = Modifier.padding(bottom = Space.sm),
        )
        ThemeMode.entries.forEach { mode ->
            ThemeOptionRow(
                label = mode.label,
                selected = theme.mode == mode,
                onSelect = { theme.setMode(mode) },
            )
        }
    }
}

@Composable
private fun ThemeOptionRow(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect)
            .padding(vertical = Space.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Space.sm),
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = label,
            style = AppTheme.typography.body,
            color = AppTheme.colors.textDefault,
        )
    }
}

private val ThemeMode.label: String
    get() = when (this) {
        ThemeMode.SYSTEM -> "Follow system"
        ThemeMode.LIGHT -> "Light"
        ThemeMode.DARK -> "Dark"
    }
