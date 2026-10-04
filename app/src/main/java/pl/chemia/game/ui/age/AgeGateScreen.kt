package pl.chemia.game.ui.age

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import pl.chemia.game.R
import pl.chemia.game.ui.common.BrandHeader
import pl.chemia.game.ui.common.GlassPanel
import pl.chemia.game.ui.common.PremiumChip
import pl.chemia.game.ui.common.PrimaryButton
import pl.chemia.game.ui.theme.Gold
import pl.chemia.game.ui.theme.Ivory
import pl.chemia.game.ui.theme.Rose
import pl.chemia.game.ui.theme.RoseSoft

@Composable
fun AgeGateScreen(onAccepted: () -> Unit) {
    var adultA by rememberSaveable { mutableStateOf(false) }
    var adultB by rememberSaveable { mutableStateOf(false) }
    var consent by rememberSaveable { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        BrandHeader()
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PremiumChip("18+", Rose)
            PremiumChip("OFFLINE", Gold)
            PremiumChip("PRIVATE", RoseSoft)
        }
        Spacer(Modifier.height(26.dp))
        GlassPanel {
            Text(androidx.compose.ui.res.stringResource(R.string.age_title), color = Ivory, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text(androidx.compose.ui.res.stringResource(R.string.age_body), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(18.dp))
            ConsentCheck(adultA, { adultA = it }, androidx.compose.ui.res.stringResource(R.string.age_partner_a), "age_partner_a")
            ConsentCheck(adultB, { adultB = it }, androidx.compose.ui.res.stringResource(R.string.age_partner_b), "age_partner_b")
            ConsentCheck(consent, { consent = it }, androidx.compose.ui.res.stringResource(R.string.age_consent), "age_voluntary")
            Spacer(Modifier.height(18.dp))
            PrimaryButton(
                text = androidx.compose.ui.res.stringResource(R.string.continue_label),
                enabled = adultA && adultB && consent,
                modifier = Modifier.testTag("age_continue"),
                onClick = onAccepted,
            )
        }
    }
}

@Composable
private fun ConsentCheck(
    checked: Boolean,
    onChange: (Boolean) -> Unit,
    label: String,
    tag: String,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .testTag(tag)
            .clickable { onChange(!checked) }
            .semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = onChange)
        Text(label, modifier = Modifier.padding(start = 8.dp), color = MaterialTheme.colorScheme.onSurface)
    }
}
