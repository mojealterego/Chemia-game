package pl.chemia.game.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.chemia.game.model.Category
import pl.chemia.game.model.EffectiveConsent
import pl.chemia.game.model.Intensity
import pl.chemia.game.model.SessionState

class SessionDirectorTest {
    private val allConsent = EffectiveConsent(
        allowedCategories = Category.entries.filterNot { it == Category.AFTERGLOW }.toSet(),
        maxIntensity = Intensity.EXTREME,
    )

    @Test
    fun warmupNeverStartsAtExtreme() {
        val plan = SessionDirector().plan(
            session = SessionState(),
            progress = 0.05f,
            consent = allConsent,
        )

        assertEquals(SessionPhase.WARMUP, plan.phase)
        assertTrue(plan.targetIntensity.rank <= Intensity.SPICY.rank)
    }

    @Test
    fun peakCanReachMaximumConsentedIntensity() {
        val plan = SessionDirector().plan(
            session = SessionState(heat = 70, completedCount = 8),
            progress = 0.76f,
            consent = allConsent,
        )

        assertEquals(SessionPhase.PEAK, plan.phase)
        assertEquals(Intensity.EXTREME, plan.targetIntensity)
    }

    @Test
    fun repeatedSkipsForceADeescalation() {
        val plan = SessionDirector().plan(
            session = SessionState(
                heat = 65,
                completedCount = 2,
                skippedCount = 4,
            ),
            progress = 0.72f,
            consent = allConsent,
        )

        assertTrue(plan.targetIntensity.rank <= Intensity.SPICY.rank)
        assertTrue(plan.deescalated)
    }

    @Test
    fun directorNeverExceedsConsentCeiling() {
        val consent = allConsent.copy(maxIntensity = Intensity.SPICY)
        val plan = SessionDirector().plan(
            session = SessionState(heat = 95, completedCount = 20),
            progress = 0.8f,
            consent = consent,
        )

        assertTrue(plan.targetIntensity.rank <= Intensity.SPICY.rank)
    }

    @Test
    fun cooldownSoftensTheEnding() {
        val plan = SessionDirector().plan(
            session = SessionState(heat = 90, completedCount = 14),
            progress = 0.96f,
            consent = allConsent,
        )

        assertEquals(SessionPhase.COOLDOWN, plan.phase)
        assertTrue(plan.targetIntensity.rank <= Intensity.HOT.rank)
    }

    @Test
    fun recentCategoriesAreDeprioritizedForVariety() {
        val plan = SessionDirector().plan(
            session = SessionState(
                recentCategories = listOf(Category.FLIRT, Category.FLIRT, Category.TOUCH),
            ),
            progress = 0.45f,
            consent = allConsent,
        )

        assertTrue(Category.FLIRT !in plan.preferredCategories)
        assertTrue(plan.preferredCategories.isNotEmpty())
    }
}
