package com.example.appjogodamemoria.ui.telas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appjogodamemoria.data.AppDatabase
import com.example.appjogodamemoria.data.local.UsuarioEntity

@Composable
fun TelaRanking(navController: NavController) {
    val context = LocalContext.current
    val repositorioUsuario = AppDatabase.getRepositorioUsuario(context)
    var ranking by remember { mutableStateOf(listOf<UsuarioEntity>()) }

    // Carregar ranking dos usuários
    LaunchedEffect(Unit) {
        repositorioUsuario.obterRanking().collect { usuarios ->
            ranking = usuarios
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
            Text(
                text = "Ranking 🏆",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Menor número de tentativas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (ranking.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nenhum jogo concluído ainda",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Complete uma partida para aparecer no ranking!",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ranking) { usuario ->
                        RankingCard(
                            posicao = ranking.indexOf(usuario) + 1,
                            usuario = usuario
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Voltar
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Voltar ao Menu")
            }
        }
    }
}

@Composable
fun RankingCard(
    posicao: Int,
    usuario: UsuarioEntity
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (posicao <= 3) 8.dp else 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = when (posicao) {
                1 -> MaterialTheme.colorScheme.tertiaryContainer // Ouro
                2 -> MaterialTheme.colorScheme.secondaryContainer // Prata
                3 -> MaterialTheme.colorScheme.primaryContainer // Bronze
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Medalha ou posição
                Box(
                    modifier = Modifier
                        .size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (posicao) {
                            1 -> "🥇"
                            2 -> "🥈"
                            3 -> "🥉"
                            else -> "#$posicao"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = usuario.nome,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (usuario.ehAdmin) {
                        Text(
                            text = "👑 Administrador",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Pontuação
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = if (usuario.menorTentativas == Int.MAX_VALUE) "N/A" else "${usuario.menorTentativas}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "tentativas",
                    style = MaterialTheme.typography.bodySmall
                )
                // Mostrar também a pontuação como informação secundária
                if (usuario.melhorPontuacao > 0) {
                    Text(
                        text = "${usuario.melhorPontuacao} pts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}