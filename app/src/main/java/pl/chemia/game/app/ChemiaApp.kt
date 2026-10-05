package pl.chemia.game.app

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.chemia.game.SoundCue
import pl.chemia.game.SoundEngine
import pl.chemia.game.engine.SessionInsights
import pl.chemia.game.ui.afterglow.AfterglowScreen
import pl.chemia.game.ui.age.AgeGateScreen
import pl.chemia.game.ui.common.PremiumBackground
import pl.chemia.game.ui.consent.ConsentScreen
import pl.chemia.game.ui.home.HomeScreen
import pl.chemia.game.ui.pass.PassDeviceScreen
import pl.chemia.game.ui.session.SessionScreen
import pl.chemia.game.ui.setup.SetupScreen
import pl.chemia.game.ui.theme.ChemiaTheme

private object Route {
    const val Age = "age"
    const val Home = "home"
    const val ConsentA = "consent_a"
    const val Pass = "pass"
    const val ConsentB = "consent_b"
    const val Setup = "setup"
    const val Session = "session"
    const val Afterglow = "afterglow"
}

@Composable
fun ChemiaApp(vm: ChemiaViewModel = viewModel(factory = ChemiaViewModel.Factory)) {
    val navController = rememberNavController()
    val state = vm.uiState

    ChemiaTheme(darkTheme = true) {
        PremiumBackground(reducedMotion = state.settings.reducedMotion) {
            NavHost(navController = navController, startDestination = Route.Age) {
                composable(Route.Age) {
                    AgeGateScreen(
                        onAccepted = {
                            if (state.settings.soundEnabled) SoundEngine.play(SoundCue.CONSENT)
                            navController.navigate(Route.Home) {
                                popUpTo(Route.Age) { inclusive = true }
                            }
                        }
                    )
                }
                composable(Route.Home) {
                    HomeScreen(
                        onNewGame = {
                            vm.newGame()
                            navController.navigate(Route.ConsentA)
                        }
                    )
                }
                composable(Route.ConsentA) {
                    ConsentScreen(
                        playerLabel = state.playerA,
                        profile = state.consentA,
                        maxSelectableIntensity = vm.maxSelectableIntensity,
                        onChange = vm::updateConsentA,
                        onComplete = { navController.navigate(Route.Pass) },
                    )
                }
                composable(Route.Pass) {
                    PassDeviceScreen(onReady = { navController.navigate(Route.ConsentB) })
                }
                composable(Route.ConsentB) {
                    ConsentScreen(
                        playerLabel = state.playerB,
                        profile = state.consentB,
                        maxSelectableIntensity = vm.maxSelectableIntensity,
                        conflict = state.consentConflict,
                        onChange = vm::updateConsentB,
                        onComplete = {
                            if (vm.finalizeConsent()) {
                                if (state.settings.soundEnabled) SoundEngine.play(SoundCue.CONSENT)
                                navController.navigate(Route.Setup)
                            }
                        },
                    )
                }
                composable(Route.Setup) {
                    SetupScreen(
                        playerA = state.playerA,
                        playerB = state.playerB,
                        durationMinutes = state.durationMinutes,
                        sessionStyle = state.sessionStyle,
                        soundEnabled = state.settings.soundEnabled,
                        hapticsEnabled = state.settings.hapticsEnabled,
                        reducedMotion = state.settings.reducedMotion,
                        onPlayerA = vm::setPlayerA,
                        onPlayerB = vm::setPlayerB,
                        onDuration = vm::setDuration,
                        onSessionStyle = vm::setSessionStyle,
                        onSound = vm::setSound,
                        onHaptics = vm::setHaptics,
                        onReducedMotion = vm::setReducedMotion,
                        onStart = {
                            vm.startSession()
                            if (state.settings.soundEnabled) SoundEngine.play(SoundCue.START)
                            navController.navigate(Route.Session)
                        },
                    )
                }
                composable(Route.Session) {
                    val activePlayer = if (state.currentPlayerIndex == 0) {
                        state.playerA.ifBlank { "Partner 1" }
                    } else {
                        state.playerB.ifBlank { "Partner 2" }
                    }
                    SessionScreen(
                        activePlayer = activePlayer,
                        card = state.currentCard,
                        heat = state.session.heat,
                        chain = state.session.chain,
                        phase = state.directorPhase,
                        directorDeescalated = state.directorDeescalated,
                        soundEnabled = state.settings.soundEnabled,
                        hapticsEnabled = state.settings.hapticsEnabled,
                        reducedMotion = state.settings.reducedMotion,
                        remainingSecondsProvider = vm::remainingSeconds,
                        onSkip = vm::skipCurrent,
                        onDone = vm::completeCurrent,
                        onReviewConsent = {
                            vm.reviewConsent()
                            navController.navigate(Route.ConsentA) {
                                popUpTo(Route.Session) { inclusive = true }
                            }
                        },
                        onEnd = {
                            vm.clearSensitiveSession()
                            goHome(navController)
                        },
                        onAfterglow = {
                            if (state.settings.soundEnabled) SoundEngine.play(SoundCue.AFTERGLOW)
                            vm.nextAfterglow()
                            navController.navigate(Route.Afterglow) {
                                popUpTo(Route.Session) { inclusive = true }
                            }
                        },
                    )
                }
                composable(Route.Afterglow) {
                    val insights = SessionInsights.from(state.session)
                    AfterglowScreen(
                        card = state.afterglowCard,
                        completedCount = state.session.completedCount,
                        skippedCount = state.session.skippedCount,
                        finalHeat = state.session.heat,
                        completionRate = insights.completionRate,
                        varietyCount = insights.varietyCount,
                        onAnother = vm::nextAfterglow,
                        onFinish = {
                            vm.clearSensitiveSession()
                            goHome(navController)
                        },
                    )
                }
            }
        }
    }
}

private fun goHome(navController: NavHostController) {
    navController.navigate(Route.Home) {
        popUpTo(Route.Home) { inclusive = true }
        launchSingleTop = true
    }
}
