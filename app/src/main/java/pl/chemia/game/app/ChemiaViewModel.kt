package pl.chemia.game.app

import android.app.Application
import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.chemia.game.BuildConfig
import pl.chemia.game.data.CardRepository
import pl.chemia.game.data.SettingsRepository
import pl.chemia.game.data.UserSettings
import pl.chemia.game.engine.GameEngine
import pl.chemia.game.engine.SessionDirector
import pl.chemia.game.engine.SessionPhase
import pl.chemia.game.model.Category
import pl.chemia.game.model.ConsentProfile
import pl.chemia.game.model.EffectiveConsent
import pl.chemia.game.model.GameCard
import pl.chemia.game.model.Intensity
import pl.chemia.game.model.SessionState
import pl.chemia.game.model.intersectConsent
import kotlin.random.Random

data class ChemiaUiState(
    val consentA: ConsentProfile = ConsentProfile(),
    val consentB: ConsentProfile = ConsentProfile(),
    val effectiveConsent: EffectiveConsent = intersectConsent(ConsentProfile(), ConsentProfile()),
    val consentConflict: Boolean = false,
    val playerA: String = "Partner 1",
    val playerB: String = "Partner 2",
    val durationMinutes: Int = 30,
    val settings: UserSettings = UserSettings(),
    val session: SessionState = SessionState(),
    val currentCard: GameCard? = null,
    val currentPlayerIndex: Int = 0,
    val sessionStartedAtMs: Long? = null,
    val afterglowCard: GameCard? = null,
    val directorPhase: SessionPhase = SessionPhase.WARMUP,
    val directorDeescalated: Boolean = false,
)

class ChemiaViewModel(application: Application) : AndroidViewModel(application) {
    private val cardRepository = CardRepository(application)
    private val settingsRepository = SettingsRepository(application)
    private val engine = GameEngine()
    private val director = SessionDirector()
    private val random = Random.Default

    private val deck: List<GameCard> = cardRepository.load()
    private val sessionDeck: List<GameCard> = deck.filterNot { it.afterglow || it.category == Category.AFTERGLOW }
    private val afterglowDeck: List<GameCard> = deck.filter { it.afterglow || it.category == Category.AFTERGLOW }

    var uiState by mutableStateOf(ChemiaUiState())
        private set

    val maxSelectableIntensity: Intensity
        get() = if (BuildConfig.ADULT_CONTENT) Intensity.EXTREME else Intensity.SPICY

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                uiState = uiState.copy(settings = settings)
            }
        }
    }

    fun newGame() {
        val defaultMax = if (BuildConfig.ADULT_CONTENT) Intensity.HOT else Intensity.SPICY
        val defaultProfile = ConsentProfile(
            allowedCategories = Category.entries.filterNot { it == Category.AFTERGLOW }.toSet(),
            maxIntensity = defaultMax,
        )
        uiState = uiState.copy(
            consentA = defaultProfile,
            consentB = defaultProfile,
            effectiveConsent = intersectConsent(defaultProfile, defaultProfile),
            consentConflict = false,
            session = SessionState(),
            currentCard = null,
            currentPlayerIndex = 0,
            sessionStartedAtMs = null,
            afterglowCard = null,
            directorPhase = SessionPhase.WARMUP,
            directorDeescalated = false,
        )
    }

    fun updateConsentA(profile: ConsentProfile) {
        uiState = uiState.copy(consentA = profile, consentConflict = false)
    }

    fun updateConsentB(profile: ConsentProfile) {
        uiState = uiState.copy(consentB = profile, consentConflict = false)
    }

    fun finalizeConsent(): Boolean {
        val effective = intersectConsent(uiState.consentA, uiState.consentB)
        val valid = effective.allowedCategories.isNotEmpty()
        uiState = uiState.copy(
            effectiveConsent = effective,
            consentConflict = !valid,
        )
        return valid
    }

    fun setPlayerA(value: String) {
        uiState = uiState.copy(playerA = value.take(24))
    }

    fun setPlayerB(value: String) {
        uiState = uiState.copy(playerB = value.take(24))
    }

    fun setDuration(minutes: Int) {
        uiState = uiState.copy(durationMinutes = minutes.coerceIn(15, 60))
    }

    fun setSound(enabled: Boolean) {
        uiState = uiState.copy(settings = uiState.settings.copy(soundEnabled = enabled))
        viewModelScope.launch { settingsRepository.setSound(enabled) }
    }

    fun setHaptics(enabled: Boolean) {
        uiState = uiState.copy(settings = uiState.settings.copy(hapticsEnabled = enabled))
        viewModelScope.launch { settingsRepository.setHaptics(enabled) }
    }

    fun startSession() {
        val clean = SessionState()
        uiState = uiState.copy(
            session = clean,
            currentPlayerIndex = 0,
            sessionStartedAtMs = SystemClock.elapsedRealtime(),
            afterglowCard = null,
        )
        drawNext(clean)
    }

    fun skipCurrent() {
        val current = uiState.currentCard ?: return
        val updated = engine.skip(uiState.session, current)
        uiState = uiState.copy(
            session = updated,
            currentPlayerIndex = 1 - uiState.currentPlayerIndex,
        )
        drawNext(updated)
    }

    fun completeCurrent(): Boolean {
        val current = uiState.currentCard ?: return false
        val updated = engine.complete(uiState.session, current)
        val reachedAfterglow = updated.heat >= 100
        uiState = uiState.copy(
            session = updated,
            currentPlayerIndex = 1 - uiState.currentPlayerIndex,
        )
        if (!reachedAfterglow) {
            drawNext(updated)
        }
        return reachedAfterglow
    }

    fun reviewConsent() {
        uiState = uiState.copy(
            effectiveConsent = intersectConsent(
                ConsentProfile(allowedCategories = emptySet(), maxIntensity = Intensity.SOFT),
                ConsentProfile(allowedCategories = emptySet(), maxIntensity = Intensity.SOFT),
            ),
            consentConflict = false,
            session = SessionState(),
            currentCard = null,
            currentPlayerIndex = 0,
            sessionStartedAtMs = null,
            afterglowCard = null,
            directorPhase = SessionPhase.WARMUP,
            directorDeescalated = false,
        )
    }

    fun nextAfterglow() {
        uiState = uiState.copy(
            afterglowCard = if (afterglowDeck.isEmpty()) null else afterglowDeck[random.nextInt(afterglowDeck.size)]
        )
    }

    fun remainingSeconds(nowElapsedMs: Long = SystemClock.elapsedRealtime()): Long {
        val started = uiState.sessionStartedAtMs ?: return uiState.durationMinutes * 60L
        val elapsed = ((nowElapsedMs - started) / 1000L).coerceAtLeast(0L)
        return (uiState.durationMinutes * 60L - elapsed).coerceAtLeast(0L)
    }

    fun clearSensitiveSession() {
        uiState = uiState.copy(
            consentA = ConsentProfile(allowedCategories = emptySet(), maxIntensity = Intensity.SOFT),
            consentB = ConsentProfile(allowedCategories = emptySet(), maxIntensity = Intensity.SOFT),
            effectiveConsent = intersectConsent(
                ConsentProfile(allowedCategories = emptySet(), maxIntensity = Intensity.SOFT),
                ConsentProfile(allowedCategories = emptySet(), maxIntensity = Intensity.SOFT),
            ),
            consentConflict = false,
            playerA = "Partner 1",
            playerB = "Partner 2",
            session = SessionState(),
            currentCard = null,
            currentPlayerIndex = 0,
            sessionStartedAtMs = null,
            afterglowCard = null,
            directorPhase = SessionPhase.WARMUP,
            directorDeescalated = false,
        )
    }

    private fun drawNext(session: SessionState) {
        val consent = uiState.effectiveConsent
        val durationSeconds = (uiState.durationMinutes * 60L).coerceAtLeast(1L)
        val progress = 1f - (remainingSeconds().toFloat() / durationSeconds.toFloat())
        val plan = director.plan(
            session = session,
            progress = progress,
            consent = consent,
        )
        val card = engine.next(
            deck = sessionDeck,
            state = session,
            maxIntensity = consent.maxIntensity,
            allowedCategories = consent.allowedCategories,
            favoriteCategories = emptySet(),
            directorPlan = plan,
        )
        uiState = uiState.copy(
            currentCard = card,
            directorPhase = plan.phase,
            directorDeescalated = plan.deescalated,
        )
    }
}
