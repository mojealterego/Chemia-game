package pl.chemia.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SoundDesignTest {

    @Test
    fun drawCueIsShortBrightAndRising() {
        val cue = SoundDesign.profile(SoundCue.DRAW)
        assertTrue(cue.durationMs in 40..160)
        assertTrue(cue.frequencyHz >= 500)
        assertTrue(cue.endFrequencyHz > cue.frequencyHz)
    }

    @Test
    fun completeCueIsWarmerAndLongerThanSkip() {
        val complete = SoundDesign.profile(SoundCue.COMPLETE)
        val skip = SoundDesign.profile(SoundCue.SKIP)

        assertTrue(complete.durationMs > skip.durationMs)
        assertTrue(complete.frequencyHz > skip.frequencyHz)
        assertTrue(complete.harmonicMix > skip.harmonicMix)
    }

    @Test
    fun heatMaxCueIsDistinctAndStrongest() {
        val heat = SoundDesign.profile(SoundCue.HEAT_MAX)
        val complete = SoundDesign.profile(SoundCue.COMPLETE)

        assertTrue(heat.durationMs >= complete.durationMs)
        assertTrue(heat.amplitude >= complete.amplitude)
        assertEquals(3, heat.pulses)
    }

    @Test
    fun afterglowIsLongerSofterAndFalling() {
        val afterglow = SoundDesign.profile(SoundCue.AFTERGLOW)
        val draw = SoundDesign.profile(SoundCue.DRAW)

        assertTrue(afterglow.durationMs > draw.durationMs)
        assertTrue(afterglow.amplitude < SoundDesign.profile(SoundCue.COMPLETE).amplitude)
        assertTrue(afterglow.endFrequencyHz < afterglow.frequencyHz)
    }

    @Test
    fun discreetCueStaysLowAndSubtle() {
        val discreet = SoundDesign.profile(SoundCue.DISCREET)
        assertTrue(discreet.frequencyHz < 400)
        assertTrue(discreet.amplitude <= 0.12)
    }

    @Test
    fun consentCueHasTwoGentlePulses() {
        val consent = SoundDesign.profile(SoundCue.CONSENT)
        assertEquals(2, consent.pulses)
        assertTrue(consent.amplitude <= 0.18)
    }
}
