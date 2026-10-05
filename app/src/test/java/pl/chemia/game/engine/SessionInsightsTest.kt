package pl.chemia.game.engine

import org.junit.Assert.assertEquals
import org.junit.Test
import pl.chemia.game.model.Category
import pl.chemia.game.model.SessionState

class SessionInsightsTest {
    @Test
    fun emptySessionHasZeroCompletionRate() {
        val insights = SessionInsights.from(SessionState())
        assertEquals(0, insights.completionRate)
    }

    @Test
    fun completionRateUsesCompletedAndSkippedActions() {
        val insights = SessionInsights.from(
            SessionState(completedCount = 3, skippedCount = 1)
        )
        assertEquals(75, insights.completionRate)
    }

    @Test
    fun varietyCountsUniqueCategoriesAcrossTheSession() {
        val insights = SessionInsights.from(
            SessionState(
                seenCategories = setOf(
                    Category.CONNECTION,
                    Category.FLIRT,
                    Category.TOUCH,
                    Category.QUESTION,
                )
            )
        )
        assertEquals(4, insights.varietyCount)
    }
}
