package pl.chemia.game.engine

data class MutualRevealGate(
    val cardId: String,
    val required: Boolean,
    val partnerAConfirmed: Boolean,
    val partnerBConfirmed: Boolean,
) {
    val canReveal: Boolean
        get() = !required || (partnerAConfirmed && partnerBConfirmed)

    fun confirmPartnerA(): MutualRevealGate =
        if (!required) this else copy(partnerAConfirmed = true)

    fun confirmPartnerB(): MutualRevealGate =
        if (!required) this else copy(partnerBConfirmed = true)

    fun forCard(cardId: String, required: Boolean): MutualRevealGate =
        if (this.cardId == cardId && this.required == required) {
            this
        } else if (required) {
            required(cardId)
        } else {
            notRequired(cardId)
        }

    companion object {
        fun required(cardId: String): MutualRevealGate =
            MutualRevealGate(
                cardId = cardId,
                required = true,
                partnerAConfirmed = false,
                partnerBConfirmed = false,
            )

        fun notRequired(cardId: String): MutualRevealGate =
            MutualRevealGate(
                cardId = cardId,
                required = false,
                partnerAConfirmed = true,
                partnerBConfirmed = true,
            )
    }
}
