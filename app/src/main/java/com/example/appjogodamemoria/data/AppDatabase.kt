package com.example.appjogodamemoria.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.appjogodamemoria.data.local.BancoLocal
import com.example.appjogodamemoria.data.remoto.ServicoFirestore
import com.example.appjogodamemoria.data.repository.RepositorioJogo
import com.example.appjogodamemoria.data.repository.RepositorioUsuario
import com.example.appjogodamemoria.model.Usuario
import com.example.appjogodamemoria.viewmodel.AdminUsuarioViewModel
import com.example.appjogodamemoria.viewmodel.AuthViewModel
import com.example.appjogodamemoria.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

object AppDatabase {
    @Volatile
    private var INSTANCE: BancoLocal? = null

    @Volatile
    private var REPOSITORIO_JOGO_INSTANCE: RepositorioJogo? = null

    @Volatile
    private var REPOSITORIO_USUARIO_INSTANCE: RepositorioUsuario? = null

    @Volatile
    private var VIEW_MODEL_FACTORY_INSTANCE: ViewModelFactory? = null

    @Volatile
    private var AUTH_VIEW_MODEL_INSTANCE: AuthViewModel? = null

    @Volatile
    private var ADMIN_USUARIO_VIEW_MODEL_INSTANCE: AdminUsuarioViewModel? = null

    fun getDatabase(context: Context): BancoLocal {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                BancoLocal::class.java,
                "jogo_memoria_database"
            )
                .fallbackToDestructiveMigration() // Para facilitar desenvolvimento
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Criar usuário admin padrão
                        CoroutineScope(Dispatchers.IO).launch {
                            val adminId = UUID.randomUUID().toString()
                            val timestamp = System.currentTimeMillis()

                            // Criar no banco local
                            db.execSQL(
                                "INSERT INTO usuarios (id, nome, email, senha, ehAdmin, dataCriacao, ultimoLogin, ativo, melhorPontuacao, menorTentativas) " +
                                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                arrayOf(
                                    adminId,
                                    "Administrador",
                                    "admin@admin.com",
                                    "123456",
                                    1,
                                    timestamp,
                                    0,
                                    1,
                                    0,
                                    Int.MAX_VALUE
                                )
                            )

                            // Criar também no Firebase
                            try {
                                val servicoFirestore = ServicoFirestore()
                                val adminUsuario = Usuario(
                                    id = adminId,
                                    nome = "Administrador",
                                    email = "admin@admin.com",
                                    senha = "123456",
                                    ehAdmin = true,
                                    dataCriacao = timestamp,
                                    ultimoLogin = 0L,
                                    ativo = true,
                                    melhorPontuacao = 0,
                                    menorTentativas = Int.MAX_VALUE
                                )
                                servicoFirestore.salvarUsuario(adminUsuario)
                                println(" Usuário admin criado no Firebase")
                            } catch (e: Exception) {
                                println(" Erro ao criar admin no Firebase: ${e.message}")
                            }
                        }
                    }
                })
                .build()
            INSTANCE = instance
            instance
        }
    }

    fun getRepositorioJogo(context: Context): RepositorioJogo {
        return REPOSITORIO_JOGO_INSTANCE ?: synchronized(this) {
            val database = getDatabase(context)
            val instance = RepositorioJogo(
                cartaDao = database.cartaDao(),
                pontuacaoDao = database.pontuacaoDao(),
                servicoFirestore = ServicoFirestore()
            )
            REPOSITORIO_JOGO_INSTANCE = instance
            instance
        }
    }

    fun getRepositorioUsuario(context: Context): RepositorioUsuario {
        return REPOSITORIO_USUARIO_INSTANCE ?: synchronized(this) {
            val database = getDatabase(context)
            val instance = RepositorioUsuario(
                usuarioDao = database.usuarioDao(),
                servicoFirestore = ServicoFirestore()
            )
            REPOSITORIO_USUARIO_INSTANCE = instance

            // Sincronização removida para evitar travamentos na inicialização

            instance
        }
    }

    fun getViewModelFactory(context: Context): ViewModelFactory {
        return VIEW_MODEL_FACTORY_INSTANCE ?: synchronized(this) {
            val repositorio = getRepositorioJogo(context)
            val instance = ViewModelFactory(repositorio)
            VIEW_MODEL_FACTORY_INSTANCE = instance
            instance
        }
    }

    fun getAuthViewModel(context: Context): AuthViewModel {
        return AUTH_VIEW_MODEL_INSTANCE ?: synchronized(this) {
            val repositorio = getRepositorioUsuario(context)
            val instance = AuthViewModel(repositorio)
            AUTH_VIEW_MODEL_INSTANCE = instance
            println(" Nova instância do AuthViewModel criada")
            instance
        }
    }

    fun getAdminUsuarioViewModel(context: Context): AdminUsuarioViewModel {
        return ADMIN_USUARIO_VIEW_MODEL_INSTANCE ?: synchronized(this) {
            val repositorio = getRepositorioUsuario(context)
            val instance = AdminUsuarioViewModel(repositorio)
            ADMIN_USUARIO_VIEW_MODEL_INSTANCE = instance
            instance
        }
    }
}