package com.example.appjogodamemoria.data.repository

import com.example.appjogodamemoria.data.local.*
import com.example.appjogodamemoria.data.remoto.ServicoFirestore
import com.example.appjogodamemoria.model.Carta
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RepositorioJogo(
    private val cartaDao: CartaDao,
    private val pontuacaoDao: PontuacaoDao,
    private val servicoFirestore: ServicoFirestore
) {

    // 🔹 Listar cartas do banco local (Flow)
    fun listarCartas(): Flow<List<CartaEntity>> = cartaDao.listarCartas()

    // 🔹 Carregar cartas do Firestore
    suspend fun carregarCartasRemotas(): List<Carta> {
        return try {
            servicoFirestore.carregarCartas()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // 🔹 Inserir carta localmente
    suspend fun inserirCartaLocal(carta: CartaEntity) {
        cartaDao.inserirCarta(carta)
    }

    // 🔹 Inserir pontuação
    suspend fun salvarPontuacao(pontuacao: PontuacaoEntity) {
        pontuacaoDao.inserirPontuacao(pontuacao)
    }

    // 🔹 Listar pontuações (ordenadas)
    fun listarPontuacoes(): Flow<List<PontuacaoEntity>> =
        pontuacaoDao.listarPontuacoes().map { lista ->
            lista.sortedByDescending { it.pontos }
        }

    // 🔹 Sincronizar cartas com Firestore
    suspend fun sincronizarCartas() {
        val cartasRemotas = carregarCartasRemotas()
        cartaDao.deletarTodas()
        cartasRemotas.forEach {
            cartaDao.inserirCarta(
                CartaEntity(
                    nome = it.nome,
                    imagemUrl = it.imagemUrl
                )
            )
        }
    }
}