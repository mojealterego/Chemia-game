package pl.chemia.game

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.sin

enum class SoundCue {
    START,
    DRAW,
    COMPLETE,
    SKIP,
    HEAT_MAX,
}

data class SoundProfile(
    val frequencyHz: Int,
    val durationMs: Int,
    val amplitude: Double,
    val pulses: Int = 1,
)

object SoundDesign {
    fun profile(cue: SoundCue): SoundProfile = when (cue) {
        SoundCue.START -> SoundProfile(620, 150, 0.18)
        SoundCue.DRAW -> SoundProfile(760, 90, 0.17)
        SoundCue.COMPLETE -> SoundProfile(980, 180, 0.25)
        SoundCue.SKIP -> SoundProfile(260, 65, 0.10)
        SoundCue.HEAT_MAX -> SoundProfile(1180, 230, 0.30, pulses = 2)
    }
}

object SoundEngine {
    private const val SAMPLE_RATE = 44_100

    fun play(cue: SoundCue) {
        val profile = SoundDesign.profile(cue)

        Thread({
            runCatching {
                val samples = buildSamples(profile)
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
                    .setBufferSizeInBytes(samples.size * 2)
                    .build()

                track.write(samples, 0, samples.size)
                track.play()
                Thread.sleep(totalDurationMs(profile).toLong() + 40L)
                runCatching { track.stop() }
                track.release()
            }
        }, "chemia-sound").start()
    }

    private fun totalDurationMs(profile: SoundProfile): Int =
        (profile.durationMs * profile.pulses) + (55 * (profile.pulses - 1))

    private fun buildSamples(profile: SoundProfile): ShortArray {
        val pulseSamples = SAMPLE_RATE * profile.durationMs / 1000
        val gapSamples = SAMPLE_RATE * 55 / 1000
        val totalSamples = pulseSamples * profile.pulses + gapSamples * (profile.pulses - 1)
        val data = ShortArray(totalSamples)

        repeat(profile.pulses) { pulse ->
            val start = pulse * (pulseSamples + gapSamples)
            for (i in 0 until pulseSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val progress = i.toDouble() / pulseSamples.coerceAtLeast(1)
                val envelope = when {
                    progress < 0.10 -> progress / 0.10
                    progress > 0.82 -> (1.0 - progress) / 0.18
                    else -> 1.0
                }.coerceIn(0.0, 1.0)

                val fundamental = sin(2.0 * PI * profile.frequencyHz * t)
                val harmonic = 0.28 * sin(2.0 * PI * profile.frequencyHz * 2.0 * t)
                val sample = (fundamental + harmonic) * 0.78 * profile.amplitude * envelope
                data[start + i] = (sample * Short.MAX_VALUE).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                    .toShort()
            }
        }
        return data
    }
}
