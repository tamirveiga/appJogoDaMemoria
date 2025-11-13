package com.example.appjogodamemoria.ui.telas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.example.appjogodamemoria.data.local.UsuarioEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaGerenciamento(navController: NavController) {
    val context = LocalContext.current
    val adminViewModel = AppDatabase.getAdminUsuarioViewModel(context)
    val authViewModel = AppDatabase.getAuthViewModel(context)
    val uiState by adminViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    // Verificar se o usuário é realmente admin
    val ehAdmin = authState.usuarioLogado?.ehAdmin ?: false

    // Se não for admin, redirecionar para o menu
    LaunchedEffect(ehAdmin) {
        if (!ehAdmin || !authState.estaLogado) {
            navController.navigate("menu") {
                popUpTo("gerenciamento") { inclusive = true }
            }
        }
    }

    // Se não for admin, mostrar tela vazia (enquanto redireciona)
    if (!ehAdmin) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Cabeçalho simples
            Text(
                text = "Gerenciar Usuários",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = { adminViewModel.mostrarDialogNovoUsuario() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar usuário")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de usuários
            if (uiState.carregando) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.usuarios.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum usuário encontrado",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.usuarios) { usuario ->
                        CardUsuarioGerenciamento(
                            usuario = usuario,
                            onEdit = { adminViewModel.selecionarUsuarioParaEdicao(usuario) },
                            onDelete = { adminViewModel.deletarUsuario(usuario.id) }
                        )
                    }
                }
            }
        }
    }

    // Dialog para criar/editar usuário
    if (uiState.mostrandoDialog) {
        DialogUsuarioGerenciamento(
            usuario = uiState.usuarioEditando,
            onDismiss = { adminViewModel.fecharDialog() },
            onSave = { nome, email, senha, ehAdmin, ativo ->
                if (uiState.usuarioEditando != null) {
                    adminViewModel.atualizarUsuario(
                        id = uiState.usuarioEditando!!.id,
                        nome = nome,
                        email = email,
                        ehAdmin = ehAdmin,
                        ativo = ativo
                    )
                } else {
                    adminViewModel.cadastrarUsuario(nome, email, senha, ehAdmin)
                }
            },
            carregando = uiState.carregando,
            erro = uiState.erro
        )
    }

    // Snackbar para mensagens
    uiState.erro?.let { erro ->
        LaunchedEffect(erro) {
            // Mostrar snackbar com erro
        }
    }

    uiState.sucessoOperacao?.let { sucesso ->
        LaunchedEffect(sucesso) {
            // Mostrar snackbar com sucesso
            adminViewModel.limparMensagens()
        }
    }
}

@Composable
fun CardUsuarioGerenciamento(
    usuario: UsuarioEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = usuario.nome,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = usuario.email,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Row {
                        if (usuario.ehAdmin) {
                            AssistChip(
                                onClick = { },
                                label = { Text("Admin") },
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                        if (!usuario.ativo) {
                            AssistChip(
                                onClick = { },
                                label = { Text("Inativo") },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            )
                        }
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Deletar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Estatísticas do usuário
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Melhor pontuação: ${usuario.melhorPontuacao}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Menor tentativas: ${if (usuario.menorTentativas == Int.MAX_VALUE) "N/A" else usuario.menorTentativas}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Criado: ${dateFormat.format(Date(usuario.dataCriacao))}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (usuario.ultimoLogin > 0) {
                        Text(
                            text = "Último login: ${dateFormat.format(Date(usuario.ultimoLogin))}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogUsuarioGerenciamento(
    usuario: UsuarioEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Boolean, Boolean) -> Unit,
    carregando: Boolean,
    erro: String?
) {
    var nome by remember { mutableStateOf(TextFieldValue(usuario?.nome ?: "")) }
    var email by remember { mutableStateOf(TextFieldValue(usuario?.email ?: "")) }
    var senha by remember { mutableStateOf(TextFieldValue("")) }
    var ehAdmin by remember { mutableStateOf(usuario?.ehAdmin ?: false) }
    var ativo by remember { mutableStateOf(usuario?.ativo ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (usuario == null) "Novo Usuário" else "Editar Usuário")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !carregando
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    enabled = !carregando
                )

                if (usuario == null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = senha,
                        onValueChange = { senha = it },
                        label = { Text("Senha") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        enabled = !carregando
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = ehAdmin,
                            onCheckedChange = { ehAdmin = it },
                            enabled = !carregando
                        )
                        Text("Administrador")
                    }

                    if (usuario != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = ativo,
                                onCheckedChange = { ativo = it },
                                enabled = !carregando
                            )
                            Text("Ativo")
                        }
                    }
                }

                erro?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        nome.text.trim(),
                        email.text.trim(),
                        senha.text,
                        ehAdmin,
                        ativo
                    )
                },
                enabled = !carregando
            ) {
                if (carregando) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Text("Salvar")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !carregando
            ) {
                Text("Cancelar")
            }
        }
    )
}