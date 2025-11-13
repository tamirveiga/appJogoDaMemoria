package com.example.appjogodamemoria.model

data class Carta(
    val id: String = "",
    val nome: String = "",
    val imagemUrl: String = "",
    val revelada: Boolean = false,
    val combinada: Boolean = false
)