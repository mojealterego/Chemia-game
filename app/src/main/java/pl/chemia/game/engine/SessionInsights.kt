package pl.chemia.game.engine

import pl.chemia.game.model.SessionState

data class SessionInsights(
    val completionRate: Int,
    val varietyCount: Int,
) {
    companion object {
        fun from(state: SessionState): SessionInsights {
            val actions = state.completedCount + state.skippedCount
            val completionRate = if (actions == 0) {
                0
            } else {
                ((state.completedCount * 100f) / actions).toInt().coerceIn(0, 100)
            }
            return SessionInsights(
                completionRate = completionRate,
                varietyCount = state.seenCategories.size,
            )
        }
    }
}
