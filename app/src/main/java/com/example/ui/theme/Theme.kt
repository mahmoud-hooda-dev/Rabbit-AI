package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppThemeMode(val title: String) {
    SYSTEM("System"),
    DARK("Dark"),
    LIGHT("Light")
}

val LocalIsDarkTheme = compositionLocalOf { true }
val LocalCustomFontColor = compositionLocalOf { Color.Unspecified }
val LocalFontSizeScale = compositionLocalOf { 1.0f }
val LocalAccentColor = compositionLocalOf { Color(0xFF10D876) }

fun parseCustomFontColor(hex: String, defaultColor: Color): Color {
    return try {
        val clean = hex.removePrefix("#").trim()
        when (clean.length) {
            6 -> Color(android.graphics.Color.parseColor("#$clean"))
            8 -> Color(android.graphics.Color.parseColor("#$clean"))
            else -> defaultColor
        }
    } catch (_: Exception) {
        defaultColor
    }
}

private val RabbitDarkColorScheme = darkColorScheme(
    primary = RabbitMint,
    onPrimary = TextOnMint,
    primaryContainer = Color(0xFF064E2E),
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = RabbitLime,
    onSecondary = Color(0xFF1E2F02),
    secondaryContainer = Color(0xFF2C3E08),
    onSecondaryContainer = Color(0xFFECFCCB),
    tertiary = RabbitCyan,
    onTertiary = Color(0xFF00363A),
    background = RabbitObsidian,
    onBackground = TextPrimary,
    surface = RabbitSurface,
    onSurface = TextPrimary,
    surfaceVariant = RabbitNavySteel,
    onSurfaceVariant = TextSecondary,
    outline = RabbitBorder,
    error = AlertRed,
    onError = TextPrimary
)

private val RabbitLightColorScheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = Color(0xFF10B981),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF065F46),
    tertiary = Color(0xFF2563EB),
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = AlertRed,
    onError = Color.White
)

/**
 * Ensures any customized color preserves its vibrant hue while guaranteeing optimal contrast
 * and effortless readability in both Dark and Light modes.
 */
fun ensureReadableFontColor(color: Color, isDark: Boolean): Color {
    val r = color.red
    val g = color.green
    val b = color.blue
    val lum = 0.2126f * r + 0.7152f * g + 0.0722f * b
    return if (isDark) {
        // In dark mode: ensure text is bright enough on obsidian backgrounds (lum >= 0.40f)
        if (lum < 0.40f) {
            val boost = 0.42f - lum
            Color(
                red = (r + boost * 0.8f).coerceIn(0f, 1f),
                green = (g + boost * 0.8f).coerceIn(0f, 1f),
                blue = (b + boost * 0.8f).coerceIn(0f, 1f),
                alpha = 1f
            )
        } else color
    } else {
        // In light mode: ensure text is dark enough on light backgrounds (lum <= 0.38f)
        if (lum > 0.38f) {
            val factor = 0.32f / lum.coerceAtLeast(0.01f)
            Color(
                red = (r * factor).coerceIn(0f, 0.42f),
                green = (g * factor).coerceIn(0f, 0.42f),
                blue = (b * factor).coerceIn(0f, 0.42f),
                alpha = 1f
            )
        } else color
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    customFontColorHex: String = "DEFAULT",
    fontSizeScale: Float = 1.0f,
    accentColorHex: String = "DEFAULT",
    content: @Composable () -> Unit,
) {
    val baseColorScheme = if (darkTheme) RabbitDarkColorScheme else RabbitLightColorScheme
    val isAutoDefault = customFontColorHex.isBlank() ||
            customFontColorHex.equals("DEFAULT", ignoreCase = true) ||
            customFontColorHex.equals("#DEFAULT", ignoreCase = true)

    val effectiveFontColor = if (isAutoDefault) {
        // Automatic default: Crisp white on dark, deep slate navy on light
        if (darkTheme) TextPrimary else LightTextPrimary
    } else {
        val parsed = parseCustomFontColor(customFontColorHex, if (darkTheme) TextPrimary else LightTextPrimary)
        ensureReadableFontColor(parsed, darkTheme)
    }

    val isAutoAccent = accentColorHex.isBlank() ||
            accentColorHex.equals("DEFAULT", ignoreCase = true) ||
            accentColorHex.equals("#DEFAULT", ignoreCase = true)

    val effectiveAccent = if (isAutoAccent) {
        // Automatic default: Mint on dark, Azure on light
        if (darkTheme) RabbitMint else Color(0xFF0284C7)
    } else {
        parseCustomFontColor(accentColorHex, if (darkTheme) RabbitMint else Color(0xFF0284C7))
    }

    val colorScheme = baseColorScheme.copy(
        primary = effectiveAccent,
        onBackground = effectiveFontColor,
        onSurface = effectiveFontColor
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        @Suppress("DEPRECATION")
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val barColor = if (darkTheme) RabbitObsidian.toArgb() else LightBackground.toArgb()
                window.statusBarColor = barColor
                window.navigationBarColor = barColor
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    val baseTypography = Typography
    val scaledTypography = if (fontSizeScale != 1.0f) {
        androidx.compose.material3.Typography(
            displayLarge = baseTypography.displayLarge.copy(fontSize = baseTypography.displayLarge.fontSize * fontSizeScale),
            displayMedium = baseTypography.displayMedium.copy(fontSize = baseTypography.displayMedium.fontSize * fontSizeScale),
            displaySmall = baseTypography.displaySmall.copy(fontSize = baseTypography.displaySmall.fontSize * fontSizeScale),
            headlineLarge = baseTypography.headlineLarge.copy(fontSize = baseTypography.headlineLarge.fontSize * fontSizeScale),
            headlineMedium = baseTypography.headlineMedium.copy(fontSize = baseTypography.headlineMedium.fontSize * fontSizeScale),
            headlineSmall = baseTypography.headlineSmall.copy(fontSize = baseTypography.headlineSmall.fontSize * fontSizeScale),
            titleLarge = baseTypography.titleLarge.copy(fontSize = baseTypography.titleLarge.fontSize * fontSizeScale),
            titleMedium = baseTypography.titleMedium.copy(fontSize = baseTypography.titleMedium.fontSize * fontSizeScale),
            titleSmall = baseTypography.titleSmall.copy(fontSize = baseTypography.titleSmall.fontSize * fontSizeScale),
            bodyLarge = baseTypography.bodyLarge.copy(fontSize = baseTypography.bodyLarge.fontSize * fontSizeScale),
            bodyMedium = baseTypography.bodyMedium.copy(fontSize = baseTypography.bodyMedium.fontSize * fontSizeScale),
            bodySmall = baseTypography.bodySmall.copy(fontSize = baseTypography.bodySmall.fontSize * fontSizeScale),
            labelLarge = baseTypography.labelLarge.copy(fontSize = baseTypography.labelLarge.fontSize * fontSizeScale),
            labelMedium = baseTypography.labelMedium.copy(fontSize = baseTypography.labelMedium.fontSize * fontSizeScale),
            labelSmall = baseTypography.labelSmall.copy(fontSize = baseTypography.labelSmall.fontSize * fontSizeScale)
        )
    } else {
        baseTypography
    }

    CompositionLocalProvider(
        LocalIsDarkTheme provides darkTheme,
        LocalCustomFontColor provides effectiveFontColor,
        LocalFontSizeScale provides fontSizeScale,
        LocalAccentColor provides effectiveAccent
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = scaledTypography,
            content = content
        )
    }
}
