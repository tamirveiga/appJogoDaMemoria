package com.example.appjogodamemoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appjogodamemoria.data.repository.RepositorioJogo

class FabricaViewModel(private val repositorio: RepositorioJogo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(JogoViewModel::class.java) -> {
                JogoViewModel(repositorio) as T
            }
            modelClass.isAssignableFrom(AdminViewModel::class.java) -> {
                AdminViewModel(repositorio) as T
            }
            else -> throw IllegalArgumentException("ViewModel desconhecida: ${modelClass.name}")
        }
    }
}