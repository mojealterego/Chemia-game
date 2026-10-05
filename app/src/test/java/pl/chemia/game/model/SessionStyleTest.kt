package pl.chemia.game.model

import org.junit.Assert.assertTrue
import org.junit.Test

class SessionStyleTest {
    @Test
    fun connectionStylePrefersConversationAndCloseness() {
        assertTrue(Category.CONNECTION in SessionStyle.CONNECTION.preferredCategories)
        assertTrue(Category.QUESTION in SessionStyle.CONNECTION.preferredCategories)
    }

    @Test
    fun chemistryStylePrefersFlirtKissAndTouch() {
        assertTrue(Category.FLIRT in SessionStyle.CHEMISTRY.preferredCategories)
        assertTrue(Category.KISS in SessionStyle.CHEMISTRY.preferredCategories)
        assertTrue(Category.TOUCH in SessionStyle.CHEMISTRY.preferredCategories)
    }

    @Test
    fun adventureStyleAddsRoleplayWithoutBypassingConsent() {
        assertTrue(Category.ROLEPLAY in SessionStyle.ADVENTURE.preferredCategories)
        assertTrue(Category.AFTERGLOW !in SessionStyle.ADVENTURE.preferredCategories)
    }
}
