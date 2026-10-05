package pl.chemia.game.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.chemia.game.model.Category
import pl.chemia.game.model.GameCard
import pl.chemia.game.model.Intensity
import pl.chemia.game.model.SessionState
import kotlin.random.Random

class GameEngineProductionTest {
    private val deck = listOf(
        GameCard("a", Category.CONNECTION, Intensity.SOFT, "A", "A", 3),
        GameCard("b", Category.FLIRT, Intensity.SPICY, "B", "B", 5),
        GameCard("c", Category.ROLEPLAY, Intensity.HOT, "C", "C", 8, chain = true),
        GameCard("d", Category.TOUCH, Intensity.EXTREME, "D", "D", 10),
    )

    @Test
    fun neverReturnsAboveMaximumIntensity() {
        val engine = GameEngine(Random(42))
        repeat(100) {
            assertTrue(
                engine.next(
                    deck = deck,
                    state = SessionState(),
                    maxIntensity = Intensity.SPICY,
                    allowedCategories = Category.entries.toSet(),
                ).intensity.rank <= Intensity.SPICY.rank
            )
        }
    }

    @Test
    fun respectsAllowedCategories() {
        val engine = GameEngine(Random(7))
        repeat(40) {
            val card = engine.next(
                deck = deck,
                state = SessionState(),
                maxIntensity = Intensity.EXTREME,
                allowedCategories = setOf(Category.CONNECTION),
            )
            assertEquals(Category.CONNECTION, card.category)
        }
    }

    @Test
    fun avoidsRecentAndSkippedCardsWhenAlternativeExists() {
        val engine = GameEngine(Random(1))
        val state = SessionState(
            recentIds = listOf("a", "b"),
            skippedIds = setOf("c"),
        )
        repeat(20) {
            assertEquals(
                "d",
                engine.next(
                    deck = deck,
                    state = state,
                    maxIntensity = Intensity.EXTREME,
                    allowedCategories = Category.entries.toSet(),
                ).id
            )
        }
    }

    @Test
    fun skipKeepsHeatAndResetsChain() {
        val engine = GameEngine(Random(1))
        val initial = SessionState(heat = 42, chain = 4)

        val result = engine.skip(initial, deck[1])

        assertEquals(42, result.heat)
        assertEquals(0, result.chain)
        assertTrue("b" in result.skippedIds)\n        assertEquals(1, result.consecutiveSkips)\n        assertEquals(Category.FLIRT, result.recentCategories.last())
    }

    @Test
    fun completeAddsHeatAndCapsAtHundred() {
        val engine = GameEngine(Random(1))
        val result = engine.complete(SessionState(heat = 96, chain = 2), deck.last())

        assertEquals(100, result.heat)
        assertEquals(3, result.chain)\n        assertEquals(0, result.consecutiveSkips)\n        assertEquals(Category.TOUCH, result.recentCategories.last())
    }

    @Test
    fun selectionWeightRewardsHeatChainAndFavorites() {
        val engine = GameEngine(Random(1))
        val base = deck[1]
        val boosted = deck[2]

        assertEquals(2, engine.selectionWeight(base, SessionState(), emptySet()))
        assertTrue(
            engine.selectionWeight(
                boosted,
                SessionState(heat = 60, chain = 2),
                setOf(Category.ROLEPLAY),
            ) >= 7
        )
        assertFalse(engine.selectionWeight(boosted, SessionState(), emptySet()) < 1)
    }
}
