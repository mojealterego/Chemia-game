package pl.chemia.game

import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import pl.chemia.game.engine.SessionPhase
import pl.chemia.game.model.Category
import pl.chemia.game.model.GameCard
import pl.chemia.game.model.Intensity
import pl.chemia.game.ui.session.SessionScreen
import pl.chemia.game.ui.theme.ChemiaTheme

class MutualRevealScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun sensitiveCardContentAppearsOnlyAfterBothConfirmations() {
        composeRule.setContent {
            ChemiaTheme {
                SessionScreen(
                    card = GameCard(
                        id = "locked",
                        category = Category.TOUCH,
                        intensity = Intensity.HOT,
                        title = "Hidden title",
                        text = "Hidden content",
                        heat = 8,
                        requiresMutualYes = true,
                    ),
                    activePlayer = "Partner 1",
                    heat = 50,
                    chain = 2,
                    phase = SessionPhase.PEAK,
                    directorDeescalated = false,
                    soundEnabled = false,
                    hapticsEnabled = false,
                    remainingSecondsProvider = { 120L },
                    onSkip = {},
                    onDone = { false },
                    onReviewConsent = {},
                    onEnd = {},
                    onAfterglow = {},
                )
            }
        }

        composeRule.onNodeWithTag("mutual_locked").assertIsDisplayed()
        composeRule.onNodeWithTag("card_content").assertDoesNotExist()

        composeRule.onNodeWithTag("mutual_confirm_a").performClick()
        composeRule.onNodeWithTag("card_content").assertDoesNotExist()

        composeRule.onNodeWithTag("mutual_confirm_b").performClick()
        composeRule.onNodeWithTag("card_content").assertIsDisplayed()
        composeRule.onNodeWithTag("mutual_locked").assertDoesNotExist()
    }
}
