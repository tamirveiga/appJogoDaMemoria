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
fun TelaCadastro(navController: NavController) {
    val context = LocalContext.current
    val authViewModel = AppDatabase.getAuthViewModel(context)
    val uiState by authViewModel.uiState.collectAsState()

    var nome by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var senha by remember { mutableStateOf(TextFieldValue("")) }
    var confirmarSenha by remember { mutableStateOf(TextFieldValue("")) }

    // Navegar para menu após cadastro bem-sucedido!
    LaunchedEffect(uiState.estaLogado) {
        if (uiState.estaLogado && uiState.sucessoCadastro) {
            authViewModel.limparSucessoCadastro()
            navController.navigate("menu") {
                popUpTo("login") { inclusive = true }
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
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Criar Conta 📝",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Campo Nome
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !uiState.carregando
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                label = { Text("Senha (mín. 6 caracteres)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                enabled = !uiState.carregando
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Confirmar Senha
            OutlinedTextField(
                value = confirmarSenha,
                onValueChange = { confirmarSenha = it },
                label = { Text("Confirmar senha") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                enabled = !uiState.carregando
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botão Cadastrar
            Button(
                onClick = {
                    authViewModel.cadastrar(
                        nome = nome.text.trim(),
                        email = email.text.trim(),
                        senha = senha.text,
                        confirmarSenha = confirmarSenha.text
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
                Text("Criar Conta")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Voltar ao Login
            TextButton(
                onClick = { navController.popBackStack() },
                enabled = !uiState.carregando
            ) {
                Text("Já tem conta? Faça login!")
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