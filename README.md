<<<<<<< HEAD
# 🍎 Jogo da Memória - Android

Um jogo da memória desenvolvido em Android usando Kotlin, Jetpack Compose e arquitetura MVVM com
sistema completo de autenticação e gerenciamento de usuários.

## 📱 Funcionalidades

### 🎮 Sistema de Jogo

- **Jogo da Memória**: Jogo clássico com cartas de frutas em grid 4x4
- **Sistema de Pontuação**: Pontuação baseada em acertos (+10 pontos por par)
- **Contador de Tentativas**: Acompanhamento do número de tentativas
- **Interface Moderna**: Animações e feedback visual

### 👤 Sistema de Autenticação

- **Cadastro de Usuários**: Registro com nome, email e senha
- **Login Seguro**: Autenticação com email e senha
- **Validação Completa**: Verificação de email único e senhas
- **Sessão Persistente**: Manutenção do login entre sessões

### 🏆 Sistema de Ranking

- **Ranking de Usuários**: Lista dos melhores jogadores por pontuação
- **Melhor Pontuação**: Acompanhamento do recorde de cada usuário
- **Interface Visual**: Medalhas para top 3 e design diferenciado
- **Atualização Automática**: Sincronização em tempo real

### 👑 Painel Administrativo

- **CRUD Completo de Usuários**: Criar, ler, atualizar e deletar usuários
- **Gerenciamento de Permissões**: Definir usuários como administradores
- **Controle de Status**: Ativar/desativar contas de usuários
- **Estatísticas**: Visualizar dados dos usuários e pontuações

### 💾 Armazenamento

- **Banco Local**: Room Database para dados offline
- **Sincronização Cloud**: Integração com Firebase Firestore
- **Backup Automático**: Dados salvos localmente e na nuvem

## 🏗️ Arquitetura

O projeto segue o padrão **MVVM (Model-View-ViewModel)** com as seguintes camadas:

```
📁 app/src/main/java/com/example/appjogodamemoria/
├── 📁 data/
│   ├── 📁 local/          # Room Database (SQLite)
│   │   ├── BancoLocal.kt           # Configuração do banco
│   │   ├── UsuarioEntity.kt        # Entidade de usuários
│   │   ├── UsuarioDao.kt           # DAO para usuários
│   │   ├── CartaEntity.kt          # Entidade de cartas
│   │   ├── CartaDao.kt             # DAO para cartas
│   │   ├── PontuacaoEntity.kt      # Entidade de pontuações
│   │   └── PontuacaoDao.kt         # DAO para pontuações
│   ├── 📁 remoto/         # Firebase Firestore
│   │   └── ServicoFirestore.kt     # Serviços da nuvem
│   ├── 📁 repository/     # Repositórios para gerenciar dados
│   │   ├── RepositorioUsuario.kt   # Repositório de usuários
│   │   └── RepositorioJogo.kt      # Repositório do jogo
│   └── AppDatabase.kt     # Configuração e dependências
├── 📁 model/              # Classes de modelo/entidade
│   ├── Usuario.kt                  # Modelo de usuário
│   ├── Carta.kt                    # Modelo de carta
│   └── Jogador.kt                  # Modelo de jogador
├── 📁 viewmodel/          # ViewModels para lógica de negócio
│   ├── AuthViewModel.kt            # ViewModel de autenticação
│   ├── AdminUsuarioViewModel.kt    # ViewModel para admin de usuários
│   ├── JogoViewModel.kt            # ViewModel do jogo
│   └── FabricaViewModel.kt         # Factory de ViewModels
├── 📁 ui/
│   ├── 📁 theme/          # Tema e cores
│   ├── 📁 telas/          # Telas/Screens Compose
│   │   ├── TelaLogin.kt            # Tela de login
│   │   ├── TelaCadastro.kt         # Tela de cadastro
│   │   ├── TelaJogo.kt             # Tela do jogo
│   │   ├── TelaRanking.kt          # Tela de ranking
│   │   ├── TelaAdmin.kt            # Painel administrativo
│   │   ├── TelaAdminUsuarios.kt    # CRUD de usuários
│   │   └── NavegacaoApp.kt         # Navegação principal
│   └── 📁 componentes/    # Componentes reutilizáveis
└── MainActivity.kt
```

## 🚀 Tecnologias Utilizadas

- **Kotlin**: Linguagem principal
- **Jetpack Compose**: Interface moderna e reativa
- **Room Database**: Armazenamento local com SQLite
- **Firebase Firestore**: Banco de dados na nuvem
- **Navigation Compose**: Navegação entre telas
- **MVVM Pattern**: Arquitetura limpa e testável
- **Coroutines**: Programação assíncrona
- **StateFlow**: Gerenciamento reativo de estado
- **Material 3**: Design system moderno

## 🎮 Como Usar

### 🔐 Sistema de Login/Cadastro

1. **Primeiro Acesso**:
    - Abra o app e clique em "Não tem conta? Cadastre-se"
    - Preencha nome, email, senha e confirmação
    - Faça login automaticamente após cadastro

2. **Login Posterior**:
    - Digite email e senha
    - Clique em "Entrar"
    - Para teste de admin: use "Acesso Admin (Desenvolvimento)"

3. **Acesso Administrativo**:
    - Email: `admin@admin.com`
    - Senha: `123456`
    - Ou crie um usuário e marque como administrador

### 🎯 Jogando

1. **Iniciar Partida**: No menu principal, clique em "Jogar"
2. **Gameplay**:
    - Clique em duas cartas para revelá-las
    - Se forem iguais, elas permanecem viradas
    - Se diferentes, voltam a ficar viradas para baixo
    - Continue até encontrar todos os 8 pares
3. **Pontuação**: Ganhe 10 pontos para cada par correto
4. **Final**: Sua melhor pontuação é salva automaticamente

### 🏆 Ranking

- Visualize os melhores jogadores
- Top 3 com medalhas especiais
- Pontuações atualizadas em tempo real
- Diferenciação visual para administradores

### 👑 Painel Admin

**Gerenciar Usuários:**

- ➕ **Criar**: Adicionar novos usuários
- ✏️ **Editar**: Modificar dados existentes
- 🗑️ **Deletar**: Remover usuários permanentemente
- 👤 **Permissões**: Definir como administrador
- 🔄 **Status**: Ativar/desativar contas

**Funcionalidades:**

- Lista completa de usuários
- Dados detalhados (criação, último login, pontuação)
- Interface intuitiva com dialogs
- Validação de dados

## 📋 Pré-requisitos

- Android Studio Hedgehog ou superior
- JDK 17+
- Android SDK (API 24+)
- Conexão com internet (para sincronização Firebase)

## 🛠️ Configuração do Projeto

1. **Clone o repositório**
2. **Abra no Android Studio**
3. **Configure o Firebase**:
    - Crie um projeto no [Firebase Console](https://console.firebase.google.com)
    - Adicione um app Android
    - Baixe o `google-services.json` e coloque na pasta `app/`
    - Configure o Firestore Database

4. **Sincronize as dependências**:
```bash
./gradlew build
```

## 📦 Dependências Principais

```kotlin
// Jetpack Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.activity:activity-compose")

// Navigation
implementation("androidx.navigation:navigation-compose")

// Room Database
implementation("androidx.room:room-runtime")
implementation("androidx.room:room-ktx")
kapt("androidx.room:room-compiler")

// Firebase
implementation(platform("com.google.firebase:firebase-bom"))
implementation("com.google.firebase:firebase-firestore-ktx")

// Lifecycle & ViewModel
implementation("androidx.lifecycle:lifecycle-runtime-ktx")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
```

## 🎨 Features do Sistema

### 🔐 Autenticação

- Validação de email único
- Senhas com mínimo de 6 caracteres
- Confirmação de senha no cadastro
- Estados de carregamento e erro
- Login automático após cadastro

### 🎯 Jogo da Memória
- Grid 4x4 com 16 cartas (8 pares)
- Emojis de frutas coloridos
- Sistema de bloqueio durante comparação
- Animações de revelação
- Contador de tentativas
- Tela de parabéns ao completar

### 📊 Sistema de Dados

- **Usuários**: ID, nome, email, senha, admin, ativo, melhor pontuação
- **Pontuações**: Histórico de jogadas para cada usuário
- **Cartas**: Dados das cartas (futuro: gerenciamento pelo admin)

### 🎨 Interface

- Material 3 Design
- Tema personalizado com cores pastel
- Componentes reutilizáveis
- Navegação fluida
- Feedback visual completo

## 🔧 Banco de Dados

### Estrutura Room (Local)

```sql
-- Tabela de usuários
usuarios (
    id TEXT PRIMARY KEY,
    nome TEXT NOT NULL,
    email TEXT NOT NULL,
    senha TEXT NOT NULL,
    ehAdmin INTEGER DEFAULT 0,
    dataCriacao INTEGER NOT NULL,
    ultimoLogin INTEGER DEFAULT 0,
    ativo INTEGER DEFAULT 1,
    melhorPontuacao INTEGER DEFAULT 0
)

-- Tabela de pontuações (histórico)
pontuacoes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    pontos INTEGER NOT NULL
)

-- Tabela de cartas
cartas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    imagemUrl TEXT NOT NULL,
    revelada INTEGER DEFAULT 0,
    combinada INTEGER DEFAULT 0
)
```

### Firestore (Cloud)
```
Collection: "usuarios" (futuro)
- nome: String
- email: String
- melhorPontuacao: Number

Collection: "pontuacoes"
- nome: String  
- pontos: Number
- timestamp: Number

Collection: "cartas"
- nome: String
- imagemUrl: String
```

## 🚀 Como Executar

1. **Via Android Studio**:
    - Abra o projeto
    - Conecte um dispositivo ou inicie um emulador
    - Clique em "Run" ou pressione `Shift + F10`

2. **Via Linha de Comando**:
```bash
./gradlew assembleDebug
./gradlew installDebug
```

## 🧪 Testes

Execute os testes unitários:
```bash
./gradlew test
```

Execute os testes instrumentados:
```bash
./gradlew connectedAndroidTest
```

## 🔒 Credenciais Padrão

**Administrador (criado automaticamente):**

- Email: `admin@admin.com`
- Senha: `123456`
- Permissões: Administrador completo

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 👨‍💻 Desenvolvedor

Desenvolvido com ❤️ usando as melhores práticas de desenvolvimento Android moderno.

---

**Status**: ✅ Projeto Totalmente Funcional  
**Funcionalidades**: 🔐 Autenticação + 🎮 Jogo + 🏆 Ranking + 👑 Admin CRUD  
**Última Atualização**: Novembro 2024

## 🎯 Próximas Funcionalidades

- [ ] Recuperação de senha por email
- [ ] Perfis de usuário com avatares
- [ ] Diferentes níveis de dificuldade
- [ ] Modo multiplayer
- [ ] Conquistas e badges
- [ ] Estatísticas detalhadas
- [ ] Temas personalizáveis
=======
# appJogoDaMemoria
Projeto final da disciplina de Des. de Aplicativos Móveis.
>>>>>>> 9d5f23be2f522ea6ca7ce5b71792da9566cfe50c
