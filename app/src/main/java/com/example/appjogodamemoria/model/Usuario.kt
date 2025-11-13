package com.example.appjogodamemoria.model

data class Usuario(
    val id: String = "",
    val nome: String = "",
    val email: String = "",
    val senha: String = "",
    val ehAdmin: Boolean = false,
    val dataCriacao: Long = System.currentTimeMillis(),
    val ultimoLogin: Long = 0L,
    val ativo: Boolean = true,
    val melhorPontuacao: Int = 0,
    val menorTentativas: Int = Int.MAX_VALUE
)