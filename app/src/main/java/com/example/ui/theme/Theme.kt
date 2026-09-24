package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldDarkPrimary,
    onPrimary = Color(0xFF003825),
    primaryContainer = Color(0xFF005238),
    onPrimaryContainer = EmeraldContainer,
    secondary = GoldDarkAccent,
    onSecondary = Color(0xFF3B2D00),
    secondaryContainer = Color(0xFF554200),
    onSecondaryContainer = GoldContainer,
    tertiary = Color(0xFF7DD0EE),
    background = EmeraldDarkBackground,
    onBackground = Color(0xFFE1E3DF),
    surface = EmeraldDarkSurface,
    onSurface = Color(0xFFE1E3DF),
    surfaceVariant = EmeraldDarkCard,
    onSurfaceVariant = Color(0xFFC1C9C2)
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = OnEmeraldContainer,
    secondary = GoldAccent,
    onSecondary = Color.White,
    secondaryContainer = GoldContainer,
    onSecondaryContainer = OnGoldContainer,
    tertiary = DomeBlue,
    onTertiary = Color.White,
    tertiaryContainer = DomeBlueContainer,
    background = IslamicBackgroundLight,
    onBackground = Color(0xFF191C1A),
    surface = IslamicSurfaceLight,
    onSurface = Color(0xFF191C1A),
    surfaceVariant = Color(0xFFE8EFEA),
    onSurfaceVariant = Color(0xFF404943)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep spiritual theme consistent
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
