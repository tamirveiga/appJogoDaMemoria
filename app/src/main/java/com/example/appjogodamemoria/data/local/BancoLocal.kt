package com.example.appjogodamemoria.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CartaEntity::class, PontuacaoEntity::class, UsuarioEntity::class],
    version = 3,
    exportSchema = false
)
abstract class BancoLocal : RoomDatabase() {
    abstract fun cartaDao(): CartaDao
    abstract fun pontuacaoDao(): PontuacaoDao
    abstract fun usuarioDao(): UsuarioDao
}