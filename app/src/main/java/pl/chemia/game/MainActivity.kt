package pl.chemia.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Obsidian = Color(0xFF08060A)
private val BlackGlass = Color(0xE6161019)
private val Burgundy = Color(0xFF5C1638)
private val Rose = Color(0xFFFF3F6C)
private val RoseSoft = Color(0xFFFF7896)
private val Gold = Color(0xFFFFD29A)
private val GoldDim = Color(0xFF9D7554)
private val Ivory = Color(0xFFFFF8F0)
private val Muted = Color(0xFFB8ABB5)
private val Divider = Color(0xFF3A2937)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ChemiaApp() }
    }
}

@Composable
private fun ChemiaApp() {
    var screen by rememberSaveable { mutableStateOf("gate") }
    var playerA by rememberSaveable { mutableStateOf("Partner 1") }
    var playerB by rememberSaveable { mutableStateOf("Partner 2") }
    var intensity by rememberSaveable { mutableIntStateOf(3) }
    var soundEnabled by rememberSaveable { mutableStateOf(true) }
    var hapticsEnabled by rememberSaveable { mutableStateOf(true) }

    MaterialTheme {
        PremiumBackground {
            Crossfade(targetState = screen, animationSpec = tween(280), label = "screen") { destination ->
                when (destination) {
                    "gate" -> AgeGateScreen(
                        onAccepted = {
                            if (soundEnabled) SoundEngine.play(SoundCue.START)
                            screen = "home"
                        }
                    )
                    "home" -> HomeScreen(onNew = { screen = "setup" })
                    "setup" -> SetupScreen(
                        playerA = playerA,
                        playerB = playerB,
                        onA = { playerA = it },
                        onB = { playerB = it },
                        intensity = intensity,
                        onIntensity = { intensity = it },
                        soundEnabled = soundEnabled,
                        onSoundEnabled = { soundEnabled = it },
                        hapticsEnabled = hapticsEnabled,
                        onHapticsEnabled = { hapticsEnabled = it },
                        onStart = {
                            if (soundEnabled) SoundEngine.play(SoundCue.START)
                            screen = "session"
                        },
                    )
                    else -> SessionScreen(
                        playerA = playerA.ifBlank { "Partner 1" },
                        playerB = playerB.ifBlank { "Partner 2" },
                        intensity = intensity,
                        soundEnabled = soundEnabled,
                        hapticsEnabled = hapticsEnabled,
                        onEnd = { screen = "home" },
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumBackground(content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF050407), Color(0xFF120812), Color(0xFF09070B))
                )
            )
    ) {
        Box(
            Modifier
                .size(280.dp)
                .align(Alignment.TopEnd)
                .blur(70.dp)
                .background(Rose.copy(alpha = 0.18f), CircleShape)
        )
        Box(
            Modifier
                .size(240.dp)
                .align(Alignment.BottomStart)
                .blur(80.dp)
                .background(Gold.copy(alpha = 0.10f), CircleShape)
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.Transparent, Obsidian.copy(alpha = 0.64f)),
                        radius = 980f
                    )
                )
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun BrandLockup(compact: Boolean = false, trailing: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                "CHEMIA",
                color = Ivory,
                fontSize = if (compact) 23.sp else 44.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
            )
            Text(
                "PRIVATE COUPLES EXPERIENCE",
                color = Gold,
                fontSize = if (compact) 9.sp else 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp,
            )
        }
        trailing?.let { PremiumChip(it, Rose) }
    }
}

@Composable
private fun PremiumChip(text: String, color: Color) {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.45f), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 0.8.sp)
    }
}

@Composable
private fun AgeGateScreen(onAccepted: () -> Unit) {
    var adultA by rememberSaveable { mutableStateOf(false) }
    var adultB by rememberSaveable { mutableStateOf(false) }
    var consent by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
    ) {
        BrandLockup()
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PremiumChip("18+", Rose)
            PremiumChip("OFFLINE", Gold)
            PremiumChip("PRIVATE", RoseSoft)
        }
        Spacer(Modifier.height(28.dp))
        GlassCard {
            Text("Tylko dla Was", color = Ivory, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(8.dp))
            Text(
                "Pikantna gra dla dwojga pełnoletnich partnerów. Bez konta, bez chmury, bez historii sesji.",
                color = Muted,
                fontSize = 15.sp,
                lineHeight = 22.sp,
            )
            Spacer(Modifier.height(20.dp))
            ConsentRow(adultA, { adultA = it }, "Partner 1 ma ukończone 18 lat")
            ConsentRow(adultB, { adultB = it }, "Partner 2 ma ukończone 18 lat")
            ConsentRow(consent, { consent = it }, "Każdą kartę można pominąć, a sesję zakończyć w dowolnym momencie")
            Spacer(Modifier.height(20.dp))
            PremiumPrimaryButton("WEJDŹ DO GRY", adultA && adultB && consent, onAccepted)
        }
    }
}

@Composable
private fun ConsentRow(checked: Boolean, onChecked: (Boolean) -> Unit, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = onChecked)
        Text(text, color = Ivory, fontSize = 14.sp, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun HomeScreen(onNew: () -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        BrandLockup()
        Spacer(Modifier.height(18.dp))
        Text("Napięcie. Chemia. Gra.", color = Ivory, fontSize = 30.sp, fontWeight = FontWeight.Black, lineHeight = 34.sp)
        Spacer(Modifier.height(8.dp))
        Text("72 karty • 4 poziomy • jedna prywatna sesja", color = Muted, fontSize = 14.sp)
        Spacer(Modifier.height(30.dp))
        GlassCard {
            Text("SPICY COUPLES EDITION", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
            Spacer(Modifier.height(10.dp))
            Text(
                "Zaczynacie lekko. To Wy decydujecie, jak daleko idzie gra.",
                color = Ivory,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 28.sp,
            )
            Spacer(Modifier.height(22.dp))
            PremiumPrimaryButton("NOWA GRA", true, onNew)
        }
        Spacer(Modifier.height(18.dp))
        Text("PRYWATNOŚĆ: sesja lokalna • bez internetu • bez konta", color = GoldDim, fontSize = 10.sp, letterSpacing = 0.6.sp)
    }
}

@Composable
private fun SetupScreen(
    playerA: String,
    playerB: String,
    onA: (String) -> Unit,
    onB: (String) -> Unit,
    intensity: Int,
    onIntensity: (Int) -> Unit,
    soundEnabled: Boolean,
    onSoundEnabled: (Boolean) -> Unit,
    hapticsEnabled: Boolean,
    onHapticsEnabled: (Boolean) -> Unit,
    onStart: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
    ) {
        BrandLockup(compact = true, trailing = "SETUP")
        Spacer(Modifier.height(22.dp))
        Text("Wasza sesja", color = Ivory, fontSize = 29.sp, fontWeight = FontWeight.Black)
        Text("Ustawcie imiona i maksymalny poziom.", color = Muted, fontSize = 14.sp)
        Spacer(Modifier.height(18.dp))

        GlassCard {
            OutlinedTextField(
                value = playerA,
                onValueChange = onA,
                label = { Text("Partner 1") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = playerB,
                onValueChange = onB,
                label = { Text("Partner 2") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(18.dp))
        Text("INTENSYWNOŚĆ", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
        Spacer(Modifier.height(8.dp))
        IntensityCard(1, "SOFT", "Flirt, rozmowa, czułość", intensity, onIntensity)
        IntensityCard(2, "SPICY", "Pocałunki i zmysłowe zadania", intensity, onIntensity)
        IntensityCard(3, "HOT", "Odważniejsze pytania i bliskość", intensity, onIntensity)
        IntensityCard(4, "EXTREME", "Najmocniejsza talia w granicach obopólnej zgody", intensity, onIntensity)

        Spacer(Modifier.height(18.dp))
        GlassCard {
            SettingToggle("Dźwięk", "Subtelne sygnały kart, wykonania i HEAT 100", soundEnabled, onSoundEnabled)
            Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(Divider))
            Spacer(Modifier.height(12.dp))
            SettingToggle("Haptyka", "Delikatna odpowiedź telefonu przy akcjach", hapticsEnabled, onHapticsEnabled)
        }

        Spacer(Modifier.height(18.dp))
        PremiumPrimaryButton("ROZPOCZNIJ SESJĘ", true, onStart)
        Spacer(Modifier.height(12.dp))
        Text(
            "POMIŃ nigdy nie ma kary. HEAT rośnie dopiero po WYKONANE.",
            color = Muted,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun IntensityCard(
    value: Int,
    label: String,
    description: String,
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    val active = selected == value
    val accent = intensityColor(value)

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp).clickable { onSelect(value) },
        colors = CardDefaults.cardColors(containerColor = if (active) accent.copy(alpha = 0.13f) else BlackGlass),
        border = BorderStroke(1.dp, if (active) accent else Divider),
        shape = RoundedCornerShape(18.dp),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(label, color = if (active) accent else Ivory, fontWeight = FontWeight.Black, fontSize = 16.sp)
                Text(description, color = Muted, fontSize = 12.sp, lineHeight = 17.sp)
            }
            Box(Modifier.size(14.dp).background(if (active) accent else Divider, CircleShape))
        }
    }
}

@Composable
private fun SettingToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, color = Ivory, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Muted, fontSize = 11.sp, lineHeight = 15.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onChecked,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Ivory,
                checkedTrackColor = Rose,
                uncheckedThumbColor = Muted,
                uncheckedTrackColor = Divider,
            ),
        )
    }
}

@Composable
private fun SessionScreen(
    playerA: String,
    playerB: String,
    intensity: Int,
    soundEnabled: Boolean,
    hapticsEnabled: Boolean,
    onEnd: () -> Unit,
) {
    val engine = remember { GameEngine() }
    val haptic = LocalHapticFeedback.current
    var state by remember { mutableStateOf(SessionState()) }
    var current by remember { mutableStateOf<GameCard?>(null) }
    var turn by rememberSaveable { mutableIntStateOf(0) }
    var cardNumber by rememberSaveable { mutableIntStateOf(0) }

    val activePlayer = if (turn % 2 == 0) playerA else playerB
    val animatedHeat by animateFloatAsState(
        targetValue = state.heat / 100f,
        animationSpec = tween(420),
        label = "heat"
    )

    Column(Modifier.fillMaxSize()) {
        BrandLockup(compact = true, trailing = "HEAT " + state.heat + "%")
        Spacer(Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = { animatedHeat },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(99.dp)),
            color = if (state.heat >= 100) Gold else Rose,
            trackColor = Divider,
        )

        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PlayerPill(playerA, activePlayer == playerA, Modifier.weight(1f))
            PlayerPill(playerB, activePlayer == playerB, Modifier.weight(1f))
        }

        Spacer(Modifier.height(14.dp))

        Box(Modifier.fillMaxWidth().weight(1f)) {
            Crossfade(targetState = current, animationSpec = tween(220), label = "card") { card ->
                GameCardPanel(
                    card = card,
                    activePlayer = activePlayer,
                    cardNumber = cardNumber,
                    heat = state.heat,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        if (current == null) {
            PremiumPrimaryButton(
                text = if (state.heat >= 100) "LOSOWANIE BEZ LIMITU" else "LOSUJ KARTĘ",
                enabled = true,
                onClick = {
                    current = engine.nextCard(spicyDeck, intensity, state.recentIds)
                    cardNumber += 1
                    if (soundEnabled) SoundEngine.play(SoundCue.DRAW)
                    if (hapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
            )
        } else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = {
                        state = engine.skip(state, current!!.id)
                        current = null
                        turn += 1
                        if (soundEnabled) SoundEngine.play(SoundCue.SKIP)
                        if (hapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, Divider),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text("POMIŃ", color = Muted)
                }

                Button(
                    onClick = {
                        val before = state.heat
                        val completed = engine.complete(state, current!!)
                        state = completed
                        current = null
                        turn += 1
                        if (soundEnabled) {
                            SoundEngine.play(
                                if (before < 100 && completed.heat >= 100) SoundCue.HEAT_MAX
                                else SoundCue.COMPLETE
                            )
                        }
                        if (hapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Rose),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text("WYKONANE", color = Color.White, fontWeight = FontWeight.Black)
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onEnd,
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, Divider),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text("ZAKOŃCZ SESJĘ", color = Muted)
        }
    }
}

@Composable
private fun PlayerPill(name: String, active: Boolean, modifier: Modifier = Modifier) {
    val color = if (active) Gold else Divider
    Box(
        modifier.clip(RoundedCornerShape(14.dp))
            .background(if (active) Gold.copy(alpha = 0.10f) else BlackGlass)
            .border(1.dp, color, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            if (active) "●  " + name else name,
            color = if (active) Gold else Muted,
            fontSize = 12.sp,
            fontWeight = if (active) FontWeight.Black else FontWeight.Medium,
        )
    }
}

@Composable
private fun GameCardPanel(
    card: GameCard?,
    activePlayer: String,
    cardNumber: Int,
    heat: Int,
    modifier: Modifier = Modifier,
) {
    val accent = card?.let { intensityColor(it.intensity) } ?: Rose
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xF0201521)),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.55f)),
        shape = RoundedCornerShape(28.dp),
    ) {
        Box(
            Modifier.fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(accent.copy(alpha = 0.08f), Color.Transparent, Gold.copy(alpha = 0.035f))
                    )
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (card == null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PremiumChip(if (heat >= 100) "ENDLESS" else "TURA " + activePlayer, if (heat >= 100) Gold else Rose)
                    Spacer(Modifier.height(18.dp))
                    Text(
                        if (heat >= 100) "HEAT 100" else "Gotowi?",
                        color = Ivory,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        if (heat >= 100) "Macie pełny ogień. Gra może trwać dalej bez limitu."
                        else "Losujesz kartę numer " + (cardNumber + 1) + ".",
                        color = Muted,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PremiumChip(card.category, accent)
                        PremiumChip(intensityLabel(card.intensity), Gold)
                    }
                    Spacer(Modifier.height(18.dp))
                    Text(
                        card.title,
                        color = Ivory,
                        fontSize = 31.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 35.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(18.dp))
                    Text(
                        card.text,
                        color = Color(0xFFF5EAF0),
                        fontSize = 20.sp,
                        lineHeight = 29.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(22.dp))
                    Text(
                        "+" + card.heat + " HEAT",
                        color = accent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun GlassCard(content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BlackGlass),
        border = BorderStroke(1.dp, Divider),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp)) {
            content()
        }
    }
}

@Composable
private fun PremiumPrimaryButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        if (enabled) Burgundy else Divider,
                        if (enabled) Rose else Divider,
                        if (enabled) Color(0xFFB12A60) else Divider,
                    )
                )
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
        ),
        shape = shape,
    ) {
        Text(
            text,
            color = if (enabled) Color.White else Muted,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(vertical = 4.dp),
        )
    }
}

private fun intensityColor(value: Int): Color = when (value) {
    1 -> Color(0xFF9ED8CB)
    2 -> Gold
    3 -> RoseSoft
    else -> Rose
}

private fun intensityLabel(value: Int): String = when (value) {
    1 -> "SOFT"
    2 -> "SPICY"
    3 -> "HOT"
    else -> "EXTREME"
}
