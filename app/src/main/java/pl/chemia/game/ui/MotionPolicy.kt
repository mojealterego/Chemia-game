package pl.chemia.game.ui

object MotionPolicy {
    fun duration(designedDurationMs: Int, reducedMotion: Boolean): Int =
        if (reducedMotion) 0 else designedDurationMs.coerceAtLeast(0)
}
