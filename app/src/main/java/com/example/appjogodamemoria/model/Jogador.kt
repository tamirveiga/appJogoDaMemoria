package com.example.appjogodamemoria.model

data class Jogador(
    val id: String = "",
    val nome: String = "",
    val pontuacao: Int = 0,
    val ehAdmin: Boolean = false
)