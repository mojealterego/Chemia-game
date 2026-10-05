package pl.chemia.game.ui.pass

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.chemia.game.R
import pl.chemia.game.ui.common.BrandHeader
import pl.chemia.game.ui.common.GlassPanel
import pl.chemia.game.ui.common.PrimaryButton
import pl.chemia.game.ui.common.ScreenTitle

@Composable
fun PassDeviceScreen(onReady: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        BrandHeader()
        Spacer(Modifier.height(28.dp))
        GlassPanel {
            ScreenTitle(
                title = androidx.compose.ui.res.stringResource(R.string.pass_title),
                body = androidx.compose.ui.res.stringResource(R.string.pass_body),
            )
            Spacer(Modifier.height(24.dp))
            PrimaryButton(
                text = androidx.compose.ui.res.stringResource(R.string.ready),
                onClick = onReady,
            )
        }
    }
}
