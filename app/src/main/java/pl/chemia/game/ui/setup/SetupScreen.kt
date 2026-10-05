package pl.chemia.game.ui.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.chemia.game.R
import pl.chemia.game.model.SessionStyle
import pl.chemia.game.ui.common.BrandHeader
import pl.chemia.game.ui.common.GlassPanel
import pl.chemia.game.ui.common.PrimaryButton
import pl.chemia.game.ui.common.ScreenTitle

@Composable
fun SetupScreen(
    playerA: String,
    playerB: String,
    durationMinutes: Int,
    sessionStyle: SessionStyle,
    soundEnabled: Boolean,
    hapticsEnabled: Boolean,
    reducedMotion: Boolean,
    onPlayerA: (String) -> Unit,
    onPlayerB: (String) -> Unit,
    onDuration: (Int) -> Unit,
    onSessionStyle: (SessionStyle) -> Unit,
    onSound: (Boolean) -> Unit,
    onHaptics: (Boolean) -> Unit,
    onReducedMotion: (Boolean) -> Unit,
    onStart: () -> Unit,
) {
    Column(Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(20.dp)) {
        BrandHeader(compact = true)
        Spacer(Modifier.height(22.dp))
        ScreenTitle(androidx.compose.ui.res.stringResource(R.string.setup_title))
        Spacer(Modifier.height(16.dp))
        GlassPanel {
            OutlinedTextField(
                value = playerA,
                onValueChange = onPlayerA,
                label = { Text(androidx.compose.ui.res.stringResource(R.string.partner_a)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = playerB,
                onValueChange = onPlayerB,
                label = { Text(androidx.compose.ui.res.stringResource(R.string.partner_b)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(Modifier.height(20.dp))
            Text(androidx.compose.ui.res.stringResource(R.string.session_length), style = MaterialTheme.typography.titleMedium)
            listOf(15, 30, 45, 60).forEach { minutes ->
                FilterChip(
                    selected = durationMinutes == minutes,
                    onClick = { onDuration(minutes) },
                    label = { Text(androidx.compose.ui.res.stringResource(R.string.minutes, minutes)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                androidx.compose.ui.res.stringResource(R.string.session_style),
                style = MaterialTheme.typography.titleMedium,
            )
            SessionStyle.entries.forEach { style ->
                val label = when (style) {
                    SessionStyle.CONNECTION -> R.string.style_connection
                    SessionStyle.CHEMISTRY -> R.string.style_chemistry
                    SessionStyle.ADVENTURE -> R.string.style_adventure
                }
                FilterChip(
                    selected = sessionStyle == style,
                    onClick = { onSessionStyle(style) },
                    label = { Text(androidx.compose.ui.res.stringResource(label)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            SettingToggle(
                label = androidx.compose.ui.res.stringResource(R.string.sound),
                checked = soundEnabled,
                onCheckedChange = onSound,
            )
            SettingToggle(
                label = androidx.compose.ui.res.stringResource(R.string.haptics),
                checked = hapticsEnabled,
                onCheckedChange = onHaptics,
            )
            SettingToggle(
                label = androidx.compose.ui.res.stringResource(R.string.reduced_motion),
                checked = reducedMotion,
                onCheckedChange = onReducedMotion,
            )
            Spacer(Modifier.height(20.dp))
            PrimaryButton(
                text = androidx.compose.ui.res.stringResource(R.string.start),
                enabled = playerA.isNotBlank() && playerB.isNotBlank(),
                onClick = onStart,
            )
        }
    }
}

@Composable
private fun SettingToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
