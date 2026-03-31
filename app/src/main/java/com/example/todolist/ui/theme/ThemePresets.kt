package com.example.todolist.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

sealed class ThemePreset {
    object Default : ThemePreset()
    object Ocean : ThemePreset()
    object Sunset : ThemePreset()
    object Forest : ThemePreset()
    object Berry : ThemePreset()
    object Lavender : ThemePreset()

    companion object {
        fun fromString(value: String): ThemePreset {
            return when (value) {
                "Ocean" -> Ocean
                "Sunset" -> Sunset
                "Forest" -> Forest
                "Berry" -> Berry
                "Lavender" -> Lavender
                else -> Default
            }
        }

        fun toString(preset: ThemePreset): String {
            return when (preset) {
                Ocean -> "Ocean"
                Sunset -> "Sunset"
                Forest -> "Forest"
                Berry -> "Berry"
                Lavender -> "Lavender"
                Default -> "Default"
            }
    }
}

private fun defaultLightColorScheme(): ColorScheme = lightColorScheme(
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

private fun defaultDarkColorScheme(): ColorScheme = darkColorScheme(
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
}

// Ocean preset colors - blues and teals
private val OceanPrimary = Color(0xFF0D47A1)
private val OceanOnPrimary = Color(0xFFFFFFFF)
private val OceanPrimaryContainer = Color(0xFFBBDEFB)
private val OceanOnPrimaryContainer = Color(0xFF001D3D)

private val OceanSecondary = Color(0xFF0288D1)
private val OceanOnSecondary = Color(0xFFFFFFFF)
private val OceanSecondaryContainer = Color(0xFFB3E5FC)
private val OceanOnSecondaryContainer = Color(0xFF00354A)

private val OceanTertiary = Color(0xFF26C6DA)
private val OceanOnTertiary = Color(0xFF00363A)
private val OceanTertiaryContainer = Color(0xFF80DEEA)
private val OceanOnTertiaryContainer = Color(0xFF00211F)

private val OceanBackground = Color(0xFFFFFFFF)
private val OceanOnBackground = Color(0xFF0A1929)
private val OceanSurface = Color(0xFFFFFFFF)
private val OceanOnSurface = Color(0xFF0A1929)
private val OceanSurfaceVariant = Color(0xFFE3F2FD)
private val OceanOnSurfaceVariant = Color(0xFF4A5568)

private val OceanError = Color(0xFFBA1A1A)
private val OceanOnError = Color(0xFFFFFFFF)
private val OceanErrorContainer = Color(0xFFFFDAD6)
private val OceanOnErrorContainer = Color(0xFF410002)
private val OceanOutline = Color(0xFF8C92A1)
private val OceanOutlineVariant = Color(0xFFCAC4D4)

// Ocean Dark
private val OceanPrimaryDark = Color(0xFF90CAF9)
private val OceanOnPrimaryDark = Color(0xFF003258)
private val OceanPrimaryContainerDark = Color(0xFF0D47A1)
private val OceanOnPrimaryContainerDark = Color(0xFFBBDEFB)

private val OceanSecondaryDark = Color(0xFF4FC3F7)
private val OceanOnSecondaryDark = Color(0xFF003546)
private val OceanSecondaryContainerDark = Color(0xFF0288D1)
private val OceanOnSecondaryContainerDark = Color(0xFFB3E5FC)

private val OceanTertiaryDark = Color(0xFF6EE7E3)
private val OceanOnTertiaryDark = Color(0xFF003A3E)
private val OceanTertiaryContainerDark = Color(0xFF26C6DA)
private val OceanOnTertiaryContainerDark = Color(0xFF80DEEA)

private val OceanBackgroundDark = Color(0xFF0A1929)
private val OceanOnBackgroundDark = Color(0xFFE8EEF4)
private val OceanSurfaceDark = Color(0xFF0A1929)
private val OceanOnSurfaceDark = Color(0xFFE8EEF4)
private val OceanSurfaceVariantDark = Color(0xFF1E2D39)
private val OceanOnSurfaceVariantDark = Color(0xFFBFC9D8)

private val OceanErrorDark = Color(0xFFFFB4AB)
private val OceanOnErrorDark = Color(0xFF690005)
private val OceanErrorContainerDark = Color(0xFF93000A)
private val OceanOnErrorContainerDark = Color(0xFFFFDAD6)
private val OceanOutlineDark = Color(0xFF6F7A8C)
private val OceanOutlineVariantDark = Color(0xFF495670)

// Sunset preset colors - oranges, purples, pinks
private val SunsetPrimary = Color(0xFFFF6D00)
private val SunsetOnPrimary = Color(0xFFFFFFFF)
private val SunsetPrimaryContainer = Color(0xFFFFD180)
private val SunsetOnPrimaryContainer = Color(0xFF331100)

private val SunsetSecondary = Color(0xFF9C27B0)
private val SunsetOnSecondary = Color(0xFFFFFFFF)
private val SunsetSecondaryContainer = Color(0xFFE1BEE7)
private val SunsetOnSecondaryContainer = Color(0xFF2E0548)

private val SunsetTertiary = Color(0xFFFF4081)
private val SunsetOnTertiary = Color(0xFFFFFFFF)
private val SunsetTertiaryContainer = Color(0xFFFFB2DD)
private val SunsetOnTertiaryContainer = Color(0xFF4A001C)

private val SunsetBackground = Color(0xFFFFFBF5)
private val SunsetOnBackground = Color(0xFF2C1A0A)
private val SunsetSurface = Color(0xFFFFFFFF)
private val SunsetOnSurface = Color(0xFF2C1A0A)
private val SunsetSurfaceVariant = Color(0xFFFFEEDD)
private val SunsetOnSurfaceVariant = Color(0xFF59463F)

private val SunsetError = Color(0xFFBA1A1A)
private val SunsetOnError = Color(0xFFFFFFFF)
private val SunsetErrorContainer = Color(0xFFFFDAD6)
private val SunsetOnErrorContainer = Color(0xFF410002)
private val SunsetOutline = Color(0xFFE0A684)
private val SunsetOutlineVariant = Color(0xFFD7C8BA)

// Sunset Dark
private val SunsetPrimaryDark = Color(0xFFFFB74D)
private val SunsetOnPrimaryDark = Color(0xFF532100)
private val SunsetPrimaryContainerDark = Color(0xFFFF6D00)
private val SunsetOnPrimaryContainerDark = Color(0xFFFFD180)

private val SunsetSecondaryDark = Color(0xFFBA68C8)
private val SunsetOnSecondaryDark = Color(0xFF421254)
private val SunsetSecondaryContainerDark = Color(0xFF9C27B0)
private val SunsetOnSecondaryContainerDark = Color(0xFFE1BEE7)

private val SunsetTertiaryDark = Color(0xFFFF80AB)
private val SunsetOnTertiaryDark = Color(0xFF4A0029)
private val SunsetTertiaryContainerDark = Color(0xFFFF4081)
private val SunsetOnTertiaryContainerDark = Color(0xFFFFB2DD)

private val SunsetBackgroundDark = Color(0xFF2C1A0A)
private val SunsetOnBackgroundDark = Color(0xFFFFF0E6)
private val SunsetSurfaceDark = Color(0xFF2C1A0A)
private val SunsetOnSurfaceDark = Color(0xFFFFF0E6)
private val SunsetSurfaceVariantDark = Color(0xFF4A3830)
private val SunsetOnSurfaceVariantDark = Color(0xFFD8C5B8)

private val SunsetErrorDark = Color(0xFFFFB4AB)
private val SunsetOnErrorDark = Color(0xFF690005)
private val SunsetErrorContainerDark = Color(0xFF93000A)
private val SunsetOnErrorContainerDark = Color(0xFFFFDAD6)
private val SunsetOutlineDark = Color(0xFFBFA088)
private val SunsetOutlineVariantDark = Color(0xFF8A7064)

// Forest preset colors - greens and browns
private val ForestPrimary = Color(0xFF2E7D32)
private val ForestOnPrimary = Color(0xFFFFFFFF)
private val ForestPrimaryContainer = Color(0xFFC8E6C9)
private val ForestOnPrimaryContainer = Color(0xFF08200B)

private val ForestSecondary = Color(0xFF558B2F)
private val ForestOnSecondary = Color(0xFFFFFFFF)
private val ForestSecondaryContainer = Color(0xFFDCE775)
private val ForestOnSecondaryContainer = Color(0xFF161F00)

private val ForestTertiary = Color(0xFF8D6E63)
private val ForestOnTertiary = Color(0xFFFFFFFF)
private val ForestTertiaryContainer = Color(0xFFFFEBE0)
private val ForestOnTertiaryContainer = Color(0xFF2B170B)

private val ForestBackground = Color(0xFFFFFBFF)
private val ForestOnBackground = Color(0xFF161D12)
private val ForestSurface = Color(0xFFFFFFFF)
private val ForestOnSurface = Color(0xFF161D12)
private val ForestSurfaceVariant = Color(0xFFE0E8D8)
private val ForestOnSurfaceVariant = Color(0xFF454B42)

private val ForestError = Color(0xFFBA1A1A)
private val ForestOnError = Color(0xFFFFFFFF)
private val ForestErrorContainer = Color(0xFFFFDAD6)
private val ForestOnErrorContainer = Color(0xFF410002)
private val ForestOutline = Color(0xFF767870)
private val ForestOutlineVariant = Color(0xFFC6C8BF)

// Forest Dark
private val ForestPrimaryDark = Color(0xFF81C784)
private val ForestOnPrimaryDark = Color(0xFF003910)
private val ForestPrimaryContainerDark = Color(0xFF2E7D32)
private val ForestOnPrimaryContainerDark = Color(0xFFC8E6C9)

private val ForestSecondaryDark = Color(0xFFAED581)
private val ForestOnSecondaryDark = Color(0xFF1B2E00)
private val ForestSecondaryContainerDark = Color(0xFF558B2F)
private val ForestOnSecondaryContainerDark = Color(0xFFDCE775)

private val ForestTertiaryDark = Color(0xFFBCAAA4)
private val ForestOnTertiaryDark = Color(0xFF3A2720)
private val ForestTertiaryContainerDark = Color(0xFF8D6E63)
private val ForestOnTertiaryContainerDark = Color(0xFFFFEBE0)

private val ForestBackgroundDark = Color(0xFF161D12)
private val ForestOnBackgroundDark = Color(0xFFE2E8DE)
private val ForestSurfaceDark = Color(0xFF161D12)
private val ForestOnSurfaceDark = Color(0xFFE2E8DE)
private val ForestSurfaceVariantDark = Color(0xFF2C3328)
private val ForestOnSurfaceVariantDark = Color(0xFFBFC8B4)

private val ForestErrorDark = Color(0xFFFFB4AB)
private val ForestOnErrorDark = Color(0xFF690005)
private val ForestErrorContainerDark = Color(0xFF93000A)
private val ForestOnErrorContainerDark = Color(0xFFFFDAD6)
private val ForestOutlineDark = Color(0xFF8F9289)
private val ForestOutlineVariantDark = Color(0xFF6C7067)

// Berry preset colors - purples and pinks
private val BerryPrimary = Color(0xFF8E24AA)
private val BerryOnPrimary = Color(0xFFFFFFFF)
private val BerryPrimaryContainer = Color(0xFFF3E5F5)
private val BerryOnPrimaryContainer = Color(0xFF22005D)

private val BerrySecondary = Color(0xFFD81B60)
private val BerryOnSecondary = Color(0xFFFFFFFF)
private val BerrySecondaryContainer = Color(0xFFFFEBF2)
private val BerryOnSecondaryContainer = Color(0xFF4A001F)

private val BerryTertiary = Color(0xFFF06292)
private val BerryOnTertiary = Color(0xFF4A1C35)
private val BerryTertiaryContainer = Color(0xFFFFD1E9)
private val BerryOnTertiaryContainer = Color(0xFF3E0B28)

private val BerryBackground = Color(0xFFFFF7FB)
private val BerryOnBackground = Color(0xFF241428)
private val BerrySurface = Color(0xFFFFFFFF)
private val BerryOnSurface = Color(0xFF241428)
private val BerrySurfaceVariant = Color(0xFFF3E5F5)
private val BerryOnSurfaceVariant = Color(0xFF5A4A61)

private val BerryError = Color(0xFFBA1A1A)
private val BerryOnError = Color(0xFFFFFFFF)
private val BerryErrorContainer = Color(0xFFFFDAD6)
private val BerryOnErrorContainer = Color(0xFF410002)
private val BerryOutline = Color(0xFF9C8A9F)
private val BerryOutlineVariant = Color(0xFFD1C2D6)

// Berry Dark
private val BerryPrimaryDark = Color(0xFFCE93D8)
private val BerryOnPrimaryDark = Color(0xFF3E0B43)
private val BerryPrimaryContainerDark = Color(0xFF8E24AA)
private val BerryOnPrimaryContainerDark = Color(0xFFF3E5F5)

private val BerrySecondaryDark = Color(0xFFFF4081)
private val BerryOnSecondaryDark = Color(0xFF4A001F)
private val BerrySecondaryContainerDark = Color(0xFFD81B60)
private val BerryOnSecondaryContainerDark = Color(0xFFFFEBF2)

private val BerryTertiaryDark = Color(0xFFFF8FAF)
private val BerryOnTertiaryDark = Color(0xFF521B36)
private val BerryTertiaryContainerDark = Color(0xFFF06292)
private val BerryOnTertiaryContainerDark = Color(0xFFFFD1E9)

private val BerryBackgroundDark = Color(0xFF241428)
private val BerryOnBackgroundDark = Color(0xFFF2DEF7)
private val BerrySurfaceDark = Color(0xFF241428)
private val BerryOnSurfaceDark = Color(0xFFF2DEF7)
private val BerrySurfaceVariantDark = Color(0xFF3E2A45)
private val BerryOnSurfaceVariantDark = Color(0xFFC5B8C9)

private val BerryErrorDark = Color(0xFFFFB4AB)
private val BerryOnErrorDark = Color(0xFF690005)
private val BerryErrorContainerDark = Color(0xFF93000A)
private val BerryOnErrorContainerDark = Color(0xFFFFDAD6)
private val BerryOutlineDark = Color(0xFF9E8D9F)
private val BerryOutlineVariantDark = Color(0xFF837086)

// Lavender preset colors - purples and lavenders
private val LavenderPrimary = Color(0xFF673AB7)
private val LavenderOnPrimary = Color(0xFFFFFFFF)
private val LavenderPrimaryContainer = Color(0xFFEDE7F6)
private val LavenderOnPrimaryContainer = Color(0xFF1A0C32)

private val LavenderSecondary = Color(0xFF7E57C2)
private val LavenderOnSecondary = Color(0xFFFFFFFF)
private val LavenderSecondaryContainer = Color(0xFFEDE7F6)
private val LavenderOnSecondaryContainer = Color(0xFF1D2B4E)

private val LavenderTertiary = Color(0xFF9575CD)
private val LavenderOnTertiary = Color(0xFFFFFFFF)
private val LavenderTertiaryContainer = Color(0xFFEDE7F6)
private val LavenderOnTertiaryContainer = Color(0xFF2B2040)

private val LavenderBackground = Color(0xFFFFFBFF)
private val LavenderOnBackground = Color(0xFF1A1920)
private val LavenderSurface = Color(0xFFFFFFFF)
private val LavenderOnSurface = Color(0xFF1A1920)
private val LavenderSurfaceVariant = Color(0xFFEBE8EF)
private val LavenderOnSurfaceVariant = Color(0xFF4B4657)

private val LavenderError = Color(0xFFBA1A1A)
private val LavenderOnError = Color(0xFFFFFFFF)
private val LavenderErrorContainer = Color(0xFFFFDAD6)
private val LavenderOnErrorContainer = Color(0xFF410002)
private val LavenderOutline = Color(0xFF938A99)
private val LavenderOutlineVariant = Color(0xFFCAC4D4)

// Lavender Dark
private val LavenderPrimaryDark = Color(0xFFB39DDB)
private val LavenderOnPrimaryDark = Color(0xFF29223D)
private val LavenderPrimaryContainerDark = Color(0xFF673AB7)
private val LavenderOnPrimaryContainerDark = Color(0xFFEDE7F6)

private val LavenderSecondaryDark = Color(0xFF9575CD)
private val LavenderOnSecondaryDark = Color(0xFF29223D)
private val LavenderSecondaryContainerDark = Color(0xFF7E57C2)
private val LavenderOnSecondaryContainerDark = Color(0xFFEDE7F6)

private val LavenderTertiaryDark = Color(0xFFAF7AC5)
private val LavenderOnTertiaryDark = Color(0xFF332440)
private val LavenderTertiaryContainerDark = Color(0xFF9575CD)
private val LavenderOnTertiaryContainerDark = Color(0xFFEDE7F6)

private val LavenderBackgroundDark = Color(0xFF1A1920)
private val LavenderOnBackgroundDark = Color(0xFFE6E1E5)
private val LavenderSurfaceDark = Color(0xFF1A1920)
private val LavenderOnSurfaceDark = Color(0xFFE6E1E5)
private val LavenderSurfaceVariantDark = Color(0xFF373543)
private val LavenderOnSurfaceVariantDark = Color(0xFFC5C0C9)

private val LavenderErrorDark = Color(0xFFFFB4AB)
private val LavenderOnErrorDark = Color(0xFF690005)
private val LavenderErrorContainerDark = Color(0xFF93000A)
private val LavenderOnErrorContainerDark = Color(0xFFFFDAD6)
private val LavenderOutlineDark = Color(0xFF7D7586)
private val LavenderOutlineVariantDark = Color(0xFF65616E)

internal fun getColorScheme(preset: ThemePreset, darkTheme: Boolean): ColorScheme {
    return when (preset) {
        ThemePreset.Default -> if (darkTheme) defaultDarkColorScheme() else defaultLightColorScheme()
        ThemePreset.Ocean -> if (darkTheme) oceanDarkColorScheme() else oceanLightColorScheme()
        ThemePreset.Sunset -> if (darkTheme) sunsetDarkColorScheme() else sunsetLightColorScheme()
        ThemePreset.Forest -> if (darkTheme) forestDarkColorScheme() else forestLightColorScheme()
        ThemePreset.Berry -> if (darkTheme) berryDarkColorScheme() else berryLightColorScheme()
        ThemePreset.Lavender -> if (darkTheme) lavenderDarkColorScheme() else lavenderLightColorScheme()
    }
}

private fun defaultLightColorScheme(): ColorScheme = lightColorScheme(
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

private fun defaultDarkColorScheme(): ColorScheme = darkColorScheme(
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

private fun oceanLightColorScheme(): ColorScheme = lightColorScheme(
    primary = OceanPrimary,
    onPrimary = OceanOnPrimary,
    primaryContainer = OceanPrimaryContainer,
    onPrimaryContainer = OceanOnPrimaryContainer,
    secondary = OceanSecondary,
    onSecondary = OceanOnSecondary,
    secondaryContainer = OceanSecondaryContainer,
    onSecondaryContainer = OceanOnSecondaryContainer,
    tertiary = OceanTertiary,
    onTertiary = OceanOnTertiary,
    tertiaryContainer = OceanTertiaryContainer,
    onTertiaryContainer = OceanOnTertiaryContainer,
    background = OceanBackground,
    onBackground = OceanOnBackground,
    surface = OceanSurface,
    onSurface = OceanOnSurface,
    surfaceVariant = OceanSurfaceVariant,
    onSurfaceVariant = OceanOnSurfaceVariant,
    error = OceanError,
    onError = OceanOnError,
    errorContainer = OceanErrorContainer,
    onErrorContainer = OceanOnErrorContainer,
    outline = OceanOutline,
    outlineVariant = OceanOutlineVariant
)

private fun oceanDarkColorScheme(): ColorScheme = darkColorScheme(
    primary = OceanPrimaryDark,
    onPrimary = OceanOnPrimaryDark,
    primaryContainer = OceanPrimaryContainerDark,
    onPrimaryContainer = OceanOnPrimaryContainerDark,
    secondary = OceanSecondaryDark,
    onSecondary = OceanOnSecondaryDark,
    secondaryContainer = OceanSecondaryContainerDark,
    onSecondaryContainer = OceanOnSecondaryContainerDark,
    tertiary = OceanTertiaryDark,
    onTertiary = OceanOnTertiaryDark,
    tertiaryContainer = OceanTertiaryContainerDark,
    onTertiaryContainer = OceanOnTertiaryContainerDark,
    background = OceanBackgroundDark,
    onBackground = OceanOnBackgroundDark,
    surface = OceanSurfaceDark,
    onSurface = OceanOnSurfaceDark,
    surfaceVariant = OceanSurfaceVariantDark,
    onSurfaceVariant = OceanOnSurfaceVariantDark,
    error = OceanErrorDark,
    onError = OceanOnErrorDark,
    errorContainer = OceanErrorContainerDark,
    onErrorContainer = OceanOnErrorContainerDark,
    outline = OceanOutlineDark,
    outlineVariant = OceanOutlineVariantDark
)

private fun sunsetLightColorScheme(): ColorScheme = lightColorScheme(
    primary = SunsetPrimary,
    onPrimary = SunsetOnPrimary,
    primaryContainer = SunsetPrimaryContainer,
    onPrimaryContainer = SunsetOnPrimaryContainer,
    secondary = SunsetSecondary,
    onSecondary = SunsetOnSecondary,
    secondaryContainer = SunsetSecondaryContainer,
    onSecondaryContainer = SunsetOnSecondaryContainer,
    tertiary = SunsetTertiary,
    onTertiary = SunsetOnTertiary,
    tertiaryContainer = SunsetTertiaryContainer,
    onTertiaryContainer = SunsetOnTertiaryContainer,
    background = SunsetBackground,
    onBackground = SunsetOnBackground,
    surface = SunsetSurface,
    onSurface = SunsetOnSurface,
    surfaceVariant = SunsetSurfaceVariant,
    onSurfaceVariant = SunsetOnSurfaceVariant,
    error = SunsetError,
    onError = SunsetOnError,
    errorContainer = SunsetErrorContainer,
    onErrorContainer = SunsetOnErrorContainer,
    outline = SunsetOutline,
    outlineVariant = SunsetOutlineVariant
)

private fun sunsetDarkColorScheme(): ColorScheme = darkColorScheme(
    primary = SunsetPrimaryDark,
    onPrimary = SunsetOnPrimaryDark,
    primaryContainer = SunsetPrimaryContainerDark,
    onPrimaryContainer = SunsetOnPrimaryContainerDark,
    secondary = SunsetSecondaryDark,
    onSecondary = SunsetOnSecondaryDark,
    secondaryContainer = SunsetSecondaryContainerDark,
    onSecondaryContainer = SunsetOnSecondaryContainerDark,
    tertiary = SunsetTertiaryDark,
    onTertiary = SunsetOnTertiaryDark,
    tertiaryContainer = SunsetTertiaryContainerDark,
    onTertiaryContainer = SunsetOnTertiaryContainerDark,
    background = SunsetBackgroundDark,
    onBackground = SunsetOnBackgroundDark,
    surface = SunsetSurfaceDark,
    onSurface = SunsetOnSurfaceDark,
    surfaceVariant = SunsetSurfaceVariantDark,
    onSurfaceVariant = SunsetOnSurfaceVariantDark,
    error = SunsetErrorDark,
    onError = SunsetOnErrorDark,
    errorContainer = SunsetErrorContainerDark,
    onErrorContainer = SunsetOnErrorContainerDark,
    outline = SunsetOutlineDark,
    outlineVariant = SunsetOutlineVariantDark
)

private fun forestLightColorScheme(): ColorScheme = lightColorScheme(
    primary = ForestPrimary,
    onPrimary = ForestOnPrimary,
    primaryContainer = ForestPrimaryContainer,
    onPrimaryContainer = ForestOnPrimaryContainer,
    secondary = ForestSecondary,
    onSecondary = ForestOnSecondary,
    secondaryContainer = ForestSecondaryContainer,
    onSecondaryContainer = ForestOnSecondaryContainer,
    tertiary = ForestTertiary,
    onTertiary = ForestOnTertiary,
    tertiaryContainer = ForestTertiaryContainer,
    onTertiaryContainer = ForestOnTertiaryContainer,
    background = ForestBackground,
    onBackground = ForestOnBackground,
    surface = ForestSurface,
    onSurface = ForestOnSurface,
    surfaceVariant = ForestSurfaceVariant,
    onSurfaceVariant = ForestOnSurfaceVariant,
    error = ForestError,
    onError = ForestOnError,
    errorContainer = ForestErrorContainer,
    onErrorContainer = ForestOnErrorContainer,
    outline = ForestOutline,
    outlineVariant = ForestOutlineVariant
)

private fun forestDarkColorScheme(): ColorScheme = darkColorScheme(
    primary = ForestPrimaryDark,
    onPrimary = ForestOnPrimaryDark,
    primaryContainer = ForestPrimaryContainerDark,
    onPrimaryContainer = ForestOnPrimaryContainerDark,
    secondary = ForestSecondaryDark,
    onSecondary = ForestOnSecondaryDark,
    secondaryContainer = ForestSecondaryContainerDark,
    onSecondaryContainer = ForestOnSecondaryContainerDark,
    tertiary = ForestTertiaryDark,
    onTertiary = ForestOnTertiaryDark,
    tertiaryContainer = ForestTertiaryContainerDark,
    onTertiaryContainer = ForestOnTertiaryContainerDark,
    background = ForestBackgroundDark,
    onBackground = ForestOnBackgroundDark,
    surface = ForestSurfaceDark,
    onSurface = ForestOnSurfaceDark,
    surfaceVariant = ForestSurfaceVariantDark,
    onSurfaceVariant = ForestOnSurfaceVariantDark,
    error = ForestErrorDark,
    onError = ForestOnErrorDark,
    errorContainer = ForestErrorContainerDark,
    onErrorContainer = ForestOnErrorContainerDark,
    outline = ForestOutlineDark,
    outlineVariant = ForestOutlineVariantDark
)

private fun berryLightColorScheme(): ColorScheme = lightColorScheme(
    primary = BerryPrimary,
    onPrimary = BerryOnPrimary,
    primaryContainer = BerryPrimaryContainer,
    onPrimaryContainer = BerryOnPrimaryContainer,
    secondary = BerrySecondary,
    onSecondary = BerryOnSecondary,
    secondaryContainer = BerrySecondaryContainer,
    onSecondaryContainer = BerryOnSecondaryContainer,
    tertiary = BerryTertiary,
    onTertiary = BerryOnTertiary,
    tertiaryContainer = BerryTertiaryContainer,
    onTertiaryContainer = BerryOnTertiaryContainer,
    background = BerryBackground,
    onBackground = BerryOnBackground,
    surface = BerrySurface,
    onSurface = BerryOnSurface,
    surfaceVariant = BerrySurfaceVariant,
    onSurfaceVariant = BerryOnSurfaceVariant,
    error = BerryError,
    onError = BerryOnError,
    errorContainer = BerryErrorContainer,
    onErrorContainer = BerryOnErrorContainer,
    outline = BerryOutline,
    outlineVariant = BerryOutlineVariant
)

private fun berryDarkColorScheme(): ColorScheme = darkColorScheme(
    primary = BerryPrimaryDark,
    onPrimary = BerryOnPrimaryDark,
    primaryContainer = BerryPrimaryContainerDark,
    onPrimaryContainer = BerryOnPrimaryContainerDark,
    secondary = BerrySecondaryDark,
    onSecondary = BerryOnSecondaryDark,
    secondaryContainer = BerrySecondaryContainerDark,
    onSecondaryContainer = BerryOnSecondaryContainerDark,
    tertiary = BerryTertiaryDark,
    onTertiary = BerryOnTertiaryDark,
    tertiaryContainer = BerryTertiaryContainerDark,
    onTertiaryContainer = BerryOnTertiaryContainerDark,
    background = BerryBackgroundDark,
    onBackground = BerryOnBackgroundDark,
    surface = BerrySurfaceDark,
    onSurface = BerryOnSurfaceDark,
    surfaceVariant = BerrySurfaceVariantDark,
    onSurfaceVariant = BerryOnSurfaceVariantDark,
    error = BerryErrorDark,
    onError = BerryOnErrorDark,
    errorContainer = BerryErrorContainerDark,
    onErrorContainer = BerryOnErrorContainerDark,
    outline = BerryOutlineDark,
    outlineVariant = BerryOutlineVariantDark
)

private fun lavenderLightColorScheme(): ColorScheme = lightColorScheme(
    primary = LavenderPrimary,
    onPrimary = LavenderOnPrimary,
    primaryContainer = LavenderPrimaryContainer,
    onPrimaryContainer = LavenderOnPrimaryContainer,
    secondary = LavenderSecondary,
    onSecondary = LavenderOnSecondary,
    secondaryContainer = LavenderSecondaryContainer,
    onSecondaryContainer = LavenderOnSecondaryContainer,
    tertiary = LavenderTertiary,
    onTertiary = LavenderOnTertiary,
    tertiaryContainer = LavenderTertiaryContainer,
    onTertiaryContainer = LavenderOnTertiaryContainer,
    background = LavenderBackground,
    onBackground = LavenderOnBackground,
    surface = LavenderSurface,
    onSurface = LavenderOnSurface,
    surfaceVariant = LavenderSurfaceVariant,
    onSurfaceVariant = LavenderOnSurfaceVariant,
    error = LavenderError,
    onError = LavenderOnError,
    errorContainer = LavenderErrorContainer,
    onErrorContainer = LavenderOnErrorContainer,
    outline = LavenderOutline,
    outlineVariant = LavenderOutlineVariant
)

private fun lavenderDarkColorScheme(): ColorScheme = darkColorScheme(
    primary = LavenderPrimaryDark,
    onPrimary = LavenderOnPrimaryDark,
    primaryContainer = LavenderPrimaryContainerDark,
    onPrimaryContainer = LavenderOnPrimaryContainerDark,
    secondary = LavenderSecondaryDark,
    onSecondary = LavenderOnSecondaryDark,
    secondaryContainer = LavenderSecondaryContainerDark,
    onSecondaryContainer = LavenderOnSecondaryContainerDark,
    tertiary = LavenderTertiaryDark,
    onTertiary = LavenderOnTertiaryDark,
    tertiaryContainer = LavenderTertiaryContainerDark,
    onTertiaryContainer = LavenderOnTertiaryContainerDark,
    background = LavenderBackgroundDark,
    onBackground = LavenderOnBackgroundDark,
    surface = LavenderSurfaceDark,
    onSurface = LavenderOnSurfaceDark,
    surfaceVariant = LavenderSurfaceVariantDark,
    onSurfaceVariant = LavenderOnSurfaceVariantDark,
    error = LavenderErrorDark,
    onError = LavenderOnErrorDark,
    errorContainer = LavenderErrorContainerDark,
    onErrorContainer = LavenderOnErrorContainerDark,
    outline = LavenderOutlineDark,
    outlineVariant = LavenderOutlineVariantDark
)
