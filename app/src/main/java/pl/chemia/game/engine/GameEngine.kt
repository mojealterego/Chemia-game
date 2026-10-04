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
    ): GameCard {
        val allowed = deck.filter {
            it.intensity.rank <= maxIntensity.rank && it.category in allowedCategories
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
            card to selectionWeight(card, state, favoriteCategories)
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
    ): Int {
        var weight = 1
        if (state.heat >= 50 && card.intensity.rank >= Intensity.HOT.rank) weight += 2
        if (state.chain > 0 && card.chain) weight += 2
        if (card.category in favoriteCategories) weight += 2
        return weight
    }

    fun skip(state: SessionState, cardId: String): SessionState =
        state.copy(
            chain = 0,
            recentIds = (state.recentIds + cardId).takeLast(8),
            skippedIds = state.skippedIds + cardId,
            skippedCount = state.skippedCount + 1,
        )

    fun complete(state: SessionState, card: GameCard): SessionState =
        state.copy(
            heat = (state.heat + card.heat).coerceAtMost(100),
            chain = (state.chain + 1).coerceAtMost(9),
            recentIds = (state.recentIds + card.id).takeLast(8),
            completedCount = state.completedCount + 1,
        )
}
