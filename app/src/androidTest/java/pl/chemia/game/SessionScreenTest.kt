package pl.chemia.game

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import pl.chemia.game.engine.SessionPhase
import pl.chemia.game.model.Category
import pl.chemia.game.model.GameCard
import pl.chemia.game.model.Intensity
import pl.chemia.game.ui.session.SessionScreen
import pl.chemia.game.ui.theme.ChemiaTheme

class SessionScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun consentReviewIsAlwaysAvailableDuringGameplay() {
        var reviewed = false

        composeRule.setContent {
            ChemiaTheme {
                SessionScreen(
                    card = GameCard(
                        id = "test",
                        category = Category.FLIRT,
                        intensity = Intensity.SOFT,
                        title = "Test",
                        text = "Test card",
                        heat = 1,
                    ),
                    activePlayer = "A",
                    heat = 25,
                    chain = 1,
                    phase = SessionPhase.WARMUP,
                    directorDeescalated = false,
                    soundEnabled = false,
                    hapticsEnabled = false,
                    reducedMotion = true,
                    remainingSecondsProvider = { 60L },
                    onSkip = {},
                    onDone = { false },
                    onReviewConsent = { reviewed = true },
                    onEnd = {},
                    onAfterglow = {},
                )
            }
        }

        composeRule.onNodeWithTag("session_review_consent")
            .assertIsDisplayed()
            .performClick()

        composeRule.runOnIdle {
            assertTrue(reviewed)
        }
    }
}
