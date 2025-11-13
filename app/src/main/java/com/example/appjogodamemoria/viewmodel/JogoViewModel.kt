package com.example.appjogodamemoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appjogodamemoria.data.local.CartaEntity
import com.example.appjogodamemoria.data.local.PontuacaoEntity
import com.example.appjogodamemoria.data.repository.RepositorioJogo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class JogoUiState(
    val cartas: List<CartaEntity> = emptyList(),
    val pontuacoes: List<PontuacaoEntity> = emptyList(),
    val carregando: Boolean = false,
    val erro: String? = null
)

class JogoViewModel(private val repositorio: RepositorioJogo) : ViewModel() {

    private val _uiState = MutableStateFlow(JogoUiState())
    val uiState: StateFlow<JogoUiState> = _uiState

    init {
        carregarCartas()
        carregarPontuacoes()
    }

    private fun carregarCartas() {
        viewModelScope.launch {
            repositorio.listarCartas().collectLatest { lista ->
                _uiState.value = _uiState.value.copy(cartas = lista)
            }
        }
    }

    private fun carregarPontuacoes() {
        viewModelScope.launch {
            repositorio.listarPontuacoes().collectLatest { lista ->
                _uiState.value = _uiState.value.copy(pontuacoes = lista)
            }
        }
    }

    fun sincronizarCartas() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(carregando = true)
            try {
                repositorio.sincronizarCartas()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(erro = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(carregando = false)
            }
        }
    }

    fun salvarPontuacao(nome: String, pontos: Int) {
        viewModelScope.launch {
            try {
                repositorio.salvarPontuacao(PontuacaoEntity(nome = nome, pontos = pontos))
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(erro = e.message)
            }
        }
    }
}