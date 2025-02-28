package com.example.carrentapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    error = ErrorLight,
    onError = OnErrorLight,
    surfaceVariant = Color(0xFFE0E4E9), // Subtle gray for inputs
    outline = Color(0xFFB0BEC5)        // Border color
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    error = ErrorDark,
    onError = OnErrorDark,
    surfaceVariant = Color(0xFF294057), // Darker shade for inputs
    outline = Color(0xFF78909C)        // Border color
)

// Share dark mode state (for toggle or checking)
val LocalDarkMode = compositionLocalOf { false }

@Composable
fun CarRentAppTheme(
    useDarkTheme: Boolean? = null,
    //darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Decide dark mode: useDarkTheme if set, otherwise system setting
    val isDarkTheme = useDarkTheme ?: isSystemInDarkTheme()

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Apply the theme to the app
    CompositionLocalProvider(LocalDarkMode provides isDarkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography, // Default fonts (customizable in Typography.kt)
            content = content
        )
    }
}