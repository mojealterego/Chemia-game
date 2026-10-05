package pl.chemia.game.engine

import pl.chemia.game.model.Category
import pl.chemia.game.model.EffectiveConsent
import pl.chemia.game.model.Intensity
import pl.chemia.game.model.SessionState

enum class SessionPhase {
    WARMUP,
    BUILD,
    PEAK,
    COOLDOWN,
}

data class DirectorPlan(
    val phase: SessionPhase,
    val targetIntensity: Intensity,
    val preferredCategories: Set<Category>,
    val deescalated: Boolean,
)

class SessionDirector {
    fun plan(
        session: SessionState,
        progress: Float,
        consent: EffectiveConsent,
    ): DirectorPlan {
        val normalizedProgress = progress.coerceIn(0f, 1f)
        val phase = when {
            normalizedProgress < 0.18f -> SessionPhase.WARMUP
            normalizedProgress < 0.58f -> SessionPhase.BUILD
            normalizedProgress < 0.88f -> SessionPhase.PEAK
            else -> SessionPhase.COOLDOWN
        }

        val skipPressure =
            session.consecutiveSkips >= 2 ||
                session.skippedCount > session.completedCount + 1

        val phaseTarget = when (phase) {
            SessionPhase.WARMUP -> Intensity.SPICY
            SessionPhase.BUILD -> if (session.heat >= 35) Intensity.HOT else Intensity.SPICY
            SessionPhase.PEAK -> consent.maxIntensity
            SessionPhase.COOLDOWN -> Intensity.HOT
        }

        val target = minIntensity(
            consent.maxIntensity,
            if (skipPressure) Intensity.SPICY else phaseTarget,
        )

        val phaseCategories = when (phase) {
            SessionPhase.WARMUP -> setOf(Category.CONNECTION, Category.FLIRT, Category.QUESTION)
            SessionPhase.BUILD -> setOf(Category.FLIRT, Category.TOUCH, Category.KISS, Category.QUESTION)
            SessionPhase.PEAK -> setOf(Category.TOUCH, Category.KISS, Category.ROLEPLAY, Category.FLIRT)
            SessionPhase.COOLDOWN -> setOf(Category.CONNECTION, Category.QUESTION, Category.FLIRT)
        }

        val consentedPhaseCategories = phaseCategories intersect consent.allowedCategories
        val candidates = if (consentedPhaseCategories.isNotEmpty()) {
            consentedPhaseCategories
        } else {
            consent.allowedCategories
        }

        val recent = session.recentCategories.takeLast(2).toSet()
        val varied = candidates - recent
        val preferred = when {
            varied.isNotEmpty() -> varied
            candidates.isNotEmpty() -> candidates
            else -> consent.allowedCategories
        }

        return DirectorPlan(
            phase = phase,
            targetIntensity = target,
            preferredCategories = preferred,
            deescalated = skipPressure,
        )
    }

    private fun minIntensity(a: Intensity, b: Intensity): Intensity =
        if (a.rank <= b.rank) a else b
}
