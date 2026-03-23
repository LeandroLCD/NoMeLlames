package cl.blipblipcode.prefixsapp.ui.theme

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
import cl.blipblipcode.prefixsapp.domain.model.ThemeApp

private val DarkColorScheme = darkColorScheme(
    // Primary: Cyan — acción principal, botones, highlights
    primary = CyanAccent,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF003741),
    onPrimaryContainer = CyanAccent,
    inversePrimary = Color(0xFF006878),

    // Secondary: Purple — acentos secundarios
    secondary = PurpleAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF3D0052),
    onSecondaryContainer = PurpleAccent,

    // Tertiary: Red — peligro / llamadas bloqueadas
    tertiary = BlockedRed,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF4D0012),
    onTertiaryContainer = BlockedRed,

    // Error: alineado con BlockedRed
    error = BlockedRed,
    onError = Color.White,
    errorContainer = Color(0xFF4D0012),
    onErrorContainer = Color(0xFFFFB3BE),

    // Background
    background = DarkBg,
    onBackground = Color.White,

    // Surface y variantes (niveles de elevación)
    surface = CardBg,
    onSurface = Color.White,
    surfaceVariant = DarkGray,
    onSurfaceVariant = TextGray,
    surfaceTint = CyanAccent,
    surfaceBright = DarkGray,
    surfaceDim = DarkBg,
    surfaceContainer = CardBg,
    surfaceContainerLowest = DarkBg,
    surfaceContainerLow = Color(0xFF0F0F0F),
    surfaceContainerHigh = DividerColor,
    surfaceContainerHighest = DarkGray,

    // Inverse (snackbars, tooltips)
    inverseSurface = Color(0xFFE4E2E6),
    inverseOnSurface = DarkBg,

    // Outline / divisores
    outline = DarkGray,
    outlineVariant = DividerColor,

    // Scrim (modales)
    scrim = Color.Black,
)

private val LightColorScheme = lightColorScheme(
    // Primary: Cyan profundo — legible sobre blanco
    primary = Color(0xFF00D1E0),
    onPrimary = Color.White,
    primaryContainer = CyanContainer,
    onPrimaryContainer = OnCyanContainer,
    inversePrimary = CyanAccent,

    // Secondary: Purple profundo
    secondary = PurpleDeep,
    onSecondary = Color.White,
    secondaryContainer = PurpleContainer,
    onSecondaryContainer = OnPurpleContainer,

    // Tertiary: Red profundo — peligro / llamadas bloqueadas
    tertiary = RedDeep,
    onTertiary = Color.White,
    tertiaryContainer = RedContainer,
    onTertiaryContainer = OnRedContainer,

    // Error
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFD1D1D6),
    onErrorContainer = Color(0xFF410002),

    // Background: blanco frío con tinte cyan
    background = LightBg,
    onBackground = LightOnBg,

    // Surface y variantes
    surface = Color(0xFFEAE9E9),
    onSurface = LightOnBg,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = DarkGray,
    surfaceTint = CyanDeep,
    surfaceBright = Color.White,
    surfaceDim = Color(0xFFD6DADB),
    surfaceContainer = Color(0xFFEAF1F3),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF0F5F6),
    surfaceContainerHigh = Color(0xFFE4ECEE),
    surfaceContainerHighest = Color(0xFFDEE7E9),

    // Inverse
    inverseSurface = Color(0xFF2D3133),
    inverseOnSurface = Color(0xFFEDF1F2),

    // Outline / divisores
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,

    // Scrim
    scrim = Color.Black,
)

private val GreenColorScheme = darkColorScheme(
    onPrimary = Color(0xFFEBFFE2),
    primary = Color(0xFF0B7117),
    primaryContainer = Color(0xFF00FF41),
    onPrimaryContainer = Color(0xFF007117),
    inversePrimary = Color(0xFF006E16),
    secondary = Color(0xFF66DD8B),
    onSecondary = Color(0xFF003919),
    secondaryContainer = Color(0xFF25A55A),
    onSecondaryContainer = Color(0xFF003115),
    tertiary = Color(0xFFE9FFE9),
    onTertiary = Color(0xFF00391A),
    tertiaryContainer = Color(0xFFADEBBA),
    onTertiaryContainer = Color(0xFF326C45),
    error = BlockedRed,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF131316),
    onBackground = Color(0xFFE5E1E5),
    surface = Color(0xFF131316),
    onSurface = Color(0xFFE5E1E5),
    surfaceVariant = Color(0xFF353438),
    onSurfaceVariant = Color(0xFFB9CCB2),
    outline = Color(0xFF84967E),
    outlineVariant = Color(0xFF3B4B37),
    inverseSurface = Color(0xFFE5E1E5),
    inverseOnSurface = Color(0xFF313033),
    surfaceTint = Color(0xFF00E639),
    primaryFixed = Color(0xFF72FF70),
    onPrimaryFixed = Color(0xFF002203),
    primaryFixedDim = Color(0xFF00E639),
    onPrimaryFixedVariant = Color(0xFF00530E),
    secondaryFixed = Color(0xFF83FBA5),
    onSecondaryFixed = Color(0xFF00210C),
    secondaryFixedDim = Color(0xFF66DD8B),
    onSecondaryFixedVariant = Color(0xFF005227),
    tertiaryFixed = Color(0xFFB2F1BF),
    onTertiaryFixed = Color(0xFF00210D),
    tertiaryFixedDim = Color(0xFF97D5A5),
    onTertiaryFixedVariant = Color(0xFF14512D),
    surfaceDim = Color(0xFF131316),
    surfaceBright = Color(0xFF39393C),
    surfaceContainerLowest = Color(0xFF0E0E11),
    surfaceContainerLow = Color(0xFF1C1B1E),
    surfaceContainer = Color(0xFF201F22),
    surfaceContainerHigh = Color(0xFF2A2A2D),
    surfaceContainerHighest = Color(0xFF353438),
)

private val PinkColorScheme = darkColorScheme(
    primary = Color(0xFFFF007F),
    onPrimary = Color(0xFFF4F4F6),
    primaryContainer = Color(0xFF4D0026),
    onPrimaryContainer = Color(0xFFFF007F),
    inversePrimary = Color(0xFFFF007F),

    secondary = Color(0xFFB026FF),
    onSecondary = Color(0xFFF4F4F6),
    secondaryContainer = Color(0xFF35004D),
    onSecondaryContainer = Color(0xFFB026FF),

    tertiary = Color(0xFFFF003C),
    onTertiary = Color(0xFFF4F4F6),
    tertiaryContainer = Color(0xFF4D0012),
    onTertiaryContainer = Color(0xFFFF003C),

    error = Color(0xFFFF003C),
    onError = Color(0xFFF4F4F6),
    errorContainer = Color(0xFF4D0012),
    onErrorContainer = Color(0xFFFFB3BE),

    background = Color(0xFF050507),
    onBackground = Color(0xFFF4F4F6),

    surface = Color(0xFF111116),
    onSurface = Color(0xFFF4F4F6),
    surfaceVariant = Color(0xFF33333D),
    onSurfaceVariant = Color(0xFFF4F4F6),
    surfaceTint = Color(0xFFFF007F),
    surfaceBright = Color(0xFF33333D),
    surfaceDim = Color(0xFF050507),
    surfaceContainer = Color(0xFF111116),
    surfaceContainerLowest = Color(0xFF050507),
    surfaceContainerLow = Color(0xFF0F0F0F),
    surfaceContainerHigh = Color(0xFF33333D),
    surfaceContainerHighest = Color(0xFF33333D),

    inverseSurface = Color(0xFFF4F4F6),
    inverseOnSurface = Color(0xFF050507),

    outline = Color(0xFF33333D),
    outlineVariant = Color(0xFF33333D),

    scrim = Color.Black,
)

private val SolarisColorScheme = darkColorScheme(
    primary = Color(0xFFFFB000),
    onPrimary = Color(0xFF432C00),
    primaryContainer = Color(0xFFE09D0A),
    onPrimaryContainer = Color(0xFF6A4700),
    inversePrimary = Color(0xFF805600),
    secondary = Color(0xFFFFB5A0),
    onSecondary = Color(0xFF5F1500),
    secondaryContainer = Color(0xFFD73B00),
    onSecondaryContainer = Color(0xFFFFFBFF),
    tertiary = Color(0xFFFFD862),
    onTertiary = Color(0xFF3C2F00),
    tertiaryContainer = Color(0xFFE2BC43),
    onTertiaryContainer = Color(0xFF604C00),
    error = BlockedRed,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF131316),
    onBackground = Color(0xFFE5E1E5),
    surface = Color(0xFF131316),
    onSurface = Color(0xFFE5E1E5),
    surfaceVariant = Color(0xFF353438),
    onSurfaceVariant = Color(0xFFD7C4AC),
    outline = Color(0xFF9F8E78),
    outlineVariant = Color(0xFF524533),
    inverseSurface = Color(0xFFE5E1E5),
    inverseOnSurface = Color(0xFF313033),
    surfaceTint = Color(0xFFFFBA43),
    primaryFixed = Color(0xFFFFDDAF),
    onPrimaryFixed = Color(0xFF281800),
    primaryFixedDim = Color(0xFFFFBA43),
    onPrimaryFixedVariant = Color(0xFF614000),
    secondaryFixed = Color(0xFFFFDBD1),
    onSecondaryFixed = Color(0xFF3B0900),
    secondaryFixedDim = Color(0xFFFFB5A0),
    onSecondaryFixedVariant = Color(0xFF862200),
    tertiaryFixed = Color(0xFFFFE088),
    onTertiaryFixed = Color(0xFF241A00),
    tertiaryFixedDim = Color(0xFFE9C349),
    onTertiaryFixedVariant = Color(0xFF574500),
    surfaceDim = Color(0xFF131316),
    surfaceBright = Color(0xFF39393C),
    surfaceContainerLowest = Color(0xFF0E0E11),
    surfaceContainerLow = Color(0xFF1C1B1E),
    surfaceContainer = Color(0xFF201F22),
    surfaceContainerHigh = Color(0xFF2A2A2D),
    surfaceContainerHighest = Color(0xFF353438),
)

@Composable
fun PrefixsAppTheme(
    themeApp: ThemeApp = ThemeApp.System,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when(themeApp) {
        ThemeApp.System -> {
            when {
                dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    val context = LocalContext.current
                    if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
                }

                darkTheme -> DarkColorScheme
                else -> LightColorScheme
            }
        }
        ThemeApp.Dark -> DarkColorScheme
        ThemeApp.Light -> LightColorScheme
        ThemeApp.Pink -> PinkColorScheme
        ThemeApp.Green -> GreenColorScheme
        ThemeApp.Solaris -> SolarisColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
