package com.example.appjogodamemoria.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey
    val id: String,
    val nome: String,
    val email: String,
    val senha: String, // Em produção, seria um hash
    val ehAdmin: Boolean = false,
    val dataCriacao: Long = System.currentTimeMillis(),
    val ultimoLogin: Long = 0L,
    val ativo: Boolean = true,
    val melhorPontuacao: Int = 0,
    val menorTentativas: Int = Int.MAX_VALUE // Menor número de tentativas para completar o jogo
)