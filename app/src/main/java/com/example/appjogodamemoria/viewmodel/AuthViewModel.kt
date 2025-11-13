package com.example.appjogodamemoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appjogodamemoria.data.local.UsuarioEntity
import com.example.appjogodamemoria.data.repository.RepositorioUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val usuarioLogado: UsuarioEntity? = null,
    val estaLogado: Boolean = false,
    val carregando: Boolean = false,
    val erro: String? = null,
    val sucessoCadastro: Boolean = false
)

class AuthViewModel(
    private val repositorioUsuario: RepositorioUsuario
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    // Sincronização removida da inicialização para evitar travamentos
    // Pode ser chamada manualmente quando necessário
    init {
        // Nenhuma ação aqui
    }

    // 🔹 Fazer login
    fun login(email: String, senha: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(carregando = true, erro = null)

            if (email.isBlank() || senha.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    carregando = false,
                    erro = "Email e senha são obrigatórios"
                )
                return@launch
            }

            val resultado = repositorioUsuario.login(email, senha)

            resultado.fold(
                onSuccess = { usuario ->
                    println("🔍 Login realizado - Nome: ${usuario.nome}, Admin: ${usuario.ehAdmin}")
                    _uiState.value = _uiState.value.copy(
                        carregando = false,
                        usuarioLogado = usuario,
                        estaLogado = true,
                        erro = null
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        carregando = false,
                        erro = exception.message ?: "Erro no login"
                    )
                }
            )
        }
    }

    // 🔹 Cadastrar usuário
    fun cadastrar(nome: String, email: String, senha: String, confirmarSenha: String) {
        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(carregando = true, erro = null, sucessoCadastro = false)

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

                senha != confirmarSenha -> {
                    _uiState.value = _uiState.value.copy(
                        carregando = false,
                        erro = "Senhas não coincidem"
                    )
                    return@launch
                }
            }

            val resultado = repositorioUsuario.cadastrarUsuario(nome, email, senha)

            resultado.fold(
                onSuccess = { usuario ->
                    println("🔍 Usuário cadastrado - Nome: ${usuario.nome}, Admin: ${usuario.ehAdmin}")
                    _uiState.value = _uiState.value.copy(
                        carregando = false,
                        usuarioLogado = usuario,
                        estaLogado = true,
                        erro = null,
                        sucessoCadastro = true
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        carregando = false,
                        erro = exception.message ?: "Erro no cadastro"
                    )
                }
            )
        }
    }

    // 🔹 Logout
    fun logout() {
        _uiState.value = AuthUiState()
    }

    // 🔹 Limpar erro
    fun limparErro() {
        _uiState.value = _uiState.value.copy(erro = null)
    }

    // 🔹 Limpar sucesso do cadastro
    fun limparSucessoCadastro() {
        _uiState.value = _uiState.value.copy(sucessoCadastro = false)
    }

    // 🔹 Atualizar melhor pontuação do usuário logado
    fun atualizarMelhorPontuacao(pontuacao: Int) {
        viewModelScope.launch {
            val usuario = _uiState.value.usuarioLogado
            if (usuario != null && pontuacao > usuario.melhorPontuacao) {
                repositorioUsuario.atualizarMelhorPontuacao(usuario.id, pontuacao)

                // Atualizar estado local
                _uiState.value = _uiState.value.copy(
                    usuarioLogado = usuario.copy(melhorPontuacao = pontuacao)
                )
            }
        }
    }

    // 🔹 Atualizar menor número de tentativas do usuário logado
    fun atualizarMenorTentativas(tentativas: Int) {
        viewModelScope.launch {
            val usuario = _uiState.value.usuarioLogado
            if (usuario != null && (tentativas < usuario.menorTentativas || usuario.menorTentativas == Int.MAX_VALUE)) {
                repositorioUsuario.atualizarMenorTentativas(usuario.id, tentativas)

                // Atualizar estado local
                _uiState.value = _uiState.value.copy(
                    usuarioLogado = usuario.copy(menorTentativas = tentativas)
                )
            }
        }
    }

    // 🔹 Verificar se usuário é admin
    fun ehAdmin(): Boolean {
        return _uiState.value.usuarioLogado?.ehAdmin ?: false
    }

    // 🔹 Obter nome do usuário logado
    fun obterNomeUsuario(): String {
        return _uiState.value.usuarioLogado?.nome ?: "Usuário"
    }

    // 🔹 Obter ID do usuário logado
    fun obterIdUsuario(): String? {
        return _uiState.value.usuarioLogado?.id
    }

    // 🔹 Sincronizar manualmente com Firebase
    fun sincronizarComFirebase() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(carregando = true)
                repositorioUsuario.sincronizarComFirebase()
                println("✅ Sincronização manual com Firebase concluída")
            } catch (e: Exception) {
                println("❌ Erro na sincronização manual: ${e.message}")
                _uiState.value = _uiState.value.copy(erro = "Erro na sincronização: ${e.message}")
            } finally {
                _uiState.value = _uiState.value.copy(carregando = false)
            }
        }
    }
}