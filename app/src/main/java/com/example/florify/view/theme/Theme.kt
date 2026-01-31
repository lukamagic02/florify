package com.example.florify.view.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = BrightSage,
    onPrimary = DarkForestGreen,
    primaryContainer = MediumForestGreen,
    onPrimaryContainer = LightSage,

    secondary = LightBeige,
    onSecondary = DarkBeige,
    secondaryContainer = MediumBeige,
    onSecondaryContainer = LightEarth,

    tertiary = AquaMint,
    onTertiary = DarkAqua,
    tertiaryContainer = MediumAqua,
    onTertiaryContainer = LightMint,

    surface = DarkSurface,
    onSurface = LightOnDark,
    surfaceVariant = DarkNeutral,
    onSurfaceVariant = LightNeutral,

    background = DarkSurface,
    onBackground = LightOnDark,

    error = ErrorLight,
    onError = ErrorDark,
    errorContainer = ErrorDark,
    onErrorContainer = ErrorLight,

    outline = OutlineGreen,
    outlineVariant = DarkNeutral
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = LightSage,
    onPrimaryContainer = VeryDarkGreen,

    secondary = EarthBrown,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = LightEarth,
    onSecondaryContainer = DarkEarth,

    tertiary = MutedTeal,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = LightMint,
    onTertiaryContainer = DarkTeal,

    surface = SoftWhite,
    onSurface = AlmostBlack,
    surfaceVariant = LightNeutral,
    onSurfaceVariant = DarkNeutral,

    background = SoftWhite,
    onBackground = AlmostBlack,

    error = ErrorRed,
    onError = androidx.compose.ui.graphics.Color.White,
    errorContainer = ErrorLight,
    onErrorContainer = ErrorDark,

    outline = OutlineGreen,
    outlineVariant = OutlineVariant
)

@Composable
fun FlorifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color disabled to maintain consistent botanical branding
    dynamicColor: Boolean = false,
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}