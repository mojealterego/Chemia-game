package pl.chemia.game

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GameEngineTest {

    private val deck = listOf(
        GameCard("a", "FLIRT", 1, 3, "A", "A"),
        GameCard("b", "BLISKOSC", 2, 5, "B", "B"),
        GameCard("c", "ROLEPLAY", 3, 8, "C", "C"),
        GameCard("d", "ODWAZNE", 4, 10, "D", "D"),
    )

    @Test
    fun nextCardNeverExceedsSelectedIntensity() {
        val engine = GameEngine(Random(42))

        repeat(100) {
            val card = engine.nextCard(
                deck = deck,
                maxIntensity = 2,
                recentIds = emptyList(),
            )
            assertTrue(card.intensity <= 2)
        }
    }

    @Test
    fun nextCardAvoidsRecentCardsWhenAlternativeExists() {
        val engine = GameEngine(Random(7))

        repeat(30) {
            val card = engine.nextCard(
                deck = deck,
                maxIntensity = 4,
                recentIds = listOf("a", "b", "c"),
            )
            assertFalse(card.id in setOf("a", "b", "c"))
        }
    }

    @Test
    fun skippedCardDoesNotIncreaseHeat() {
        val engine = GameEngine(Random(1))
        val initial = SessionState(heat = 37)

        val afterSkip = engine.skip(initial, "a")

        assertTrue(afterSkip.heat == 37)
    }

    @Test
    fun completedCardIncreasesHeatButCapsAtOneHundred() {
        val engine = GameEngine(Random(1))
        val initial = SessionState(heat = 96)
        val card = GameCard("z", "ODWAZNE", 4, 12, "Z", "Z")

        val completed = engine.complete(initial, card)

        assertTrue(completed.heat == 100)
    }
}
