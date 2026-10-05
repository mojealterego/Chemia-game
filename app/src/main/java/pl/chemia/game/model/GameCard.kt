package pl.chemia.game.model

enum class Category {
    CONNECTION,
    FLIRT,
    TOUCH,
    QUESTION,
    KISS,
    ROLEPLAY,
    AFTERGLOW,
}

enum class Intensity(val rank: Int) {
    SOFT(1),
    SPICY(2),
    HOT(3),
    EXTREME(4);

    companion object {
        fun fromRank(rank: Int): Intensity =
            entries.firstOrNull { it.rank == rank } ?: EXTREME
    }
}

enum class SessionStyle(val preferredCategories: Set<Category>) {
    CONNECTION(setOf(Category.CONNECTION, Category.QUESTION, Category.FLIRT)),
    CHEMISTRY(setOf(Category.FLIRT, Category.KISS, Category.TOUCH)),
    ADVENTURE(setOf(Category.ROLEPLAY, Category.FLIRT, Category.QUESTION)),
}

data class GameCard(
    val id: String,
    val category: Category,
    val intensity: Intensity,
    val title: String,
    val text: String,
    val heat: Int,
    val tags: Set<String> = emptySet(),
    val chain: Boolean = false,
    val afterglow: Boolean = false,
    val requiresMutualYes: Boolean = false,
)

data class SessionState(
    val heat: Int = 0,
    val chain: Int = 0,
    val recentIds: List<String> = emptyList(),
    val skippedIds: Set<String> = emptySet(),
    val recentCategories: List<Category> = emptyList(),
    val seenCategories: Set<Category> = emptySet(),
    val consecutiveSkips: Int = 0,
    val completedCount: Int = 0,
    val skippedCount: Int = 0,
)
