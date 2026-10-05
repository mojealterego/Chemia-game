package pl.chemia.game.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class MotionPolicyTest {
    @Test
    fun reducedMotionEliminatesDecorativeAnimationDuration() {
        assertEquals(0, MotionPolicy.duration(650, reducedMotion = true))
    }

    @Test
    fun standardMotionKeepsDesignedDuration() {
        assertEquals(650, MotionPolicy.duration(650, reducedMotion = false))
    }

    @Test
    fun negativeDurationsAreAlwaysClamped() {
        assertEquals(0, MotionPolicy.duration(-1, reducedMotion = false))
    }
}
