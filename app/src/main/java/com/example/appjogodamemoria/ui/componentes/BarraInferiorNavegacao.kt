package com.example.appjogodamemoria.ui.componentes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.appjogodamemoria.data.AppDatabase

@Composable
fun BarraInferiorNavegacao(navController: NavController) {
    val context = LocalContext.current
    val authViewModel = AppDatabase.getAuthViewModel(context)
    val uiState by authViewModel.uiState.collectAsState()

    val usuario = uiState.usuarioLogado
    val ehAdmin = usuario?.ehAdmin == true

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Menu") },
            label = { Text("Menu") },
            selected = false,
            onClick = { navController.navigate("menu") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Jogar") },
            label = { Text("Jogo") },
            selected = false,
            onClick = { navController.navigate("jogo") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Star, contentDescription = "Ranking") },
            label = { Text("Ranking") },
            selected = false,
            onClick = { navController.navigate("ranking") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            selected = false,
            onClick = { navController.navigate("perfil") }
        )

        // Aba de Administração - apenas para administradores
        if (ehAdmin) {
            NavigationBarItem(
                icon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Gerenciamento de Usuários"
                    )
                },
                label = { Text("Admin") },
                selected = false,
                onClick = { navController.navigate("gerenciamento") }
            )
        }
    }
}