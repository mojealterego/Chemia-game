package pl.chemia.game

import kotlin.random.Random

data class GameCard(
    val id: String,
    val category: String,
    val intensity: Int,
    val heat: Int,
    val title: String,
    val text: String,
)

data class SessionState(
    val heat: Int = 0,
    val recentIds: List<String> = emptyList(),
)

class GameEngine(
    private val random: Random = Random.Default,
) {
    fun nextCard(
        deck: List<GameCard>,
        maxIntensity: Int,
        recentIds: List<String>,
    ): GameCard {
        val allowed = deck.filter { it.intensity <= maxIntensity }
        require(allowed.isNotEmpty()) { "Brak kart dla wybranego poziomu" }

        val recent = recentIds.takeLast(8).toSet()
        val fresh = allowed.filterNot { it.id in recent }
        val pool = fresh.ifEmpty { allowed }

        return pool[random.nextInt(pool.size)]
    }

    fun skip(state: SessionState, cardId: String): SessionState =
        state.copy(recentIds = (state.recentIds + cardId).takeLast(8))

    fun complete(state: SessionState, card: GameCard): SessionState =
        state.copy(
            heat = (state.heat + card.heat).coerceAtMost(100),
            recentIds = (state.recentIds + card.id).takeLast(8),
        )
}
