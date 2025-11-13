package com.example.appjogodamemoria.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cartas")
data class CartaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val imagemUrl: String,
    val revelada: Boolean = false,
    val combinada: Boolean = false
)