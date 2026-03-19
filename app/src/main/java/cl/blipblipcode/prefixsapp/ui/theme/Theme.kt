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
    primary = CyanDeep,
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
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    // Background: blanco frío con tinte cyan
    background = LightBg,
    onBackground = LightOnBg,

    // Surface y variantes
    surface = LightBg,
    onSurface = LightOnBg,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
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

@Composable
fun PrefixsAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Deshabilitado para mantener el tema cyberpunk personalizado
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
