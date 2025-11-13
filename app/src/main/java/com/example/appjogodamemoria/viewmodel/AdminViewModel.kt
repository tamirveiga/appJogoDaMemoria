package com.example.appjogodamemoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appjogodamemoria.data.local.CartaEntity
import com.example.appjogodamemoria.data.repository.RepositorioJogo
import com.example.appjogodamemoria.model.Carta
import kotlinx.coroutines.launch

class AdminViewModel(private val repositorio: RepositorioJogo) : ViewModel() {

    fun adicionarCarta(nome: String, imagemUrl: String) {
        viewModelScope.launch {
            try {
                repositorio.inserirCartaLocal(
                    CartaEntity(nome = nome, imagemUrl = imagemUrl)
                )
            } catch (_: Exception) { }
        }
    }

    fun sincronizarComFirestore() {
        viewModelScope.launch {
            try {
                repositorio.sincronizarCartas()
            } catch (_: Exception) { }
        }
    }
}