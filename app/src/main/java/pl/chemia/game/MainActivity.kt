package pl.chemia.game

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

private val Bg = Color(0xFF09070B)
private val Panel = Color(0xFF17121B)
private val Accent = Color(0xFFFF5475)
private val Accent2 = Color(0xFFFFA76B)

private data class CardData(
    val category: String,
    val intensity: Int,
    val heat: Int,
    val text: String,
)

private val starterDeck = listOf(
    CardData("CONNECTION", 1, 4, "Powiedz partnerowi jedną rzecz, którą w nim najbardziej cenisz, patrząc mu prosto w oczy."),
    CardData("TEASE", 2, 7, "Przez 20 sekund utrzymuj kontakt wzrokowy. Partner wybiera: uśmiech, komplement albo kolejny rzut."),
    CardData("DESIRE", 2, 8, "Opowiedz o swojej najbardziej kuszącej fantazji w sposób, który pozostawia miejsce na wyobraźnię."),
    CardData("ROLEPLAY", 3, 10, "Wymyślcie w 30 sekund dwie role i rozpocznijcie krótką scenę flirtu."),
    CardData("HEAT", 3, 11, "Wybierz jedną z trzech atmosfer: powolna, prowokująca albo tajemnicza. Partner interpretuje wybór."),
    CardData("CONTROL", 4, 12, "Partner wybiera: pytanie, wyzwanie albo oddanie następnego ruchu Tobie."),
    CardData("CHAOS", 4, 13, "Następna karta ma podwójny Heat. Nie możesz wybrać kategorii."),
    CardData("AFTERGLOW", 2, 6, "Zwolnij na chwilę. Powiedzcie sobie po jednej rzeczy, która buduje między Wami napięcie."),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ChemiaApp(this) }
    }
}

@androidx.compose.runtime.Composable
private fun ChemiaApp(context: Context) {
    var screen by remember { mutableStateOf("home") }
    var intensity by remember { mutableStateOf(3) }
    var heat by remember { mutableStateOf(0) }
    var chain by remember { mutableStateOf(1) }
    var draws by remember { mutableStateOf(0) }
    var current by remember { mutableStateOf<CardData?>(null) }
    var history by remember { mutableStateOf(emptyList<String>()) }
    var playerA by remember { mutableStateOf("Gracz 1") }
    var playerB by remember { mutableStateOf("Gracz 2") }

    MaterialTheme {
        Box(Modifier.fillMaxSize().background(Bg).padding(20.dp)) {
            when (screen) {
                "home" -> HomeScreen({ screen = "setup" }, { screen = "session" })
                "setup" -> SetupScreen(
                    playerA, playerB, { playerA = it }, { playerB = it }, intensity,
                    { intensity = it }, { heat = 0; chain = 1; draws = 0; history = emptyList(); current = null; screen = "session" }
                )
                else -> SessionScreen(
                    playerA, playerB, intensity, heat, chain, draws, current,
                    onDraw = {
                        val eligible = starterDeck.filter { it.intensity <= intensity }.filter { c -> history.takeLast(4).none { it.startsWith(c.category + "|") } }
                            .ifEmpty { starterDeck.filter { it.intensity <= intensity } }
                        val card = eligible.random(Random)
                        current = card
                        heat = (heat + card.heat).coerceAtMost(100)
                        draws += 1
                        history = (history + (card.category + "|" + card.text)).takeLast(20)
                        chain = (chain + 1).coerceAtMost(4)
                        context.getSharedPreferences("chemia", Context.MODE_PRIVATE).edit().putInt("heat", heat).apply()
                    },
                    onSkip = { current = null },
                    onEnd = { screen = "home" }
                )
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun HomeScreen(onNew: () -> Unit, onQuick: () -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("CHEMIA", color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Black)
        Text("gra dla dwojga", color = Accent2, fontSize = 16.sp)
        Spacer(Modifier.height(34.dp))
        Button(onClick = onNew, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Accent), shape = RoundedCornerShape(18.dp)) { Text("NOWA GRA") }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onQuick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) { Text("SZYBKA GRA", color = Color.White) }
        Spacer(Modifier.height(20.dp))
        Text("Dorośli • dobrowolnie • pomiń dowolną kartę", color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center)
    }
}

@androidx.compose.runtime.Composable
private fun SetupScreen(
    playerA: String, playerB: String, onA: (String) -> Unit, onB: (String) -> Unit, intensity: Int,
    onIntensity: (Int) -> Unit, onStart: () -> Unit
) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Text("PRZYGOTOWANIE", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(18.dp))
        Text("Poziom intensywności", color = Color.LightGray)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(1 to "SOFT", 2 to "SPICY", 3 to "HOT", 4 to "EXTREME").forEach { (v, label) ->
                Button(onClick = { onIntensity(v) }, colors = ButtonDefaults.buttonColors(containerColor = if (intensity == v) Accent else Panel), modifier = Modifier.weight(1f)) { Text(label, fontSize = 10.sp) }
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("Podstawowy zestaw zawiera sugestywne zadania, bez graficznych instrukcji seksualnych.", color = Color.Gray, fontSize = 12.sp)
        Spacer(Modifier.height(20.dp))
        Button(onClick = onStart, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Accent), shape = RoundedCornerShape(18.dp)) { Text("ROZPOCZNIJ") }
    }
}

@androidx.compose.runtime.Composable
private fun SessionScreen(
    playerA: String, playerB: String, intensity: Int, heat: Int, chain: Int, draws: Int, current: CardData?,
    onDraw: () -> Unit, onSkip: () -> Unit, onEnd: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("CHEMIA", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text("HEAT $heat%", color = Accent, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text("$playerA  ×  $playerB", color = Color.Gray, fontSize = 12.sp)
                Text("CHAIN ×$chain   •   KARTA $draws", color = Accent2, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(20.dp))
        if (heat >= 100) {
            Text("∞ ENDLESS / AFTERGLOW", color = Accent2, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF211923)), shape = RoundedCornerShape(26.dp), modifier = Modifier.fillMaxWidth().weight(1f)) {
            Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                if (current == null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Gotowi?", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        Text("Wylosuj kolejną kartę.", color = Color.Gray)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(current.category, color = Accent2, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(current.text, color = Color.White, fontSize = 23.sp, lineHeight = 31.sp, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(18.dp))
                        Text("+${current.heat} HEAT", color = Accent, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onSkip, modifier = Modifier.weight(1f)) { Text("POMIŃ") }
            Button(onClick = onDraw, modifier = Modifier.weight(2f), colors = ButtonDefaults.buttonColors(containerColor = Accent), shape = RoundedCornerShape(16.dp)) { Text("🎲  LOSUJ") }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onEnd, modifier = Modifier.fillMaxWidth()) { Text("ZAKOŃCZ SESJĘ") }
    }
}
