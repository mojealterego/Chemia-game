package pl.chemia.game.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import pl.chemia.game.model.Category
import pl.chemia.game.model.GameCard
import pl.chemia.game.model.Intensity
import pl.chemia.game.model.SessionState
import kotlin.random.Random

class GameEngineNoRepeatTest {
    private val deck = listOf(
        GameCard("a", Category.FLIRT, Intensity.SOFT, "A", "A", 1),
        GameCard("b", Category.FLIRT, Intensity.SOFT, "B", "B", 1),
        GameCard("c", Category.FLIRT, Intensity.SOFT, "C", "C", 1),
    )

    @Test
    fun seenCardIsNotRepeatedWhileUnseenAlternativesExist() {
        val card = GameEngine(Random(4)).next(
            deck = deck,
            state = SessionState(seenIds = setOf("a")),
            maxIntensity = Intensity.SOFT,
            allowedCategories = setOf(Category.FLIRT),
        )

        assertNotEquals("a", card.id)
    }

    @Test
    fun exhaustedDeckCanRecycleCards() {
        val card = GameEngine(Random(4)).next(
            deck = deck,
            state = SessionState(seenIds = setOf("a", "b", "c")),
            maxIntensity = Intensity.SOFT,
            allowedCategories = setOf(Category.FLIRT),
        )

        assertEquals(true, card.id in setOf("a", "b", "c"))
    }
}
