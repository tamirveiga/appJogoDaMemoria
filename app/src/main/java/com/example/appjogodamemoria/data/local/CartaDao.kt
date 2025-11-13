package com.example.appjogodamemoria.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartaDao {

    @Query("SELECT * FROM cartas")
    fun listarCartas(): Flow<List<CartaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirCarta(carta: CartaEntity)

    @Update
    suspend fun atualizarCarta(carta: CartaEntity)

    @Delete
    suspend fun deletarCarta(carta: CartaEntity)

    @Query("DELETE FROM cartas")
    suspend fun deletarTodas()
}