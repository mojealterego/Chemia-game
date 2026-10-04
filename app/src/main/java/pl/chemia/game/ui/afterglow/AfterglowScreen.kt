package pl.chemia.game.ui.afterglow

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.chemia.game.R
import pl.chemia.game.model.GameCard
import pl.chemia.game.ui.common.BrandHeader
import pl.chemia.game.ui.common.PrimaryButton
import pl.chemia.game.ui.common.ScreenTitle
import pl.chemia.game.ui.theme.Gold

@Composable
fun AfterglowScreen(
    card: GameCard?,
    onAnother: () -> Unit,
    onFinish: () -> Unit,
) {
    BackHandler(onBack = onFinish)

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        BrandHeader(compact = true, trailing = "AFTERGLOW")
        Spacer(Modifier.height(24.dp))
        ScreenTitle(
            title = androidx.compose.ui.res.stringResource(R.string.afterglow_title),
            body = androidx.compose.ui.res.stringResource(R.string.afterglow_body),
        )
        Spacer(Modifier.height(18.dp))
        card?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
                ),
                border = BorderStroke(1.dp, Gold.copy(alpha = 0.5f)),
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        it.title,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        it.text,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        PrimaryButton(
            text = androidx.compose.ui.res.stringResource(R.string.one_more),
            onClick = onAnother,
        )
        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = onFinish, modifier = Modifier.fillMaxWidth()) {
            Text(androidx.compose.ui.res.stringResource(R.string.finish))
        }
        Spacer(Modifier.height(28.dp))
    }
}
