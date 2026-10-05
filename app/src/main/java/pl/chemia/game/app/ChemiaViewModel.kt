package pl.chemia.game.app

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
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
import pl.chemia.game.model.SessionStyle
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
    val sessionStyle: SessionStyle = SessionStyle.CHEMISTRY,
    val settings: UserSettings = UserSettings(),
    val session: SessionState = SessionState(),
    val currentCard: GameCard? = null,
    val currentPlayerIndex: Int = 0,
    val sessionEndsAtEpochMs: Long? = null,
    val afterglowCard: GameCard? = null,
    val directorPhase: SessionPhase = SessionPhase.WARMUP,
    val directorDeescalated: Boolean = false,
)

class ChemiaViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {
    private val cardRepository = CardRepository(application)
    private val settingsRepository = SettingsRepository(application)
    private val engine = GameEngine()
    private val director = SessionDirector()
    private val random = Random.Default

    private val deck: List<GameCard> = cardRepository.load()
    private val sessionDeck: List<GameCard> = deck.filterNot { it.afterglow || it.category == Category.AFTERGLOW }
    private val afterglowDeck: List<GameCard> = deck.filter { it.afterglow || it.category == Category.AFTERGLOW }

    var uiState by mutableStateOf(
        savedStateHandle.get<SessionSnapshot>(SNAPSHOT_KEY)?.restore(deck, UserSettings())
            ?: ChemiaUiState()
    )
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
        commit(
            uiState.copy(
                consentA = defaultProfile,
                consentB = defaultProfile,
                effectiveConsent = intersectConsent(defaultProfile, defaultProfile),
                consentConflict = false,
                session = SessionState(),
                currentCard = null,
                currentPlayerIndex = 0,
                sessionEndsAtEpochMs = null,
                afterglowCard = null,
                directorPhase = SessionPhase.WARMUP,
                directorDeescalated = false,
            )
        )
    }

    fun updateConsentA(profile: ConsentProfile) {
        commit(uiState.copy(consentA = profile, consentConflict = false))
    }

    fun updateConsentB(profile: ConsentProfile) {
        commit(uiState.copy(consentB = profile, consentConflict = false))
    }

    fun finalizeConsent(): Boolean {
        val effective = intersectConsent(uiState.consentA, uiState.consentB)
        val valid = effective.allowedCategories.isNotEmpty()
        commit(
            uiState.copy(
                effectiveConsent = effective,
                consentConflict = !valid,
            )
        )
        return valid
    }

    fun setPlayerA(value: String) {
        commit(uiState.copy(playerA = value.take(24)))
    }

    fun setPlayerB(value: String) {
        commit(uiState.copy(playerB = value.take(24)))
    }

    fun setDuration(minutes: Int) {
        commit(uiState.copy(durationMinutes = minutes.coerceIn(15, 60)))
    }

    fun setSessionStyle(style: SessionStyle) {
        commit(uiState.copy(sessionStyle = style))
    }

    fun setSound(enabled: Boolean) {
        uiState = uiState.copy(settings = uiState.settings.copy(soundEnabled = enabled))
        viewModelScope.launch { settingsRepository.setSound(enabled) }
    }

    fun setHaptics(enabled: Boolean) {
        uiState = uiState.copy(settings = uiState.settings.copy(hapticsEnabled = enabled))
        viewModelScope.launch { settingsRepository.setHaptics(enabled) }
    }

    fun startSession(nowEpochMs: Long = System.currentTimeMillis()) {
        val clean = SessionState()
        commit(
            uiState.copy(
                session = clean,
                currentPlayerIndex = 0,
                sessionEndsAtEpochMs = nowEpochMs + uiState.durationMinutes * 60_000L,
                afterglowCard = null,
            )
        )
        drawNext(clean)
    }

    fun skipCurrent() {
        val current = uiState.currentCard ?: return
        val updated = engine.skip(uiState.session, current)
        commit(
            uiState.copy(
                session = updated,
                currentPlayerIndex = 1 - uiState.currentPlayerIndex,
            )
        )
        drawNext(updated)
    }

    fun completeCurrent(): Boolean {
        val current = uiState.currentCard ?: return false
        val updated = engine.complete(uiState.session, current)
        val reachedAfterglow = updated.heat >= 100
        commit(
            uiState.copy(
                session = updated,
                currentPlayerIndex = 1 - uiState.currentPlayerIndex,
            )
        )
        if (!reachedAfterglow) {
            drawNext(updated)
        }
        return reachedAfterglow
    }

    fun reviewConsent() {
        commit(
            uiState.copy(
                effectiveConsent = intersectConsent(
                    ConsentProfile(allowedCategories = emptySet(), maxIntensity = Intensity.SOFT),
                    ConsentProfile(allowedCategories = emptySet(), maxIntensity = Intensity.SOFT),
                ),
                consentConflict = false,
                session = SessionState(),
                currentCard = null,
                currentPlayerIndex = 0,
                sessionEndsAtEpochMs = null,
                afterglowCard = null,
                directorPhase = SessionPhase.WARMUP,
                directorDeescalated = false,
            )
        )
    }

    fun nextAfterglow() {
        commit(
            uiState.copy(
                afterglowCard = if (afterglowDeck.isEmpty()) null else afterglowDeck[random.nextInt(afterglowDeck.size)]
            )
        )
    }

    fun remainingSeconds(nowEpochMs: Long = System.currentTimeMillis()): Long {
        val endsAt = uiState.sessionEndsAtEpochMs ?: return uiState.durationMinutes * 60L
        return ((endsAt - nowEpochMs + 999L) / 1000L).coerceAtLeast(0L)
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
            sessionEndsAtEpochMs = null,
            afterglowCard = null,
            directorPhase = SessionPhase.WARMUP,
            directorDeescalated = false,
        )
        savedStateHandle.remove<SessionSnapshot>(SNAPSHOT_KEY)
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
            favoriteCategories = uiState.sessionStyle.preferredCategories,
            directorPlan = plan,
        )
        commit(
            uiState.copy(
                currentCard = card,
                directorPhase = plan.phase,
                directorDeescalated = plan.deescalated,
            )
        )
    }

    private fun commit(state: ChemiaUiState) {
        uiState = state
        savedStateHandle[SNAPSHOT_KEY] = SessionSnapshot.from(state)
    }

    companion object {
        private const val SNAPSHOT_KEY = "chemia_session_snapshot"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = checkNotNull(this[APPLICATION_KEY])
                ChemiaViewModel(
                    application = application,
                    savedStateHandle = createSavedStateHandle(),
                )
            }
        }
    }
}
