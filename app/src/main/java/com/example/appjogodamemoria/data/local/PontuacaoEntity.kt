package com.example.appjogodamemoria.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pontuacoes")
data class PontuacaoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val pontos: Int
)