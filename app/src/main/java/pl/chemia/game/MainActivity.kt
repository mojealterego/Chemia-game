package pl.chemia.game

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF08060A)
private val Panel = Color(0xFF171019)
private val Panel2 = Color(0xFF241724)
private val Accent = Color(0xFFFF3F6C)
private val Accent2 = Color(0xFFFFA45C)

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

    MaterialTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(Bg)
                .padding(20.dp)
        ) {
            when (screen) {
                "gate" -> AgeGateScreen(
                    onAccepted = { screen = "home" }
                )
                "home" -> HomeScreen(
                    onNew = { screen = "setup" }
                )
                "setup" -> SetupScreen(
                    playerA = playerA,
                    playerB = playerB,
                    onA = { playerA = it },
                    onB = { playerB = it },
                    intensity = intensity,
                    onIntensity = { intensity = it },
                    onStart = { screen = "session" },
                )
                else -> SessionScreen(
                    playerA = playerA.ifBlank { "Partner 1" },
                    playerB = playerB.ifBlank { "Partner 2" },
                    intensity = intensity,
                    onEnd = { screen = "home" },
                )
            }
        }
    }
}

@Composable
private fun AgeGateScreen(onAccepted: () -> Unit) {
    var adultA by rememberSaveable { mutableStateOf(false) }
    var adultB by rememberSaveable { mutableStateOf(false) }
    var consent by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "CHEMIA",
            color = Color.White,
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
        )
        Text("18+ • gra dla dwojga", color = Accent2, fontSize = 16.sp)

        Spacer(Modifier.height(28.dp))

        Text(
            "Ta wersja zawiera pikantne pytania i zadania dla dwojga pełnoletnich, dobrowolnie grających partnerów.",
            color = Color.LightGray,
            fontSize = 15.sp,
        )

        Spacer(Modifier.height(18.dp))

        ConsentRow(
            checked = adultA,
            onChecked = { adultA = it },
            text = "Partner 1 potwierdza ukończenie 18 lat",
        )
        ConsentRow(
            checked = adultB,
            onChecked = { adultB = it },
            text = "Partner 2 potwierdza ukończenie 18 lat",
        )
        ConsentRow(
            checked = consent,
            onChecked = { consent = it },
            text = "Oboje rozumiemy, że każdą kartę można pominąć i zakończyć grę w dowolnym momencie",
        )

        Spacer(Modifier.height(22.dp))

        Button(
            onClick = onAccepted,
            enabled = adultA && adultB && consent,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
            shape = RoundedCornerShape(18.dp),
        ) {
            Text("WEJDŹ DO GRY")
        }
    }
}

@Composable
private fun ConsentRow(
    checked: Boolean,
    onChecked: (Boolean) -> Unit,
    text: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = onChecked)
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun HomeScreen(onNew: () -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("CHEMIA", color = Color.White, fontSize = 44.sp, fontWeight = FontWeight.Black)
        Text("SPICY COUPLES EDITION", color = Accent2, fontSize = 15.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(34.dp))

        Button(
            onClick = onNew,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
            shape = RoundedCornerShape(18.dp),
        ) {
            Text("NOWA GRA")
        }

        Spacer(Modifier.height(18.dp))

        Text(
            "72 karty • 4 poziomy • bez konta • offline",
            color = Color.Gray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
        )
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
    onStart: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("PRZYGOTOWANIE", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(18.dp))

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

        Spacer(Modifier.height(22.dp))
        Text("Poziom intensywności", color = Color.LightGray)

        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            IntensityButton(1, "SOFT", "flirt, rozmowa, czułość", intensity, onIntensity)
            IntensityButton(2, "SPICY", "pocałunki, zmysłowe zadania", intensity, onIntensity)
            IntensityButton(3, "HOT", "odważniejsze pytania i bliskość", intensity, onIntensity)
            IntensityButton(4, "EXTREME", "najmocniejsza talia, nadal tylko za obopólną zgodą", intensity, onIntensity)
        }

        Spacer(Modifier.height(18.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Panel),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                "Zasada gry: POMIŃ nie ma kary. HEAT rośnie wyłącznie po naciśnięciu WYKONANE. Nic z sesji nie jest wysyłane ani zapisywane na serwerze.",
                color = Color.LightGray,
                fontSize = 13.sp,
                modifier = Modifier.padding(14.dp),
            )
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
            shape = RoundedCornerShape(18.dp),
        ) {
            Text("ROZPOCZNIJ")
        }
    }
}

@Composable
private fun IntensityButton(
    value: Int,
    label: String,
    description: String,
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    Button(
        onClick = { onSelect(value) },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected == value) Accent else Panel,
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(label, fontWeight = FontWeight.Black)
            Text(description, fontSize = 11.sp)
        }
    }
}

@Composable
private fun SessionScreen(
    playerA: String,
    playerB: String,
    intensity: Int,
    onEnd: () -> Unit,
) {
    val engine = remember { GameEngine() }
    var state by remember { mutableStateOf(SessionState()) }
    var current by remember { mutableStateOf<GameCard?>(null) }
    var turn by rememberSaveable { mutableIntStateOf(0) }
    var cardNumber by rememberSaveable { mutableIntStateOf(0) }

    val activePlayer = if (turn % 2 == 0) playerA else playerB

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("CHEMIA", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text("teraz: $activePlayer", color = Color.Gray, fontSize = 12.sp)
            }
            Text("HEAT ${state.heat}%", color = Accent, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Panel),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("$playerA × $playerB", color = Color.LightGray, fontSize = 12.sp)
                Text("KARTA $cardNumber", color = Accent2, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Panel2),
            shape = RoundedCornerShape(26.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                val card = current
                if (card == null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            if (state.heat >= 100) "HEAT 100" else "Gotowi?",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            if (state.heat >= 100) "Możecie grać dalej bez limitu." else "Wylosuj kartę dla: $activePlayer",
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "${card.category} • ${intensityLabel(card.intensity)}",
                            color = Accent2,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        )
                        Spacer(Modifier.height(14.dp))
                        Text(
                            card.title,
                            color = Color.White,
                            fontSize = 29.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(18.dp))
                        Text(
                            card.text,
                            color = Color.White,
                            fontSize = 21.sp,
                            lineHeight = 29.sp,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(18.dp))
                        Text("+${card.heat} HEAT po wykonaniu", color = Accent, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        if (current == null) {
            Button(
                onClick = {
                    current = engine.nextCard(
                        deck = spicyDeck,
                        maxIntensity = intensity,
                        recentIds = state.recentIds,
                    )
                    cardNumber += 1
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Accent),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text("LOSUJ KARTĘ")
            }
        } else {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        state = engine.skip(state, current!!.id)
                        current = null
                        turn += 1
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("POMIŃ")
                }

                Button(
                    onClick = {
                        state = engine.complete(state, current!!)
                        current = null
                        turn += 1
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text("WYKONANE")
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onEnd,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("ZAKOŃCZ SESJĘ")
        }
    }
}

private fun intensityLabel(value: Int): String = when (value) {
    1 -> "SOFT"
    2 -> "SPICY"
    3 -> "HOT"
    else -> "EXTREME"
}
