package pl.chemia.game.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ConsentProfileTest {
    @Test
    fun effectiveConsentUsesOnlySharedCategories() {
        val a = ConsentProfile(
            allowedCategories = setOf(Category.CONNECTION, Category.FLIRT, Category.TOUCH),
            maxIntensity = Intensity.HOT,
        )
        val b = ConsentProfile(
            allowedCategories = setOf(Category.FLIRT, Category.QUESTION),
            maxIntensity = Intensity.SPICY,
        )

        val effective = intersectConsent(a, b)

        assertEquals(setOf(Category.FLIRT), effective.allowedCategories)
        assertEquals(Intensity.SPICY, effective.maxIntensity)
        assertTrue(effective.canStart)
    }

    @Test
    fun noSharedCategoryPreventsSessionStart() {
        val effective = intersectConsent(
            ConsentProfile(setOf(Category.CONNECTION), Intensity.SPICY),
            ConsentProfile(setOf(Category.QUESTION), Intensity.SPICY),
        )
        assertFalse(effective.canStart)
    }
}
