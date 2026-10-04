package pl.chemia.game.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.chemia.game.R
import pl.chemia.game.ui.common.BrandHeader
import pl.chemia.game.ui.common.GlassPanel
import pl.chemia.game.ui.common.PrimaryButton
import pl.chemia.game.ui.common.ScreenTitle

@Composable
fun HomeScreen(onNewGame: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        BrandHeader()
        Spacer(Modifier.height(30.dp))
        GlassPanel {
            ScreenTitle(
                title = androidx.compose.ui.res.stringResource(R.string.home_title),
                body = androidx.compose.ui.res.stringResource(R.string.home_body),
            )
            Spacer(Modifier.height(24.dp))
            PrimaryButton(
                text = androidx.compose.ui.res.stringResource(R.string.new_game),
                onClick = onNewGame,
            )
            Spacer(Modifier.height(14.dp))
            Text(
                "Bez konta • bez internetu • bez historii sesji",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
