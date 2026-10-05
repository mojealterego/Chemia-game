package pl.chemia.game.engine

import pl.chemia.game.model.Category
import pl.chemia.game.model.GameCard
import pl.chemia.game.model.Intensity
import pl.chemia.game.model.SessionState
import kotlin.random.Random

class GameEngine(
    private val random: Random = Random.Default,
) {
    fun next(
        deck: List<GameCard>,
        state: SessionState,
        maxIntensity: Intensity,
        allowedCategories: Set<Category>,
        favoriteCategories: Set<Category> = emptySet(),
        directorPlan: DirectorPlan? = null,
    ): GameCard {
        val directorCeiling = directorPlan?.targetIntensity ?: maxIntensity
        val effectiveMax = if (directorCeiling.rank <= maxIntensity.rank) directorCeiling else maxIntensity

        val allowed = deck.filter {
            it.intensity.rank <= effectiveMax.rank && it.category in allowedCategories
        }
        require(allowed.isNotEmpty()) { "Brak kart dla wspólnych ustawień zgody" }

        val recent = state.recentIds.takeLast(8).toSet()
        val preferred = allowed.filterNot { it.id in recent || it.id in state.skippedIds }
        val nonSkipped = allowed.filterNot { it.id in state.skippedIds }
        val pool = when {
            preferred.isNotEmpty() -> preferred
            nonSkipped.isNotEmpty() -> nonSkipped
            else -> allowed
        }

        val weighted = pool.map { card ->
            card to selectionWeight(card, state, favoriteCategories, directorPlan)
        }
        val totalWeight = weighted.sumOf { it.second }
        var ticket = random.nextInt(totalWeight)

        for ((card, weight) in weighted) {
            ticket -= weight
            if (ticket < 0) return card
        }
        return weighted.last().first
    }

    fun selectionWeight(
        card: GameCard,
        state: SessionState,
        favoriteCategories: Set<Category>,
        directorPlan: DirectorPlan? = null,
    ): Int {
        var weight = 2
        if (state.heat >= 50 && card.intensity.rank >= Intensity.HOT.rank) weight += 2
        if (state.chain > 0 && card.chain) weight += 2
        if (card.category in favoriteCategories) weight += 2
        if (card.category in directorPlan.orEmptyPreferred()) weight += 5
        if (card.intensity == directorPlan?.targetIntensity) weight += 3

        val recentCategories = state.recentCategories.takeLast(2)
        val repeats = recentCategories.count { it == card.category }
        weight -= repeats.coerceAtMost(2)

        return weight.coerceAtLeast(1)
    }

    fun skip(state: SessionState, card: GameCard): SessionState =
        state.copy(
            chain = 0,
            recentIds = (state.recentIds + card.id).takeLast(8),
            skippedIds = state.skippedIds + card.id,
            recentCategories = (state.recentCategories + card.category).takeLast(6),
            seenCategories = state.seenCategories + card.category,
            consecutiveSkips = (state.consecutiveSkips + 1).coerceAtMost(9),
            skippedCount = state.skippedCount + 1,
        )

    fun complete(state: SessionState, card: GameCard): SessionState =
        state.copy(
            heat = (state.heat + card.heat).coerceAtMost(100),
            chain = (state.chain + 1).coerceAtMost(9),
            recentIds = (state.recentIds + card.id).takeLast(8),
            recentCategories = (state.recentCategories + card.category).takeLast(6),
            seenCategories = state.seenCategories + card.category,
            consecutiveSkips = 0,
            completedCount = state.completedCount + 1,
        )

    private fun DirectorPlan?.orEmptyPreferred(): Set<Category> =
        this?.preferredCategories ?: emptySet()
}
