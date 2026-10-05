package pl.chemia.game

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import kotlin.math.PI
import kotlin.math.sin

enum class SoundCue {
    START,
    CONSENT,
    DRAW,
    COMPLETE,
    SKIP,
    DISCREET,
    HEAT_MAX,
    AFTERGLOW,
}

data class SoundProfile(
    val frequencyHz: Int,
    val endFrequencyHz: Int,
    val durationMs: Int,
    val amplitude: Double,
    val pulses: Int = 1,
    val harmonicMix: Double = 0.22,
)

object SoundDesign {
    fun profile(cue: SoundCue): SoundProfile = when (cue) {
        SoundCue.START -> SoundProfile(
            frequencyHz = 520,
            endFrequencyHz = 760,
            durationMs = 165,
            amplitude = 0.18,
            harmonicMix = 0.24,
        )
        SoundCue.CONSENT -> SoundProfile(
            frequencyHz = 540,
            endFrequencyHz = 660,
            durationMs = 105,
            amplitude = 0.15,
            pulses = 2,
            harmonicMix = 0.18,
        )
        SoundCue.DRAW -> SoundProfile(
            frequencyHz = 690,
            endFrequencyHz = 940,
            durationMs = 92,
            amplitude = 0.17,
            harmonicMix = 0.21,
        )
        SoundCue.COMPLETE -> SoundProfile(
            frequencyHz = 860,
            endFrequencyHz = 1_080,
            durationMs = 190,
            amplitude = 0.25,
            harmonicMix = 0.31,
        )
        SoundCue.SKIP -> SoundProfile(
            frequencyHz = 250,
            endFrequencyHz = 205,
            durationMs = 68,
            amplitude = 0.095,
            harmonicMix = 0.08,
        )
        SoundCue.DISCREET -> SoundProfile(
            frequencyHz = 310,
            endFrequencyHz = 245,
            durationMs = 130,
            amplitude = 0.10,
            harmonicMix = 0.10,
        )
        SoundCue.HEAT_MAX -> SoundProfile(
            frequencyHz = 980,
            endFrequencyHz = 1_320,
            durationMs = 235,
            amplitude = 0.31,
            pulses = 3,
            harmonicMix = 0.34,
        )
        SoundCue.AFTERGLOW -> SoundProfile(
            frequencyHz = 620,
            endFrequencyHz = 390,
            durationMs = 420,
            amplitude = 0.14,
            harmonicMix = 0.16,
        )
    }
}

object SoundEngine {
    private const val SAMPLE_RATE = 44_100
    private const val PULSE_GAP_MS = 48

    private val executor = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "chemia-audio").apply { isDaemon = true }
    }
    private val sampleCache = ConcurrentHashMap<SoundCue, ShortArray>()

    fun play(cue: SoundCue) {
        executor.execute {
            runCatching {
                val profile = SoundDesign.profile(cue)
                val samples = sampleCache.getOrPut(cue) { buildSamples(profile) }
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .setBufferSizeInBytes(samples.size * Short.SIZE_BYTES)
                    .build()

                try {
                    track.write(samples, 0, samples.size)
                    track.play()
                    Thread.sleep(totalDurationMs(profile).toLong() + 28L)
                } finally {
                    runCatching { track.stop() }
                    track.release()
                }
            }
        }
    }

    private fun totalDurationMs(profile: SoundProfile): Int =
        (profile.durationMs * profile.pulses) + (PULSE_GAP_MS * (profile.pulses - 1))

    private fun buildSamples(profile: SoundProfile): ShortArray {
        val pulseSamples = SAMPLE_RATE * profile.durationMs / 1000
        val gapSamples = SAMPLE_RATE * PULSE_GAP_MS / 1000
        val totalSamples = pulseSamples * profile.pulses + gapSamples * (profile.pulses - 1)
        val data = ShortArray(totalSamples)

        repeat(profile.pulses) { pulse ->
            val start = pulse * (pulseSamples + gapSamples)
            var phase = 0.0

            for (i in 0 until pulseSamples) {
                val progress = i.toDouble() / pulseSamples.coerceAtLeast(1)
                val frequency = profile.frequencyHz +
                    ((profile.endFrequencyHz - profile.frequencyHz) * smoothStep(progress))
                phase += 2.0 * PI * frequency / SAMPLE_RATE

                val attack = smoothStep((progress / 0.12).coerceIn(0.0, 1.0))
                val release = smoothStep(((1.0 - progress) / 0.22).coerceIn(0.0, 1.0))
                val envelope = attack * release

                val fundamental = sin(phase)
                val second = sin(phase * 2.0) * profile.harmonicMix
                val third = sin(phase * 3.0) * (profile.harmonicMix * 0.22)
                val pulseAccent = 1.0 - (pulse * 0.06)
                val sample = (fundamental + second + third) *
                    profile.amplitude *
                    envelope *
                    pulseAccent *
                    0.72

                data[start + i] = (sample * Short.MAX_VALUE)
                    .toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                    .toShort()
            }
        }
        return data
    }

    private fun smoothStep(value: Double): Double {
        val x = value.coerceIn(0.0, 1.0)
        return x * x * (3.0 - 2.0 * x)
    }
}
