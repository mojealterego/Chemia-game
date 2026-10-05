package pl.chemia.game.ui.session

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import pl.chemia.game.R
import pl.chemia.game.SoundCue
import pl.chemia.game.SoundEngine
import pl.chemia.game.engine.SessionPhase
import pl.chemia.game.model.GameCard
import pl.chemia.game.ui.common.BrandHeader
import pl.chemia.game.ui.common.GlassPanel
import pl.chemia.game.ui.common.PremiumChip
import pl.chemia.game.ui.common.PrimaryButton
import pl.chemia.game.ui.common.categoryLabel
import pl.chemia.game.ui.common.formatDuration
import pl.chemia.game.ui.common.intensityColor
import pl.chemia.game.ui.common.intensityLabel
import pl.chemia.game.ui.theme.Divider
import pl.chemia.game.ui.theme.Gold
import pl.chemia.game.ui.theme.Ivory
import pl.chemia.game.ui.theme.Rose

@Composable
fun SessionScreen(
    activePlayer: String,
    card: GameCard?,
    heat: Int,
    chain: Int,
    phase: SessionPhase,
    directorDeescalated: Boolean,
    soundEnabled: Boolean,
    hapticsEnabled: Boolean,
    remainingSecondsProvider: () -> Long,
    onSkip: () -> Unit,
    onDone: () -> Boolean,
    onReviewConsent: () -> Unit,
    onEnd: () -> Unit,
    onAfterglow: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    var discreet by remember { mutableStateOf(false) }
    var remaining by remember { mutableLongStateOf(remainingSecondsProvider()) }
    val phaseLabel = androidx.compose.ui.res.stringResource(
        when (phase) {
            SessionPhase.WARMUP -> R.string.phase_warmup
            SessionPhase.BUILD -> R.string.phase_build
            SessionPhase.PEAK -> R.string.phase_peak
            SessionPhase.COOLDOWN -> R.string.phase_cooldown
        }
    )
    val animatedHeat by animateFloatAsState(
        targetValue = heat / 100f,
        animationSpec = tween(650),
        label = "heat",
    )

    BackHandler(onBack = onEnd)

    LaunchedEffect(card?.id, soundEnabled) {
        if (soundEnabled && card != null) {
            SoundEngine.play(SoundCue.DRAW)
        }
    }

    LaunchedEffect(Unit) {
        while (remaining > 0L) {
            delay(1000)
            remaining = remainingSecondsProvider()
        }
        onAfterglow()
    }

    if (discreet) {
        DiscreetScreen(onReturn = { discreet = false })
        return
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        BrandHeader(compact = true, trailing = formatDuration(remaining))
        Spacer(Modifier.height(10.dp))
        PremiumChip(phaseLabel, Gold)
        if (directorDeescalated) {
            Spacer(Modifier.height(6.dp))
            Text(
                androidx.compose.ui.res.stringResource(R.string.director_softening),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
            )
        }
        Spacer(Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth()) {
            Text(
                if (heat < 100) "HEAT " + heat else "HEAT MAX",
                color = Rose,
                fontWeight = FontWeight.Black,
                modifier = Modifier.weight(1f),
            )
            Text("CHAIN ×" + chain, color = Gold, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { animatedHeat },
            modifier = Modifier.fillMaxWidth().height(9.dp),
            color = Rose,
            trackColor = Divider,
        )
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = {
                if (soundEnabled) SoundEngine.play(SoundCue.DISCREET)
                discreet = true
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(androidx.compose.ui.res.stringResource(R.string.discreet))
        }
        Spacer(Modifier.height(14.dp))

        Crossfade(targetState = card, animationSpec = tween(280), label = "card") { shown ->
            if (shown != null) {
                GameCardPanel(shown, activePlayer)
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = {
                    if (soundEnabled) SoundEngine.play(SoundCue.SKIP)
                    if (hapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSkip()
                },
                modifier = Modifier.weight(1f),
            ) {
                Text(androidx.compose.ui.res.stringResource(R.string.skip))
            }
            Spacer(Modifier.width(10.dp))
            Button(
                onClick = {
                    if (soundEnabled) SoundEngine.play(SoundCue.COMPLETE)
                    if (hapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (onDone()) {
                        if (soundEnabled) SoundEngine.play(SoundCue.HEAT_MAX)
                        onAfterglow()
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Rose),
            ) {
                Text(androidx.compose.ui.res.stringResource(R.string.done), fontWeight = FontWeight.Black)
            }
        }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = onReviewConsent, modifier = Modifier.fillMaxWidth()) {
            Text(androidx.compose.ui.res.stringResource(R.string.review_consent))
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onEnd, modifier = Modifier.fillMaxWidth()) {
            Text(androidx.compose.ui.res.stringResource(R.string.stop_session))
        }
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun GameCardPanel(card: GameCard, activePlayer: String) {
    val accent = intensityColor(card.intensity)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.62f)),
    ) {
        Column(
            Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(accent.copy(alpha = 0.08f), MaterialTheme.colorScheme.surface)
                    )
                )
                .padding(24.dp)
        ) {
            PremiumChip(activePlayer, Rose)
            Spacer(Modifier.height(14.dp))
            Row {
                PremiumChip(categoryLabel(card.category), accent)
                Spacer(Modifier.width(8.dp))
                PremiumChip(intensityLabel(card.intensity), Gold)
            }
            Spacer(Modifier.height(20.dp))
            Text(
                card.title,
                color = Ivory,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(14.dp))
            Text(
                card.text,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(18.dp))
            if (card.requiresMutualYes) {
                Text(
                    androidx.compose.ui.res.stringResource(R.string.mutual_yes),
                    color = Gold,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(10.dp))
            }
            Text(
                "+" + card.heat + " HEAT",
                color = accent,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun DiscreetScreen(onReturn: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        BrandHeader(compact = true)
        Spacer(Modifier.height(28.dp))
        GlassPanel {
            Text(
                androidx.compose.ui.res.stringResource(R.string.discreet_title),
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                androidx.compose.ui.res.stringResource(R.string.discreet_body),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(24.dp))
            PrimaryButton(
                text = androidx.compose.ui.res.stringResource(R.string.return_game),
                onClick = onReturn,
            )
        }
    }
}
