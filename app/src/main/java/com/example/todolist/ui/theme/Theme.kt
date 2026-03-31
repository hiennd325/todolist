package com.example.todolist.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    outline = Outline,
    outlineVariant = OutlineVariant
)

fun createTypography(fontSize: String): Typography {
    val scaleFactor = when (fontSize) {
        "small" -> 0.85f
        "large" -> 1.15f
        "extra_large" -> 1.3f
        else -> 1.0f
    }

    return Typography(
        titleLarge = TextStyle(
            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            fontSize = (22.sp.value * scaleFactor).sp,
            lineHeight = (28.sp.value * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
            fontSize = (18.sp.value * scaleFactor).sp,
            lineHeight = (24.sp.value * scaleFactor).sp,
            letterSpacing = 0.15.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
            fontSize = (16.sp.value * scaleFactor).sp,
            lineHeight = (24.sp.value * scaleFactor).sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
            fontSize = (14.sp.value * scaleFactor).sp,
            lineHeight = (20.sp.value * scaleFactor).sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
            fontSize = (12.sp.value * scaleFactor).sp,
            lineHeight = (16.sp.value * scaleFactor).sp,
            letterSpacing = 0.4.sp
        ),
        labelLarge = TextStyle(
            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
            fontSize = (14.sp.value * scaleFactor).sp,
            lineHeight = (20.sp.value * scaleFactor).sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
            fontSize = (12.sp.value * scaleFactor).sp,
            lineHeight = (16.sp.value * scaleFactor).sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
            fontSize = (11.sp.value * scaleFactor).sp,
            lineHeight = (16.sp.value * scaleFactor).sp,
            letterSpacing = 0.5.sp
        )
    )
}

fun createCustomColorScheme(primaryColor: Color, darkTheme: Boolean): ColorScheme {
    val baseScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    return baseScheme.copy(
        primary = primaryColor,
        onPrimary = if (darkTheme) Color.Black else Color.White,
        primaryContainer = lerp(primaryColor, if (darkTheme) Color.Black else Color.White, 0.7f),
        onPrimaryContainer = if (darkTheme) Color.White else Color.Black
    )
}

@Composable
fun TodolistTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    fontSize: String = "medium",
    customPrimaryColor: Color? = null,
    customPrimaryDarkColor: Color? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        customPrimaryColor != null -> {
            createCustomColorScheme(
                primaryColor = if (darkTheme) (customPrimaryDarkColor ?: customPrimaryColor) else customPrimaryColor,
                darkTheme = darkTheme
            )
        }

        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = createTypography(fontSize),
        content = content
    )
}
