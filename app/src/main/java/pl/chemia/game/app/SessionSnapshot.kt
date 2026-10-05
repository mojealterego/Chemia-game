package pl.chemia.game.app

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import pl.chemia.game.data.UserSettings
import pl.chemia.game.engine.SessionPhase
import pl.chemia.game.model.Category
import pl.chemia.game.model.ConsentProfile
import pl.chemia.game.model.GameCard
import pl.chemia.game.model.Intensity
import pl.chemia.game.model.SessionState
import pl.chemia.game.model.SessionStyle
import pl.chemia.game.model.intersectConsent

@Parcelize
data class SessionSnapshot(
    val consentACategories: List<String>,
    val consentAIntensity: Int,
    val consentBCategories: List<String>,
    val consentBIntensity: Int,
    val playerA: String,
    val playerB: String,
    val durationMinutes: Int,
    val sessionStyle: String,
    val heat: Int,
    val chain: Int,
    val recentIds: List<String>,
    val skippedIds: List<String>,
    val recentCategories: List<String>,
    val seenCategories: List<String>,
    val consecutiveSkips: Int,
    val completedCount: Int,
    val skippedCount: Int,
    val currentCardId: String?,
    val currentPlayerIndex: Int,
    val sessionEndsAtEpochMs: Long?,
    val afterglowCardId: String?,
    val directorPhase: String,
    val directorDeescalated: Boolean,
) : Parcelable {
    fun restore(deck: List<GameCard>, settings: UserSettings): ChemiaUiState {
        val consentA = ConsentProfile(
            allowedCategories = consentACategories.mapNotNull(::categoryOrNull).toSet(),
            maxIntensity = Intensity.fromRank(consentAIntensity),
        )
        val consentB = ConsentProfile(
            allowedCategories = consentBCategories.mapNotNull(::categoryOrNull).toSet(),
            maxIntensity = Intensity.fromRank(consentBIntensity),
        )
        val session = SessionState(
            heat = heat.coerceIn(0, 100),
            chain = chain.coerceIn(0, 9),
            recentIds = recentIds.takeLast(8),
            skippedIds = skippedIds.toSet(),
            recentCategories = recentCategories.mapNotNull(::categoryOrNull).takeLast(6),
            seenCategories = seenCategories.mapNotNull(::categoryOrNull).toSet(),
            consecutiveSkips = consecutiveSkips.coerceIn(0, 9),
            completedCount = completedCount.coerceAtLeast(0),
            skippedCount = skippedCount.coerceAtLeast(0),
        )
        val currentCard = currentCardId?.let { id -> deck.firstOrNull { it.id == id } }
        val afterglowCard = afterglowCardId?.let { id -> deck.firstOrNull { it.id == id } }
        val activeSnapshotIsValid = sessionEndsAtEpochMs == null || currentCardId == null || currentCard != null

        if (!activeSnapshotIsValid) {
            return ChemiaUiState(settings = settings)
        }

        return ChemiaUiState(
            consentA = consentA,
            consentB = consentB,
            effectiveConsent = intersectConsent(consentA, consentB),
            consentConflict = false,
            playerA = playerA.take(24).ifBlank { "Partner 1" },
            playerB = playerB.take(24).ifBlank { "Partner 2" },
            durationMinutes = durationMinutes.coerceIn(15, 60),
            sessionStyle = enumValueOrDefault(sessionStyle, SessionStyle.CHEMISTRY),
            settings = settings,
            session = session,
            currentCard = currentCard,
            currentPlayerIndex = currentPlayerIndex.coerceIn(0, 1),
            sessionEndsAtEpochMs = sessionEndsAtEpochMs,
            afterglowCard = afterglowCard,
            directorPhase = enumValueOrDefault(directorPhase, SessionPhase.WARMUP),
            directorDeescalated = directorDeescalated,
        )
    }

    companion object {
        fun from(state: ChemiaUiState): SessionSnapshot = SessionSnapshot(
            consentACategories = state.consentA.allowedCategories.map(Category::name),
            consentAIntensity = state.consentA.maxIntensity.rank,
            consentBCategories = state.consentB.allowedCategories.map(Category::name),
            consentBIntensity = state.consentB.maxIntensity.rank,
            playerA = state.playerA,
            playerB = state.playerB,
            durationMinutes = state.durationMinutes,
            sessionStyle = state.sessionStyle.name,
            heat = state.session.heat,
            chain = state.session.chain,
            recentIds = state.session.recentIds,
            skippedIds = state.session.skippedIds.toList(),
            recentCategories = state.session.recentCategories.map(Category::name),
            seenCategories = state.session.seenCategories.map(Category::name),
            consecutiveSkips = state.session.consecutiveSkips,
            completedCount = state.session.completedCount,
            skippedCount = state.session.skippedCount,
            currentCardId = state.currentCard?.id,
            currentPlayerIndex = state.currentPlayerIndex,
            sessionEndsAtEpochMs = state.sessionEndsAtEpochMs,
            afterglowCardId = state.afterglowCard?.id,
            directorPhase = state.directorPhase.name,
            directorDeescalated = state.directorDeescalated,
        )
    }
}

private fun categoryOrNull(value: String): Category? =
    runCatching { Category.valueOf(value) }.getOrNull()

private inline fun <reified T : Enum<T>> enumValueOrDefault(value: String, default: T): T =
    enumValues<T>().firstOrNull { it.name == value } ?: default
