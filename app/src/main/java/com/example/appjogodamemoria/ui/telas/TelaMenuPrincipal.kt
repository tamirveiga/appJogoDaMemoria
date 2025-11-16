package com.example.appjogodamemoria.ui.telas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appjogodamemoria.data.AppDatabase

@Composable
fun TelaMenuPrincipal(navController: NavController) {
    val context = LocalContext.current
    val authViewModel = AppDatabase.getAuthViewModel(context)
    val uiState by authViewModel.uiState.collectAsState()
    val nomeUsuario = uiState.usuarioLogado?.nome ?: "Usuário"

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Header com nome do usuário
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Olá, $nomeUsuario. 👋",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (uiState.usuarioLogado?.ehAdmin == true) {
                            Text(
                                text = "👑 Administrador",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Botão Sair
                    TextButton(
                        onClick = {
                            authViewModel.logout()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    ) {
                        Text("Sair")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Jogo da Memória 🍎", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate("jogo") },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Jogar")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController.navigate("ranking") },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Ver Ranking")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController.navigate("perfil") },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Meu Perfil")
            }
        }
    }
}