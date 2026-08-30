/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.designsystem.theme

import androidx.annotation.DrawableRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

/* ============================================================================
 *  Theme entry point.
 *  Wraps content in the app's colour / type tokens AND a matching Material 3
 *  theme (so stock Material components also pick up the palette).
 *
 *  Usage:
 *      setContent { AppTheme { /* your UI */ } }
 *
 *  Read tokens anywhere inside:
 *      AppTheme.colors.primary
 *      AppTheme.typography.h3
 * ========================================================================== */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkAppColors else LightAppColors
    val typography = appTypography()

    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalAppTypography provides typography,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) colors.toMaterialDark() else colors.toMaterialLight(),
            typography = typography.toMaterial(),
            content = content,
        )
    }
}

/** Access tokens via `AppTheme.colors` / `AppTheme.typography`. */
object AppTheme {
    val colors: AppColors
        @Composable @ReadOnlyComposable get() = LocalAppColors.current
    val typography: AppTypography
        @Composable @ReadOnlyComposable get() = LocalAppTypography.current
}

/**
 * Picks [light] or [dark] for the active theme — one place for the light/dark choice.
 *
 * Use this instead of `isSystemInDarkTheme()` inside components, and instead of
 * threading a `dark: Boolean` parameter down: it reads the *theme actually applied*,
 * so a preview or a nested override still resolves correctly.
 */
@Composable
@ReadOnlyComposable
fun themed(light: Color, dark: Color): Color =
    if (LocalAppColors.current.isLight) light else dark

/** [themed] for drawables — picks the [light] or [dark] resource for the active theme. */
@Composable
@ReadOnlyComposable
fun themedRes(@DrawableRes light: Int, @DrawableRes dark: Int): Int =
    if (LocalAppColors.current.isLight) light else dark

/* ---------------------------------------------------------------------------
 *  Bridge to Material 3 so stock components (Button, TopAppBar, …) inherit the
 *  palette. Map only the roles Material actually draws from.
 * ------------------------------------------------------------------------- */
private fun AppColors.toMaterialLight() = lightColorScheme(
    primary = primary,
    onPrimary = textInverted,
    primaryContainer = primaryMuted,
    onPrimaryContainer = primaryText,
    background = background,
    onBackground = textDefault,
    surface = surface,
    onSurface = textDefault,
    onSurfaceVariant = textSubtle,
    error = error,
    onError = textInverted,
    outline = border,
    scrim = scrim,
)

private fun AppColors.toMaterialDark() = darkColorScheme(
    primary = primary,
    onPrimary = textInverted,
    primaryContainer = primaryMuted,
    onPrimaryContainer = primaryText,
    background = background,
    onBackground = textDefault,
    surface = surface,
    onSurface = textDefault,
    onSurfaceVariant = textSubtle,
    error = error,
    onError = textInverted,
    outline = border,
    scrim = scrim,
)

private fun AppTypography.toMaterial() = Typography(
    displayLarge = display,
    displayMedium = h1,
    displaySmall = h2,
    headlineLarge = h2,
    headlineMedium = h3,
    headlineSmall = h4,
    titleLarge = h4,
    titleMedium = h5,
    titleSmall = h6,
    bodyLarge = body,
    bodyMedium = body,
    bodySmall = caption,
    labelLarge = h6,
    labelMedium = caption,
    labelSmall = label,
)
