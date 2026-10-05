package pl.chemia.game.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Rose,
    onPrimary = Ivory,
    secondary = Gold,
    onSecondary = Obsidian,
    background = Obsidian,
    onBackground = Ivory,
    surface = ObsidianElevated,
    onSurface = Ivory,
    surfaceVariant = Divider,
    onSurfaceVariant = Muted,
    outline = Divider,
    error = RoseSoft,
)

private val LightColors = lightColorScheme(
    primary = Burgundy,
    onPrimary = Ivory,
    secondary = GoldDim,
    onSecondary = Obsidian,
    background = Ivory,
    onBackground = Obsidian,
    surface = Color(0xFFFFFBF7),
    onSurface = Obsidian,
    surfaceVariant = Color(0xFFF1E7EB),
    onSurfaceVariant = Color(0xFF685C64),
    outline = GoldDim,
)

@Composable
fun ChemiaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = ChemiaTypography,
        content = content,
    )
}
