package com.example.appjogodamemoria.data.repository

import com.example.appjogodamemoria.data.local.UsuarioDao
import com.example.appjogodamemoria.data.local.UsuarioEntity
import com.example.appjogodamemoria.data.remoto.ServicoFirestore
import com.example.appjogodamemoria.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class RepositorioUsuario(
    private val usuarioDao: UsuarioDao,
    private val servicoFirestore: ServicoFirestore = ServicoFirestore()
) {

    // 🔹 Listar todos os usuários ativos
    fun listarUsuarios(): Flow<List<UsuarioEntity>> = usuarioDao.listarUsuarios()

    // 🔹 Fazer login (prioriza local, Firebase como backup)
    suspend fun login(email: String, senha: String): Result<UsuarioEntity> {
        return try {
            println("🔄 Tentando login para $email...")

            // Primeiro tenta login local (mais rápido)
            var usuario = usuarioDao.login(email, senha)

            if (usuario != null) {
                println("✅ Login local bem-sucedido para ${usuario.nome}")
                // Atualizar último login
                usuarioDao.atualizarUltimoLogin(usuario.id, System.currentTimeMillis())

                // Tentar sincronizar com Firebase (mas não bloquear se falhar)
                try {
                    val usuarioModel =
                        entityParaModel(usuario.copy(ultimoLogin = System.currentTimeMillis()))
                    servicoFirestore.salvarUsuario(usuarioModel)
                } catch (e: Exception) {
                    println("⚠️ Aviso: Não foi possível sincronizar com Firebase: ${e.message}")
                    // Continuar mesmo se Firebase falhar
                }

                return Result.success(usuario)
            }

            // Se não encontrou local, tenta no Firebase (apenas se conectado)
            println("🔍 Usuário não encontrado localmente, tentando Firebase...")
            try {
                val usuarioFirebase = servicoFirestore.buscarUsuarioPorEmail(email)
                if (usuarioFirebase != null && usuarioFirebase.senha == senha) {
                    // Salva no banco local
                    val usuarioEntity = modelParaEntity(usuarioFirebase)
                    usuarioDao.inserirUsuario(usuarioEntity)
                    usuario = usuarioEntity
                    println("✅ Usuário sincronizado do Firebase para local")
                    return Result.success(usuario)
                }
            } catch (e: Exception) {
                println("⚠️ Falha ao buscar no Firebase, continuando apenas com dados locais: ${e.message}")
            }

            // Se chegou até aqui, login falhou
            Result.failure(Exception("Email ou senha incorretos"))

        } catch (e: Exception) {
            println("❌ Erro no login: ${e.message}")
            Result.failure(e)
        }
    }

    // 🔹 Cadastrar novo usuário (salva local primeiro, Firebase como backup)
    suspend fun cadastrarUsuario(
        nome: String,
        email: String,
        senha: String,
        ehAdmin: Boolean = false
    ): Result<UsuarioEntity> {
        return try {
            println("🔄 Cadastrando usuário: $nome ($email)")

            // Verificar se email já existe localmente
            if (usuarioDao.emailExiste(email) > 0) {
                println("❌ Email já existe localmente")
                return Result.failure(Exception("Este email já está cadastrado"))
            }

            val novoUsuario = UsuarioEntity(
                id = UUID.randomUUID().toString(),
                nome = nome,
                email = email,
                senha = senha,
                ehAdmin = ehAdmin,
                dataCriacao = System.currentTimeMillis()
            )

            // Salva no banco local (prioridade)
            usuarioDao.inserirUsuario(novoUsuario)
            println("✅ Usuário cadastrado localmente: $nome")

            // Tentar salvar no Firebase (mas não bloquear se falhar)
            try {
                val usuarioModel = entityParaModel(novoUsuario)
                servicoFirestore.salvarUsuario(usuarioModel)
                println("✅ Usuário também salvo no Firebase")
            } catch (e: Exception) {
                println("⚠️ Aviso: Não foi possível salvar no Firebase: ${e.message}")
                // Continuar mesmo se Firebase falhar - dados ficam salvos localmente
            }

            Result.success(novoUsuario)
        } catch (e: Exception) {
            println("❌ Erro no cadastro: ${e.message}")
            Result.failure(e)
        }
    }

    // 🔹 Buscar usuário por ID
    suspend fun buscarPorId(id: String): UsuarioEntity? {
        return usuarioDao.buscarPorId(id)
    }

    // 🔹 Buscar usuário por email
    suspend fun buscarPorEmail(email: String): UsuarioEntity? {
        return usuarioDao.buscarPorEmail(email)
    }

    // 🔹 Atualizar usuário (local e Firebase)
    suspend fun atualizarUsuario(usuario: UsuarioEntity): Result<Unit> {
        return try {
            usuarioDao.atualizarUsuario(usuario)

            // Sincronizar com Firebase
            val usuarioModel = entityParaModel(usuario)
            servicoFirestore.atualizarUsuario(usuario.id, usuarioModel)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 🔹 Desativar usuário (soft delete)
    suspend fun desativarUsuario(id: String): Result<Unit> {
        return try {
            usuarioDao.desativarUsuario(id)

            // Atualizar no Firebase também
            val usuario = usuarioDao.buscarPorId(id)
            if (usuario != null) {
                val usuarioModel = entityParaModel(usuario.copy(ativo = false))
                servicoFirestore.atualizarUsuario(id, usuarioModel)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 🔹 Deletar usuário permanentemente (local e Firebase)
    suspend fun deletarUsuario(id: String): Result<Unit> {
        return try {
            usuarioDao.deletarUsuario(id)
            servicoFirestore.deletarUsuario(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 🔹 Atualizar melhor pontuação
    suspend fun atualizarMelhorPontuacao(id: String, pontuacao: Int) {
        try {
            usuarioDao.atualizarMelhorPontuacao(id, pontuacao)

            // Sincronizar com Firebase
            val usuario = usuarioDao.buscarPorId(id)
            if (usuario != null && pontuacao > usuario.melhorPontuacao) {
                val usuarioModel = entityParaModel(usuario.copy(melhorPontuacao = pontuacao))
                servicoFirestore.atualizarUsuario(id, usuarioModel)
            }
        } catch (e: Exception) {
            println("❌ Erro ao atualizar pontuação: ${e.message}")
        }
    }

    // 🔹 Atualizar menor número de tentativas
    suspend fun atualizarMenorTentativas(id: String, tentativas: Int) {
        try {
            usuarioDao.atualizarMenorTentativas(id, tentativas)

            // Sincronizar com Firebase
            val usuario = usuarioDao.buscarPorId(id)
            if (usuario != null && (tentativas < usuario.menorTentativas || usuario.menorTentativas == Int.MAX_VALUE)) {
                val usuarioModel = entityParaModel(usuario.copy(menorTentativas = tentativas))
                servicoFirestore.atualizarUsuario(id, usuarioModel)
            }
        } catch (e: Exception) {
            println("❌ Erro ao atualizar tentativas: ${e.message}")
        }
    }

    // 🔹 Obter ranking dos usuários
    fun obterRanking(): Flow<List<UsuarioEntity>> = usuarioDao.obterRanking()

    // 🔹 Verificar se email existe
    suspend fun emailExiste(email: String): Boolean {
        return usuarioDao.emailExiste(email) > 0
    }

    // 🔹 Sincronizar usuários com Firebase (carregar do Firebase para local)
    suspend fun sincronizarComFirebase(): Result<Unit> {
        return try {
            println("🔄 Sincronizando usuários com Firebase...")
            val usuariosFirebase = servicoFirestore.carregarUsuarios()

            usuariosFirebase.forEach { usuarioFirebase ->
                val usuarioLocal = usuarioDao.buscarPorEmail(usuarioFirebase.email)
                if (usuarioLocal == null) {
                    // Usuário não existe localmente, inserir
                    val usuarioEntity = modelParaEntity(usuarioFirebase)
                    usuarioDao.inserirUsuario(usuarioEntity)
                    println("✅ Usuário ${usuarioFirebase.nome} sincronizado do Firebase")
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ Erro na sincronização: ${e.message}")
            Result.failure(e)
        }
    }

    // 🔹 Limpar todos os dados locais
    suspend fun limparDadosLocais(): Result<Unit> {
        return try {
            println("🗑️ Limpando todos os dados locais...")
            usuarioDao.limparTodosUsuarios()
            println("✅ Dados locais limpos com sucesso")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ Erro ao limpar dados locais: ${e.message}")
            Result.failure(e)
        }
    }

    // 🔹 Converter Entity para Model
    fun entityParaModel(entity: UsuarioEntity): Usuario {
        return Usuario(
            id = entity.id,
            nome = entity.nome,
            email = entity.email,
            senha = entity.senha,
            ehAdmin = entity.ehAdmin,
            dataCriacao = entity.dataCriacao,
            ultimoLogin = entity.ultimoLogin,
            ativo = entity.ativo,
            melhorPontuacao = entity.melhorPontuacao,
            menorTentativas = entity.menorTentativas
        )
    }

    // 🔹 Converter Model para Entity
    private fun modelParaEntity(model: Usuario): UsuarioEntity {
        return UsuarioEntity(
            id = model.id,
            nome = model.nome,
            email = model.email,
            senha = model.senha,
            ehAdmin = model.ehAdmin,
            dataCriacao = model.dataCriacao,
            ultimoLogin = model.ultimoLogin,
            ativo = model.ativo,
            melhorPontuacao = model.melhorPontuacao,
            menorTentativas = model.menorTentativas
        )
    }
}