package pl.chemia.game.data

import org.junit.Assert.assertTrue
import org.junit.Test
import pl.chemia.game.model.Category
import pl.chemia.game.model.GameCard
import pl.chemia.game.model.Intensity

class CardCatalogValidatorTest {
    private fun card(
        id: String = "a",
        category: Category = Category.FLIRT,
        intensity: Intensity = Intensity.SOFT,
        title: String = "Title",
        text: String = "Text",
        heat: Int = 4,
        afterglow: Boolean = false,
        requiresMutualYes: Boolean = false,
    ) = GameCard(
        id = id,
        category = category,
        intensity = intensity,
        title = title,
        text = text,
        heat = heat,
        afterglow = afterglow,
        requiresMutualYes = requiresMutualYes,
    )

    @Test
    fun rejectsDuplicateIds() {
        val result = CardCatalogValidator.validate(listOf(card("dup"), card("dup")))
        assertTrue(result.errors.any { it.contains("duplicate", ignoreCase = true) })
    }

    @Test
    fun highIntensityCardsRequireMutualYes() {
        val result = CardCatalogValidator.validate(
            listOf(card(intensity = Intensity.HOT, requiresMutualYes = false))
        )
        assertTrue(result.errors.any { it.contains("mutual", ignoreCase = true) })
    }

    @Test
    fun afterglowMustBeSafeAndHeatFree() {
        val result = CardCatalogValidator.validate(
            listOf(
                card(
                    category = Category.AFTERGLOW,
                    intensity = Intensity.SPICY,
                    heat = 5,
                    afterglow = true,
                )
            )
        )
        assertTrue(result.errors.size >= 2)
    }

    @Test
    fun rejectsBlankContentAndOutOfRangeHeat() {
        val result = CardCatalogValidator.validate(
            listOf(card(title = " ", text = "", heat = 99))
        )
        assertTrue(result.errors.size >= 3)
    }

    @Test
    fun acceptsWellFormedCatalog() {
        val result = CardCatalogValidator.validate(
            listOf(
                card(id = "a"),
                card(
                    id = "b",
                    category = Category.TOUCH,
                    intensity = Intensity.HOT,
                    heat = 9,
                    requiresMutualYes = true,
                ),
                card(
                    id = "ag",
                    category = Category.AFTERGLOW,
                    intensity = Intensity.SOFT,
                    heat = 0,
                    afterglow = true,
                ),
            )
        )
        assertTrue(result.errors.isEmpty())
    }
}
