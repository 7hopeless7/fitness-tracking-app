package com.fitapp.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = BrandPrimary,
    onPrimary = BrandOnPrimary,
    secondary = BrandAccent,
    background = BrandDark,
    surface = BrandSurface,
    surfaceVariant = BrandSurfaceVariant,
    onBackground = BrandOnDark,
    onSurface = BrandOnDark,
    outline = BrandOutline,
    error = BrandError
)

@Composable
fun FitAppAndroidTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}