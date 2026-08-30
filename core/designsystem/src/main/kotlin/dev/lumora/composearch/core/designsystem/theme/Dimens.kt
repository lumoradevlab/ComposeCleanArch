/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.designsystem.theme

import androidx.compose.ui.unit.dp

/**
 * Anatomy tokens — the spacing/radius/stroke scale.
 *
 * Layout code uses `Space.md`, not `12.dp`. A literal dp in a screen is a small
 * inconsistency that compounds; a token is a decision made once.
 */
object Space {
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp
    val huge = 48.dp
}

object Radius {
    val sm = 6.dp
    val md = 10.dp
    val lg = 16.dp
    val pill = 999.dp
}

object Stroke {
    val hairline = 1.dp
    val thick = 2.dp
}
