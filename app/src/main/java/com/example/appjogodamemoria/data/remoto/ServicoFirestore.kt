package com.example.appjogodamemoria.data.remoto

import com.google.firebase.firestore.FirebaseFirestore
import com.example.appjogodamemoria.model.Carta
import com.example.appjogodamemoria.model.Usuario
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class ServicoFirestore {

    private val db = FirebaseFirestore.getInstance()
    private val TIMEOUT_MS = 5000L // 5 segundos de timeout

    // ========== MÉTODOS PARA CARTAS ==========
    suspend fun carregarCartas(): List<Carta> {
        return try {
            val snapshot = withTimeoutOrNull(TIMEOUT_MS) {
                db.collection("cartas").get().await()
            }
            snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Carta::class.java)?.copy(id = doc.id)
            } ?: emptyList()
        } catch (e: Exception) {
            println("❌ Erro ao carregar cartas: ${e.message}")
            emptyList()
        }
    }

    suspend fun salvarCarta(carta: Carta) {
        try {
            withTimeoutOrNull(TIMEOUT_MS) {
                db.collection("cartas").add(carta).await()
            }
        } catch (e: Exception) {
            println("❌ Erro ao salvar carta: ${e.message}")
        }
    }

    suspend fun atualizarCarta(id: String, carta: Carta) {
        try {
            withTimeoutOrNull(TIMEOUT_MS) {
                db.collection("cartas").document(id).set(carta).await()
            }
        } catch (e: Exception) {
            println("❌ Erro ao atualizar carta: ${e.message}")
        }
    }

    suspend fun deletarCarta(id: String) {
        try {
            withTimeoutOrNull(TIMEOUT_MS) {
                db.collection("cartas").document(id).delete().await()
            }
        } catch (e: Exception) {
            println("❌ Erro ao deletar carta: ${e.message}")
        }
    }

    // ========== MÉTODOS PARA USUÁRIOS ==========
    suspend fun carregarUsuarios(): List<Usuario> {
        return try {
            println("🔄 Tentando carregar usuários do Firebase...")
            val snapshot = withTimeoutOrNull(TIMEOUT_MS) {
                db.collection("usuarios").get().await()
            }

            if (snapshot == null) {
                println("⏱️ Timeout ao carregar usuários do Firebase")
                return emptyList()
            }

            val usuarios = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Usuario::class.java)?.copy(id = doc.id)
            }
            println("✅ ${usuarios.size} usuários carregados do Firebase")
            usuarios
        } catch (e: Exception) {
            println("❌ Erro ao carregar usuários do Firebase: ${e.message}")
            emptyList()
        }
    }

    suspend fun salvarUsuario(usuario: Usuario): Boolean {
        return try {
            println("🔄 Tentando salvar usuário ${usuario.nome} no Firebase...")
            val resultado = withTimeoutOrNull(TIMEOUT_MS) {
                if (usuario.id.isNotEmpty()) {
                    db.collection("usuarios").document(usuario.id).set(usuario).await()
                } else {
                    db.collection("usuarios").add(usuario).await()
                }
                true
            }

            if (resultado == true) {
                println("✅ Usuário ${usuario.nome} salvo no Firebase")
                true
            } else {
                println("⏱️ Timeout ao salvar usuário no Firebase")
                false
            }
        } catch (e: Exception) {
            println("❌ Erro ao salvar usuário no Firebase: ${e.message}")
            false
        }
    }

    suspend fun atualizarUsuario(id: String, usuario: Usuario): Boolean {
        return try {
            val resultado = withTimeoutOrNull(TIMEOUT_MS) {
                db.collection("usuarios").document(id).set(usuario).await()
                true
            }

            if (resultado == true) {
                println("✅ Usuário atualizado no Firebase")
                true
            } else {
                println("⏱️ Timeout ao atualizar usuário no Firebase")
                false
            }
        } catch (e: Exception) {
            println("❌ Erro ao atualizar usuário no Firebase: ${e.message}")
            false
        }
    }

    suspend fun deletarUsuario(id: String): Boolean {
        return try {
            val resultado = withTimeoutOrNull(TIMEOUT_MS) {
                db.collection("usuarios").document(id).delete().await()
                true
            }

            if (resultado == true) {
                println("✅ Usuário deletado do Firebase")
                true
            } else {
                println("⏱️ Timeout ao deletar usuário no Firebase")
                false
            }
        } catch (e: Exception) {
            println("❌ Erro ao deletar usuário do Firebase: ${e.message}")
            false
        }
    }

    suspend fun buscarUsuarioPorEmail(email: String): Usuario? {
        return try {
            println("🔄 Buscando usuário $email no Firebase...")
            val snapshot = withTimeoutOrNull(TIMEOUT_MS) {
                db.collection("usuarios")
                    .whereEqualTo("email", email)
                    .whereEqualTo("ativo", true)
                    .limit(1)
                    .get()
                    .await()
            }

            if (snapshot == null) {
                println("⏱️ Timeout ao buscar usuário no Firebase")
                return null
            }

            val usuario =
                snapshot.documents.firstOrNull()?.toObject(Usuario::class.java)?.let { user ->
                    user.copy(id = snapshot.documents.first().id)
                }

            if (usuario != null) {
                println("✅ Usuário ${usuario.nome} encontrado no Firebase")
            } else {
                println("❌ Usuário $email não encontrado no Firebase")
            }

            usuario
        } catch (e: Exception) {
            println("❌ Erro ao buscar usuário por email: ${e.message}")
            null
        }
    }

    suspend fun sincronizarUsuarios(usuariosLocais: List<Usuario>): Boolean {
        return try {
            println("🔄 Iniciando sincronização de ${usuariosLocais.size} usuários com Firebase...")
            var sucessos = 0

            usuariosLocais.forEach { usuario ->
                if (salvarUsuario(usuario)) {
                    sucessos++
                }
            }

            println("✅ Sincronização concluída: $sucessos/${usuariosLocais.size} usuários")
            sucessos == usuariosLocais.size
        } catch (e: Exception) {
            println("❌ Erro na sincronização: ${e.message}")
            false
        }
    }
}