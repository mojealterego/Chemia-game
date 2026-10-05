package pl.chemia.game.data

import pl.chemia.game.model.Category
import pl.chemia.game.model.GameCard
import pl.chemia.game.model.Intensity

data class CatalogValidationResult(
    val errors: List<String>,
) {
    val isValid: Boolean get() = errors.isEmpty()
}

object CardCatalogValidator {
    fun validate(cards: List<GameCard>): CatalogValidationResult {
        val errors = mutableListOf<String>()

        cards.groupBy { it.id }
            .filterValues { it.size > 1 }
            .keys
            .forEach { errors += "Duplicate card id: $it" }

        cards.forEach { card ->
            if (card.id.isBlank()) errors += "Card id must not be blank"
            if (card.title.isBlank()) errors += "${card.id}: title must not be blank"
            if (card.text.isBlank()) errors += "${card.id}: text must not be blank"
            if (card.heat !in 0..20) errors += "${card.id}: heat must be in 0..20"

            if (card.intensity.rank >= Intensity.HOT.rank && !card.requiresMutualYes) {
                errors += "${card.id}: HOT/EXTREME cards require mutual YES"
            }

            if (card.afterglow || card.category == Category.AFTERGLOW) {
                if (card.category != Category.AFTERGLOW || !card.afterglow) {
                    errors += "${card.id}: afterglow flag and category must match"
                }
                if (card.intensity != Intensity.SOFT) {
                    errors += "${card.id}: afterglow must use SOFT intensity"
                }
                if (card.heat != 0) {
                    errors += "${card.id}: afterglow must not add heat"
                }
            }
        }

        return CatalogValidationResult(errors)
    }

    fun requireValid(cards: List<GameCard>): List<GameCard> {
        val result = validate(cards)
        require(result.isValid) {
            "Invalid CHEMIA card catalog:\n" + result.errors.joinToString("\n")
        }
        return cards
    }
}
