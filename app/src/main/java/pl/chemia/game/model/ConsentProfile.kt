package pl.chemia.game.model

data class ConsentProfile(
    val allowedCategories: Set<Category> = Category.entries.toSet(),
    val maxIntensity: Intensity = Intensity.SPICY,
) {
    fun toggle(category: Category): ConsentProfile =
        copy(
            allowedCategories = if (category in allowedCategories) {
                allowedCategories - category
            } else {
                allowedCategories + category
            }
        )
}

data class EffectiveConsent(
    val allowedCategories: Set<Category>,
    val maxIntensity: Intensity,
) {
    val canStart: Boolean get() = allowedCategories.isNotEmpty()
}

fun intersectConsent(a: ConsentProfile, b: ConsentProfile): EffectiveConsent =
    EffectiveConsent(
        allowedCategories = a.allowedCategories intersect b.allowedCategories,
        maxIntensity = if (a.maxIntensity.rank <= b.maxIntensity.rank) a.maxIntensity else b.maxIntensity,
    )
