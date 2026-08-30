package dev.lumora.composearch.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * The type scale. Screens use these named roles (`h3`, `body`, `caption`) instead of
 * literal `sp` values, so tuning the scale is one edit here.
 *
 * Shipping a custom font? Build a [FontFamily] from `res/font` and pass it to
 * [appTypography] — every style picks it up at once.
 */
@Immutable
data class AppTypography(
    val display: TextStyle,
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val h4: TextStyle,
    val h5: TextStyle,
    val h6: TextStyle,
    val body: TextStyle,
    val bodyStrong: TextStyle,
    val caption: TextStyle,
    val label: TextStyle,
)

fun appTypography(fontFamily: FontFamily = FontFamily.Default) = AppTypography(
    display = TextStyle(fontFamily = fontFamily, fontSize = 40.sp, fontWeight = FontWeight.Bold),
    h1 = TextStyle(fontFamily = fontFamily, fontSize = 32.sp, fontWeight = FontWeight.Bold),
    h2 = TextStyle(fontFamily = fontFamily, fontSize = 26.sp, fontWeight = FontWeight.Bold),
    h3 = TextStyle(fontFamily = fontFamily, fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
    h4 = TextStyle(fontFamily = fontFamily, fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
    h5 = TextStyle(fontFamily = fontFamily, fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
    h6 = TextStyle(fontFamily = fontFamily, fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
    body = TextStyle(fontFamily = fontFamily, fontSize = 15.sp, fontWeight = FontWeight.Normal),
    bodyStrong = TextStyle(fontFamily = fontFamily, fontSize = 15.sp, fontWeight = FontWeight.Medium),
    caption = TextStyle(fontFamily = fontFamily, fontSize = 13.sp, fontWeight = FontWeight.Normal),
    label = TextStyle(fontFamily = fontFamily, fontSize = 12.sp, fontWeight = FontWeight.Medium),
)

val LocalAppTypography = staticCompositionLocalOf { appTypography() }
