package pl.chemia.game.ui.consent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.chemia.game.R
import pl.chemia.game.model.Category
import pl.chemia.game.model.ConsentProfile
import pl.chemia.game.model.Intensity
import pl.chemia.game.ui.common.BrandHeader
import pl.chemia.game.ui.common.GlassPanel
import pl.chemia.game.ui.common.PrimaryButton
import pl.chemia.game.ui.common.ScreenTitle
import pl.chemia.game.ui.common.categoryLabel
import pl.chemia.game.ui.common.intensityLabel
import pl.chemia.game.ui.theme.Rose

@Composable
fun ConsentScreen(
    playerLabel: String,
    profile: ConsentProfile,
    maxSelectableIntensity: Intensity,
    conflict: Boolean = false,
    onChange: (ConsentProfile) -> Unit,
    onComplete: () -> Unit,
) {
    val categories = Category.entries.filterNot { it == Category.AFTERGLOW }
    val intensities = Intensity.entries.filter { it.rank <= maxSelectableIntensity.rank }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        BrandHeader(compact = true, trailing = playerLabel)
        Spacer(Modifier.height(24.dp))
        ScreenTitle(
            title = androidx.compose.ui.res.stringResource(R.string.consent_title),
            body = androidx.compose.ui.res.stringResource(R.string.consent_private),
        )
        Spacer(Modifier.height(18.dp))
        GlassPanel {
            Text(androidx.compose.ui.res.stringResource(R.string.consent_categories), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            categories.forEach { category ->
                FilterChip(
                    selected = category in profile.allowedCategories,
                    onClick = { onChange(profile.toggle(category)) },
                    label = { Text(categoryLabel(category)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(18.dp))
            Text(androidx.compose.ui.res.stringResource(R.string.consent_intensity), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            intensities.forEach { intensity ->
                FilterChip(
                    selected = intensity == profile.maxIntensity,
                    onClick = { onChange(profile.copy(maxIntensity = intensity)) },
                    label = { Text(intensityLabel(intensity)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (conflict) {
                Spacer(Modifier.height(12.dp))
                Text(
                    androidx.compose.ui.res.stringResource(R.string.consent_conflict),
                    color = Rose,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(Modifier.height(20.dp))
            PrimaryButton(
                text = androidx.compose.ui.res.stringResource(R.string.continue_label),
                enabled = profile.allowedCategories.isNotEmpty(),
                onClick = onComplete,
            )
        }
    }
}
