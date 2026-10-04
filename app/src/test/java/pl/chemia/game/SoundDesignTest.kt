package pl.chemia.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SoundDesignTest {

    @Test
    fun drawCueIsShortAndBright() {
        val cue = SoundDesign.profile(SoundCue.DRAW)
        assertTrue(cue.durationMs in 40..160)
        assertTrue(cue.frequencyHz >= 500)
    }

    @Test
    fun completeCueIsWarmerAndLongerThanSkip() {
        val complete = SoundDesign.profile(SoundCue.COMPLETE)
        val skip = SoundDesign.profile(SoundCue.SKIP)

        assertTrue(complete.durationMs > skip.durationMs)
        assertTrue(complete.frequencyHz > skip.frequencyHz)
    }

    @Test
    fun heatMaxCueIsDistinctAndStrongest() {
        val heat = SoundDesign.profile(SoundCue.HEAT_MAX)
        val complete = SoundDesign.profile(SoundCue.COMPLETE)

        assertTrue(heat.durationMs >= complete.durationMs)
        assertTrue(heat.amplitude >= complete.amplitude)
        assertEquals(2, heat.pulses)
    }
}
