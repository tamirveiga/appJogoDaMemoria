package com.example.appjogodamemoria.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PontuacaoDao {

    @Query("SELECT * FROM pontuacoes ORDER BY pontos DESC")
    fun listarPontuacoes(): Flow<List<PontuacaoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirPontuacao(pontuacao: PontuacaoEntity)

    @Query("DELETE FROM pontuacoes")
    suspend fun deletarTodas()
}