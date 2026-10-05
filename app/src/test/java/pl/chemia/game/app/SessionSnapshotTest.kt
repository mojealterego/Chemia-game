package pl.chemia.game.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import pl.chemia.game.data.UserSettings
import pl.chemia.game.engine.SessionPhase
import pl.chemia.game.model.Category
import pl.chemia.game.model.ConsentProfile
import pl.chemia.game.model.GameCard
import pl.chemia.game.model.Intensity
import pl.chemia.game.model.SessionState
import pl.chemia.game.model.SessionStyle
import pl.chemia.game.model.intersectConsent

class SessionSnapshotTest {
    private val card = GameCard(
        id = "card-7",
        category = Category.TOUCH,
        intensity = Intensity.HOT,
        title = "T",
        text = "X",
        heat = 8,
        requiresMutualYes = true,
    )

    @Test
    fun snapshotRestoresActiveSessionWithoutPersistingCardText() {
        val consentA = ConsentProfile(setOf(Category.FLIRT, Category.TOUCH), Intensity.HOT)
        val consentB = ConsentProfile(setOf(Category.TOUCH), Intensity.HOT)
        val original = ChemiaUiState(
            consentA = consentA,
            consentB = consentB,
            effectiveConsent = intersectConsent(consentA, consentB),
            playerA = "A",
            playerB = "B",
            durationMinutes = 45,
            sessionStyle = SessionStyle.ADVENTURE,
            settings = UserSettings(soundEnabled = false, hapticsEnabled = true),
            session = SessionState(
                heat = 61,
                chain = 3,
                recentIds = listOf("x", "card-7"),
                skippedIds = setOf("skip-1"),
                seenIds = setOf("x", "card-7", "skip-1"),
                recentCategories = listOf(Category.FLIRT, Category.TOUCH),
                seenCategories = setOf(Category.FLIRT, Category.TOUCH),
                consecutiveSkips = 1,
                completedCount = 8,
                skippedCount = 2,
            ),
            currentCard = card,
            currentPlayerIndex = 1,
            sessionEndsAtEpochMs = 123456789L,
            directorPhase = SessionPhase.PEAK,
            directorDeescalated = true,
        )

        val snapshot = SessionSnapshot.from(original)
        val restored = snapshot.restore(
            deck = listOf(card),
            settings = UserSettings(soundEnabled = true, hapticsEnabled = false),
        )

        assertEquals("card-7", snapshot.currentCardId)
        assertEquals("A", restored.playerA)
        assertEquals("B", restored.playerB)
        assertEquals(61, restored.session.heat)
        assertEquals(8, restored.session.completedCount)
        assertEquals(setOf("x", "card-7", "skip-1"), restored.session.seenIds)
        assertEquals(SessionStyle.ADVENTURE, restored.sessionStyle)
        assertEquals(SessionPhase.PEAK, restored.directorPhase)
        assertEquals(card, restored.currentCard)
        assertEquals(123456789L, restored.sessionEndsAtEpochMs)
        assertEquals(true, restored.settings.soundEnabled)
    }

    @Test
    fun missingCardFailsClosedInsteadOfRestoringStaleContent() {
        val snapshot = SessionSnapshot.from(
            ChemiaUiState(
                currentCard = card,
                sessionEndsAtEpochMs = 123L,
            )
        )

        val restored = snapshot.restore(deck = emptyList(), settings = UserSettings())

        assertNull(restored.currentCard)
        assertNull(restored.sessionEndsAtEpochMs)
        assertEquals(SessionState(), restored.session)
    }

    @Test
    fun inactiveStateDoesNotCreateResumableSession() {
        val snapshot = SessionSnapshot.from(ChemiaUiState())
        assertNull(snapshot.currentCardId)
        assertNull(snapshot.sessionEndsAtEpochMs)
        assertNotNull(snapshot)
    }
}
