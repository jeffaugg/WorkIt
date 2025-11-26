package pies3.workit.ui.theme

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


private val LightColorScheme = lightColorScheme(
    primary = WorkItBlack,
    onPrimary = WorkItWhite,

    background = WorkItBackground,
    onBackground = WorkItTextPrimary,

    surface = WorkItSurfaceCard,
    onSurface = WorkItTextPrimary,

    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = WorkItTextSecondary,

    error = StatusRed,
    onError = WorkItWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = WorkItWhite,
    onPrimary = WorkItBlack,
    background = WorkItBlack,
    surface = WorkItDarkSurface,
    onSurface = WorkItWhite
)

@Composable
fun WorkItTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}