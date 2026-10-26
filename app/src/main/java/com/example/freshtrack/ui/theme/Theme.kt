package com.example.freshtrack.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF1B6B4A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB4F1CD),
    onPrimaryContainer = Color(0xFF002114),
    secondary = Color(0xFF4D6357),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCFE9D9),
    onSecondaryContainer = Color(0xFF0A1F15),
    tertiary = Color(0xFF7A5900),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDF9E),
    onTertiaryContainer = Color(0xFF261A00),
    background = Color(0xFFF6FBF5),
    onBackground = Color(0xFF171D19),
    surface = Color(0xFFF6FBF5),
    onSurface = Color(0xFF171D19),
    surfaceVariant = Color(0xFFDBE5DC),
    onSurfaceVariant = Color(0xFF404943),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF0F5F0),
    surfaceContainer = Color(0xFFEAEFEA),
    surfaceContainerHigh = Color(0xFFE4EAE4),
    surfaceContainerHighest = Color(0xFFDFE4DF),
    outline = Color(0xFF707972),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF99D6B3),
    onPrimary = Color(0xFF00391F),
    primaryContainer = Color(0xFF00522F),
    onPrimaryContainer = Color(0xFFB4F1CD),
    secondary = Color(0xFFB3CCBD),
    onSecondary = Color(0xFF1F352A),
    secondaryContainer = Color(0xFF354B3F),
    onSecondaryContainer = Color(0xFFCFE9D9),
    tertiary = Color(0xFFEFC24A),
    onTertiary = Color(0xFF402D00),
    tertiaryContainer = Color(0xFF5C4300),
    onTertiaryContainer = Color(0xFFFFDF9E),
    background = Color(0xFF0F1511),
    onBackground = Color(0xFFDEE4DD),
    surface = Color(0xFF0F1511),
    onSurface = Color(0xFFDEE4DD),
    surfaceVariant = Color(0xFF404943),
    onSurfaceVariant = Color(0xFFBFC9C1),
    surfaceContainerLowest = Color(0xFF0A0F0C),
    surfaceContainerLow = Color(0xFF171D19),
    surfaceContainer = Color(0xFF1B211D),
    surfaceContainerHigh = Color(0xFF262B28),
    surfaceContainerHighest = Color(0xFF303632),
    outline = Color(0xFF8A938C),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun FreshTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
