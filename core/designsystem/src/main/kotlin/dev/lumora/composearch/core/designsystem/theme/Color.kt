package dev.lumora.composearch.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Semantic colour tokens. Components read *roles* (`surface`, `textSubtle`, `error`),
 * never raw hex — so rebranding the app is editing [LightAppColors]/[DarkAppColors]
 * here, not hunting literals across screens.
 *
 * Add a role when a new *meaning* appears, not a new shade. Two roles that always hold
 * the same value are one role.
 */
@Immutable
data class AppColors(
    val primary: Color,
    val primaryMuted: Color,
    val primaryText: Color,
    val background: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val border: Color,
    val textDefault: Color,
    val textSubtle: Color,
    val textInverted: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val scrim: Color,
    /** Lets `themed()` and components branch without re-deriving luminance. */
    val isLight: Boolean,
)

val LightAppColors = AppColors(
    primary = Color(0xFF2F6BFF),
    primaryMuted = Color(0xFFE6EEFF),
    primaryText = Color(0xFF10306E),
    background = Color(0xFFF7F8FA),
    surface = Color(0xFFFFFFFF),
    surfaceRaised = Color(0xFFFFFFFF),
    border = Color(0xFFE2E5EA),
    textDefault = Color(0xFF14171C),
    textSubtle = Color(0xFF697180),
    textInverted = Color(0xFFFFFFFF),
    success = Color(0xFF1E9E6A),
    warning = Color(0xFFC77700),
    error = Color(0xFFD93A3A),
    scrim = Color(0x66000000),
    isLight = true,
)

val DarkAppColors = AppColors(
    primary = Color(0xFF6E9BFF),
    primaryMuted = Color(0xFF1B2740),
    primaryText = Color(0xFFC7D8FF),
    background = Color(0xFF0E1116),
    surface = Color(0xFF161A21),
    surfaceRaised = Color(0xFF1D222B),
    border = Color(0xFF2A303A),
    textDefault = Color(0xFFECEFF4),
    textSubtle = Color(0xFF98A1B0),
    textInverted = Color(0xFF0E1116),
    success = Color(0xFF3FBE8A),
    warning = Color(0xFFE0A030),
    error = Color(0xFFF06A6A),
    scrim = Color(0x99000000),
    isLight = false,
)

val LocalAppColors = staticCompositionLocalOf { LightAppColors }
