package pl.chemia.game.engine

import org.junit.Assert.assertTrue
import org.junit.Test
import pl.chemia.game.model.Category
import pl.chemia.game.model.EffectiveConsent
import pl.chemia.game.model.GameCard
import pl.chemia.game.model.Intensity
import pl.chemia.game.model.SessionState
import kotlin.random.Random

class GameEngineFuzzTest {
    private val categories = Category.entries.filterNot { it == Category.AFTERGLOW }

    private val deck = buildList {
        var id = 0
        for (category in categories) {
            for (intensity in Intensity.entries) {
                add(
                    GameCard(
                        id = "fuzz-${id++}",
                        category = category,
                        intensity = intensity,
                        title = "T",
                        text = "X",
                        heat = 1,
                        requiresMutualYes = intensity.rank >= Intensity.HOT.rank,
                    )
                )
            }
        }
    }

    @Test
    fun engineNeverEscapesConsentAcrossRandomizedStates() {
        val random = Random(0xC0FFEE)
        repeat(500) {
            val max = Intensity.entries[random.nextInt(Intensity.entries.size)]
            val allowed = categories.shuffled(random)
                .take(random.nextInt(1, categories.size + 1))
                .toSet()
            val state = SessionState(
                heat = random.nextInt(0, 101),
                chain = random.nextInt(0, 5),
                completedCount = random.nextInt(0, 20),
                skippedCount = random.nextInt(0, 10),
                consecutiveSkips = random.nextInt(0, 4),
            )
            val consent = EffectiveConsent(allowed, max)
            val plan = SessionDirector().plan(
                session = state,
                progress = random.nextFloat(),
                consent = consent,
            )
            val card = GameEngine(Random(it + 1)).next(
                deck = deck,
                state = state,
                maxIntensity = max,
                allowedCategories = allowed,
                directorPlan = plan,
            )

            assertTrue(card.category in allowed)
            assertTrue(card.intensity.rank <= max.rank)
            assertTrue(card.intensity.rank <= plan.targetIntensity.rank)
        }
    }

    @Test
    fun directorNeverExceedsRandomizedConsentCeiling() {
        val random = Random(0xBADC0DE)
        repeat(1000) {
            val max = Intensity.entries[random.nextInt(Intensity.entries.size)]
            val consent = EffectiveConsent(categories.toSet(), max)
            val state = SessionState(
                heat = random.nextInt(0, 101),
                completedCount = random.nextInt(0, 30),
                skippedCount = random.nextInt(0, 20),
                consecutiveSkips = random.nextInt(0, 6),
            )
            val plan = SessionDirector().plan(state, random.nextFloat(), consent)
            assertTrue(plan.targetIntensity.rank <= max.rank)
            assertTrue(plan.preferredCategories.all { category -> category in consent.allowedCategories })
        }
    }
}
