package com.example.appjogodamemoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appjogodamemoria.data.local.UsuarioEntity
import com.example.appjogodamemoria.data.repository.RepositorioUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class AdminUsuarioUiState(
    val usuarios: List<UsuarioEntity> = emptyList(),
    val usuarioEditando: UsuarioEntity? = null,
    val carregando: Boolean = false,
    val erro: String? = null,
    val sucessoOperacao: String? = null,
    val mostrandoDialog: Boolean = false
)

class AdminUsuarioViewModel(
    private val repositorioUsuario: RepositorioUsuario
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUsuarioUiState())
    val uiState: StateFlow<AdminUsuarioUiState> = _uiState

    init {
        carregarUsuarios()
    }

    // 🔹 Carregar lista de usuários
    private fun carregarUsuarios() {
        viewModelScope.launch {
            repositorioUsuario.listarUsuarios().collectLatest { usuarios ->
                _uiState.value = _uiState.value.copy(usuarios = usuarios)
            }
        }
    }

    // 🔹 Cadastrar novo usuário (pelo admin)
    fun cadastrarUsuario(
        nome: String,
        email: String,
        senha: String,
        ehAdmin: Boolean = false
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(carregando = true, erro = null)

            // Validações
            when {
                nome.isBlank() -> {
                    _uiState.value = _uiState.value.copy(
                        carregando = false,
                        erro = "Nome é obrigatório"
                    )
                    return@launch
                }

                email.isBlank() || !email.contains("@") -> {
                    _uiState.value = _uiState.value.copy(
                        carregando = false,
                        erro = "Email válido é obrigatório"
                    )
                    return@launch
                }

                senha.length < 6 -> {
                    _uiState.value = _uiState.value.copy(
                        carregando = false,
                        erro = "Senha deve ter pelo menos 6 caracteres"
                    )
                    return@launch
                }
            }

            val resultado = repositorioUsuario.cadastrarUsuario(nome, email, senha, ehAdmin)

            resultado.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        carregando = false,
                        erro = null,
                        sucessoOperacao = "Usuário cadastrado com sucesso",
                        mostrandoDialog = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        carregando = false,
                        erro = exception.message ?: "Erro ao cadastrar usuário"
                    )
                }
            )
        }
    }

    // 🔹 Atualizar usuário
    fun atualizarUsuario(
        id: String,
        nome: String,
        email: String,
        ehAdmin: Boolean,
        ativo: Boolean
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(carregando = true, erro = null)

            val usuarioAtual = _uiState.value.usuarios.find { it.id == id }
            if (usuarioAtual == null) {
                _uiState.value = _uiState.value.copy(
                    carregando = false,
                    erro = "Usuário não encontrado"
                )
                return@launch
            }

            val usuarioAtualizado = usuarioAtual.copy(
                nome = nome,
                email = email,
                ehAdmin = ehAdmin,
                ativo = ativo
            )

            val resultado = repositorioUsuario.atualizarUsuario(usuarioAtualizado)

            resultado.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        carregando = false,
                        erro = null,
                        sucessoOperacao = "Usuário atualizado com sucesso",
                        usuarioEditando = null,
                        mostrandoDialog = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        carregando = false,
                        erro = exception.message ?: "Erro ao atualizar usuário"
                    )
                }
            )
        }
    }

    // 🔹 Desativar usuário (soft delete)
    fun desativarUsuario(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(carregando = true, erro = null)

            val resultado = repositorioUsuario.desativarUsuario(id)

            resultado.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        carregando = false,
                        erro = null,
                        sucessoOperacao = "Usuário desativado com sucesso"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        carregando = false,
                        erro = exception.message ?: "Erro ao desativar usuário"
                    )
                }
            )
        }
    }

    // 🔹 Deletar usuário permanentemente
    fun deletarUsuario(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(carregando = true, erro = null)

            val resultado = repositorioUsuario.deletarUsuario(id)

            resultado.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        carregando = false,
                        erro = null,
                        sucessoOperacao = "Usuário deletado permanentemente"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        carregando = false,
                        erro = exception.message ?: "Erro ao deletar usuário"
                    )
                }
            )
        }
    }

    // 🔹 Selecionar usuário para edição
    fun selecionarUsuarioParaEdicao(usuario: UsuarioEntity) {
        _uiState.value = _uiState.value.copy(
            usuarioEditando = usuario,
            mostrandoDialog = true
        )
    }

    // 🔹 Mostrar dialog de novo usuário
    fun mostrarDialogNovoUsuario() {
        _uiState.value = _uiState.value.copy(
            usuarioEditando = null,
            mostrandoDialog = true
        )
    }

    // 🔹 Fechar dialog
    fun fecharDialog() {
        _uiState.value = _uiState.value.copy(
            usuarioEditando = null,
            mostrandoDialog = false,
            erro = null
        )
    }

    // 🔹 Limpar mensagens
    fun limparMensagens() {
        _uiState.value = _uiState.value.copy(
            erro = null,
            sucessoOperacao = null
        )
    }
}