package com.example.appjogodamemoria.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appjogodamemoria.data.AppDatabase
import com.example.appjogodamemoria.data.local.PontuacaoEntity
import com.example.appjogodamemoria.viewmodel.JogoViewModel
import kotlinx.coroutines.delay

// Modelo para uma carta do jogo
data class CartaJogo(
    val id: Int,
    val emoji: String,
    var revelada: Boolean = false,
    var combinada: Boolean = false
)

// Função para gerar cartas embaralhadas
fun gerarCartas(): List<CartaJogo> {
    val emojis = listOf("🍎", "🍌", "🍊", "🍇", "🍓", "🥝", "🍑", "🍒")
    val cartasDuplicadas = emojis.flatMapIndexed { index, emoji ->
        listOf(
            CartaJogo(index * 2, emoji),
            CartaJogo(index * 2 + 1, emoji)
        )
    }.shuffled()
    return cartasDuplicadas
}

@Composable
fun TelaJogo(navController: NavController) {
    val context = LocalContext.current
    val fabrica = AppDatabase.getFabricaViewModel(context)
    val viewModel: JogoViewModel = viewModel(factory = fabrica)

    // Estado do jogo
    var cartas by remember { mutableStateOf(gerarCartas()) }
    var primeiraCartaSelecionada by remember { mutableStateOf<Int?>(null) }
    var segundaCartaSelecionada by remember { mutableStateOf<Int?>(null) }
    var pontuacao by remember { mutableStateOf(0) }
    var tentativas by remember { mutableStateOf(0) }
    var bloqueioCliques by remember { mutableStateOf(false) }
    var jogoCompleto by remember { mutableStateOf(false) }

    // Verificar se há match
    LaunchedEffect(segundaCartaSelecionada) {
        if (primeiraCartaSelecionada != null && segundaCartaSelecionada != null) {
            bloqueioCliques = true
            delay(1000) // Pausa para o jogador ver as cartas

            val primeira = cartas[primeiraCartaSelecionada!!]
            val segunda = cartas[segundaCartaSelecionada!!]

            val novasCartas = cartas.toMutableList()
            if (primeira.emoji == segunda.emoji) {
                // Match encontrado
                novasCartas[primeiraCartaSelecionada!!] = primeira.copy(combinada = true)
                novasCartas[segundaCartaSelecionada!!] = segunda.copy(combinada = true)
                pontuacao += 10
            } else {
                // Não foi match - virar cartas de volta
                novasCartas[primeiraCartaSelecionada!!] = primeira.copy(revelada = false)
                novasCartas[segundaCartaSelecionada!!] = segunda.copy(revelada = false)
            }
            cartas = novasCartas

            tentativas++
            primeiraCartaSelecionada = null
            segundaCartaSelecionada = null
            bloqueioCliques = false

            // Verificar se o jogo terminou
            if (cartas.all { it.combinada }) {
                jogoCompleto = true
            }
        }
    }

    // Salvar pontuação quando o jogo terminar
    LaunchedEffect(jogoCompleto) {
        if (jogoCompleto) {
            try {
                val authViewModel = AppDatabase.getAuthViewModel(context)
                val usuarioId = authViewModel.obterIdUsuario()
                val nomeUsuario = authViewModel.obterNomeUsuario()

                if (usuarioId != null) {
                    // Atualizar melhor pontuação do usuário logado
                    authViewModel.atualizarMelhorPontuacao(pontuacao)

                    // Atualizar menor número de tentativas
                    authViewModel.atualizarMenorTentativas(tentativas)

                    // Salvar também na tabela de pontuações (histórico)
                    viewModel.salvarPontuacao(nomeUsuario, pontuacao)
                }
            } catch (e: Exception) {
                // Tratar erro
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header com informações do jogo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pontos: $pontuacao",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tentativas: $tentativas",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (jogoCompleto) {
                // Tela de jogo completo
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "🎉 Parabéns! 🎉",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Pontuação: $pontuacao",
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Tentativas: $tentativas",
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = {
                                // Reiniciar jogo
                                cartas = gerarCartas()
                                pontuacao = 0
                                tentativas = 0
                                jogoCompleto = false
                                primeiraCartaSelecionada = null
                                segundaCartaSelecionada = null
                                bloqueioCliques = false
                            }
                        ) {
                            Text("Jogar Novamente")
                        }

                        Button(
                            onClick = { navController.popBackStack() }
                        ) {
                            Text("Menu")
                        }
                    }
                }
            } else {
                // Grid de cartas
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(cartas) { index, carta ->
                        CartaItem(
                            carta = carta,
                            onClick = {
                                if (!bloqueioCliques && !carta.revelada && !carta.combinada) {
                                    val novasCartas = cartas.toMutableList()
                                    novasCartas[index] = carta.copy(revelada = true)
                                    cartas = novasCartas

                                    when {
                                        primeiraCartaSelecionada == null -> {
                                            primeiraCartaSelecionada = index
                                        }
                                        segundaCartaSelecionada == null && index != primeiraCartaSelecionada -> {
                                            segundaCartaSelecionada = index
                                        }
                                    }
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Voltar ao Menu")
                }
            }
        }
    }
}

@Composable
fun CartaItem(
    carta: CartaJogo,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .background(
                color = when {
                    carta.combinada -> Color(0xFF4CAF50)  // Verde para combinadas
                    carta.revelada -> Color(0xFF2196F3)   // Azul para reveladas
                    else -> Color(0xFF9E9E9E)             // Cinza para viradas
                },
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (carta.revelada || carta.combinada) {
            Text(
                text = carta.emoji,
                fontSize = 32.sp,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = "?",
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}