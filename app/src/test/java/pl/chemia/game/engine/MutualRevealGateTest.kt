package pl.chemia.game.engine

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MutualRevealGateTest {
    @Test
    fun requiredCardStaysHiddenUntilBothPartnersConfirm() {
        var gate = MutualRevealGate.required("card-1")
        assertFalse(gate.canReveal)

        gate = gate.confirmPartnerA()
        assertFalse(gate.canReveal)

        gate = gate.confirmPartnerB()
        assertTrue(gate.canReveal)
    }

    @Test
    fun nonRequiredCardIsImmediatelyRevealable() {
        val gate = MutualRevealGate.notRequired("card-2")
        assertTrue(gate.canReveal)
    }

    @Test
    fun movingToAnotherCardResetsBothConfirmations() {
        val confirmed = MutualRevealGate.required("card-1")
            .confirmPartnerA()
            .confirmPartnerB()
        assertTrue(confirmed.canReveal)

        val next = confirmed.forCard("card-2", required = true)
        assertFalse(next.partnerAConfirmed)
        assertFalse(next.partnerBConfirmed)
        assertFalse(next.canReveal)
    }
}
