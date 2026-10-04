package pl.chemia.game.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.chemia.game.R
import pl.chemia.game.model.Category
import pl.chemia.game.model.Intensity
import pl.chemia.game.ui.theme.Burgundy
import pl.chemia.game.ui.theme.Divider
import pl.chemia.game.ui.theme.Gold
import pl.chemia.game.ui.theme.Ivory
import pl.chemia.game.ui.theme.Obsidian
import pl.chemia.game.ui.theme.Rose
import pl.chemia.game.ui.theme.RoseSoft
import pl.chemia.game.ui.theme.Teal

@Composable
fun PremiumBackground(content: @Composable BoxScope.() -> Unit) {
    val motion = rememberInfiniteTransition(label = "ambient")
    val roseShift by motion.animateFloat(
        initialValue = -24f,
        targetValue = 42f,
        animationSpec = infiniteRepeatable(
            animation = tween(7_200),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "roseShift",
    )
    val goldShift by motion.animateFloat(
        initialValue = 32f,
        targetValue = -36f,
        animationSpec = infiniteRepeatable(
            animation = tween(9_000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "goldShift",
    )
    val glowAlpha by motion.animateFloat(
        initialValue = 0.78f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3_800),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowAlpha",
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF050407),
                        Color(0xFF140A14),
                        Color(0xFF0D0810),
                        Obsidian,
                    )
                )
            )
    ) {
        Box(
            Modifier
                .size(310.dp)
                .align(Alignment.TopEnd)
                .graphicsLayer {
                    translationX = roseShift
                    translationY = roseShift * 0.45f
                    alpha = glowAlpha
                    scaleX = 1.05f
                    scaleY = 1.05f
                }
                .blur(82.dp)
                .background(Rose.copy(alpha = 0.18f), CircleShape)
        )
        Box(
            Modifier
                .size(275.dp)
                .align(Alignment.BottomStart)
                .graphicsLayer {
                    translationX = goldShift
                    translationY = goldShift * -0.35f
                    alpha = glowAlpha * 0.82f
                }
                .blur(92.dp)
                .background(Gold.copy(alpha = 0.11f), CircleShape)
        )
        Box(
            Modifier
                .size(180.dp)
                .align(Alignment.Center)
                .graphicsLayer {
                    translationX = roseShift * -0.28f
                    translationY = goldShift * 0.24f
                    alpha = 0.42f
                }
                .blur(72.dp)
                .background(Burgundy.copy(alpha = 0.11f), CircleShape)
        )
        content()
    }
}

@Composable
fun BrandHeader(compact: Boolean = false, trailing: String? = null) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                "CHEMIA",
                color = Ivory,
                fontSize = if (compact) 23.sp else 41.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
            )
            Text(
                androidx.compose.ui.res.stringResource(R.string.brand_tagline),
                color = Gold,
                fontSize = if (compact) 8.sp else 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
            )
        }
        trailing?.let { PremiumChip(it, Rose) }
    }
}

@Composable
fun PremiumChip(text: String, color: Color) {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.45f), RoundedCornerShape(50))
            .padding(horizontal = 11.dp, vertical = 6.dp)
    ) {
        Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 0.7.sp)
    }
}

@Composable
fun GlassPanel(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xE818131B)),
        border = BorderStroke(1.dp, Divider),
    ) {
        Column(Modifier.padding(20.dp), content = content)
    }
}

@Composable
fun PrimaryButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(18.dp)
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().clip(shape),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) Burgundy else Divider,
            contentColor = Ivory,
            disabledContainerColor = Divider,
            disabledContentColor = Ivory.copy(alpha = 0.45f),
        ),
        shape = shape,
    ) {
        Text(text, fontWeight = FontWeight.Black, letterSpacing = 0.7.sp, modifier = Modifier.padding(vertical = 4.dp))
    }
}

@Composable
fun ScreenTitle(title: String, body: String? = null) {
    Text(title, style = MaterialTheme.typography.headlineMedium, color = Ivory)
    if (body != null) {
        Spacer(Modifier.height(8.dp))
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun categoryLabel(category: Category): String = androidx.compose.ui.res.stringResource(
    when (category) {
        Category.CONNECTION -> R.string.category_connection
        Category.FLIRT -> R.string.category_flirt
        Category.TOUCH -> R.string.category_touch
        Category.QUESTION -> R.string.category_question
        Category.KISS -> R.string.category_kiss
        Category.ROLEPLAY -> R.string.category_roleplay
        Category.AFTERGLOW -> R.string.category_afterglow
    }
)

@Composable
fun intensityLabel(intensity: Intensity): String = androidx.compose.ui.res.stringResource(
    when (intensity) {
        Intensity.SOFT -> R.string.intensity_soft
        Intensity.SPICY -> R.string.intensity_spicy
        Intensity.HOT -> R.string.intensity_hot
        Intensity.EXTREME -> R.string.intensity_extreme
    }
)

fun intensityColor(intensity: Intensity): Color = when (intensity) {
    Intensity.SOFT -> Teal
    Intensity.SPICY -> Gold
    Intensity.HOT -> RoseSoft
    Intensity.EXTREME -> Rose
}

fun formatDuration(seconds: Long): String {
    val minutes = seconds / 60
    val remainder = seconds % 60
    return "%02d:%02d".format(minutes, remainder)
}
