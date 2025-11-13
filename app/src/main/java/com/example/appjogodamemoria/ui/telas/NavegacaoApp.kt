package com.example.appjogodamemoria.ui.telas

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appjogodamemoria.data.AppDatabase
import com.example.appjogodamemoria.ui.componentes.BarraInferiorNavegacao
import androidx.compose.runtime.LaunchedEffect

@Composable
fun NavegacaoApp() {
    val navController: NavHostController = rememberNavController()
    val context = LocalContext.current
    val authViewModel = AppDatabase.getAuthViewModel(context)
    val authState by authViewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            val currentRoute = navController.currentDestination?.route

            // Mostrar barra em todas as telas principais (exceto login/cadastro)
            when (currentRoute) {
                "login", "cadastro" -> {
                    // Não mostra barra nas telas de autenticação
                }
                else -> {
                    BarraInferiorNavegacao(navController)
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") { TelaLogin(navController) }
            composable("cadastro") { TelaCadastro(navController) }
            composable("menu") { TelaMenuPrincipal(navController) }
            composable("jogo") { TelaJogo(navController) }
            composable("ranking") { TelaRanking(navController) }
            composable("perfil") { TelaPerfil(navController) }
            composable("gerenciamento") {
                // Verificar se é admin antes de mostrar a tela
                if (authState.usuarioLogado?.ehAdmin == true) {
                    TelaGerenciamento(navController)
                } else {
                    // Redirecionar para menu se não for admin
                    LaunchedEffect(Unit) {
                        navController.navigate("menu") {
                            popUpTo("gerenciamento") { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}