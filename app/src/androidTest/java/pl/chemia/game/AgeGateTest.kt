package pl.chemia.game

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import pl.chemia.game.ui.age.AgeGateScreen
import pl.chemia.game.ui.theme.ChemiaTheme

class AgeGateTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun continueRequiresBothAdultsAndVoluntaryConsent() {
        compose.setContent {
            ChemiaTheme(darkTheme = true) {
                AgeGateScreen(onAccepted = {})
            }
        }

        compose.onNodeWithTag("age_continue").assertIsNotEnabled()
        compose.onNodeWithTag("age_partner_a").performClick()
        compose.onNodeWithTag("age_continue").assertIsNotEnabled()
        compose.onNodeWithTag("age_partner_b").performClick()
        compose.onNodeWithTag("age_continue").assertIsNotEnabled()
        compose.onNodeWithTag("age_voluntary").performClick()
        compose.onNodeWithTag("age_continue").assertIsEnabled()
    }
}
