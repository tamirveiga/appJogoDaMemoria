package com.example.appjogodamemoria.ui.telas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appjogodamemoria.data.AppDatabase

@Composable
fun TelaLogin(navController: NavController) {
    val context = LocalContext.current
    val authViewModel = AppDatabase.getAuthViewModel(context)
    val uiState by authViewModel.uiState.collectAsState()

    var email by remember { mutableStateOf(TextFieldValue("")) }
    var senha by remember { mutableStateOf(TextFieldValue("")) }

    // Navegar após login bem-sucedido
    LaunchedEffect(uiState.estaLogado) {
        if (uiState.estaLogado) {
            navController.navigate("menu") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    // Limpar erro quando o usuário começar a digitar
    LaunchedEffect(email.text, senha.text) {
        if (uiState.erro != null) {
            authViewModel.limparErro()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bem-vindo ao Jogo da Memória ",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Campo Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = !uiState.carregando
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Senha
            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                enabled = !uiState.carregando
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botão Login
            Button(
                onClick = {
                    authViewModel.login(
                        email = email.text.trim(),
                        senha = senha.text
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.carregando
            ) {
                if (uiState.carregando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Entrar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Cadastrar
            TextButton(
                onClick = { navController.navigate("cadastro") },
                enabled = !uiState.carregando
            ) {
                Text("Ainda não tem conta? Cadastre-se")
            }

            // Divider com texto
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = " Admin ",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Botão de acesso admin rápido (para desenvolvimento)
            OutlinedButton(
                onClick = {
                    email = TextFieldValue("admin@admin.com")
                    senha = TextFieldValue("123456")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.carregando
            ) {
                Text("Acesso Admin (Desenvolvimento)")
            }

            // Mostrar erro se houver
            uiState.erro?.let { erro ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = erro,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}