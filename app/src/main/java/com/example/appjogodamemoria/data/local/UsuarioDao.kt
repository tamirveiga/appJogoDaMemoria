package com.example.appjogodamemoria.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE ativo = 1 ORDER BY nome ASC")
    fun listarUsuarios(): Flow<List<UsuarioEntity>>

    @Query("SELECT * FROM usuarios WHERE email = :email AND senha = :senha AND ativo = 1 LIMIT 1")
    suspend fun login(email: String, senha: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE email = :email AND ativo = 1 LIMIT 1")
    suspend fun buscarPorEmail(email: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: String): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirUsuario(usuario: UsuarioEntity)

    @Update
    suspend fun atualizarUsuario(usuario: UsuarioEntity)

    @Query("UPDATE usuarios SET ativo = 0 WHERE id = :id")
    suspend fun desativarUsuario(id: String)

    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun deletarUsuario(id: String)

    @Query("UPDATE usuarios SET ultimoLogin = :timestamp WHERE id = :id")
    suspend fun atualizarUltimoLogin(id: String, timestamp: Long)

    @Query("UPDATE usuarios SET melhorPontuacao = :pontuacao WHERE id = :id AND melhorPontuacao < :pontuacao")
    suspend fun atualizarMelhorPontuacao(id: String, pontuacao: Int)

    @Query("UPDATE usuarios SET menorTentativas = :tentativas WHERE id = :id AND (menorTentativas > :tentativas OR menorTentativas = ${Int.MAX_VALUE})")
    suspend fun atualizarMenorTentativas(id: String, tentativas: Int)

    @Query("SELECT COUNT(*) FROM usuarios WHERE email = :email AND ativo = 1")
    suspend fun emailExiste(email: String): Int

    @Query("SELECT * FROM usuarios WHERE menorTentativas != ${Int.MAX_VALUE} ORDER BY menorTentativas ASC LIMIT 10")
    fun obterRanking(): Flow<List<UsuarioEntity>>

    // 🔹 Limpar todos os usuários (para reset completo)
    @Query("DELETE FROM usuarios")
    suspend fun limparTodosUsuarios()
}