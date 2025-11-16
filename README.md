# 🎮 Jogo da Memória - Android

Um jogo da memória desenvolvido em Android usando **Kotlin** e **Jetpack Compose**, seguindo o
padrão **MVVM** com persistência local e sincronização em nuvem.

## 📱 Demonstração

### 🎥 Vídeo do App Funcionando

*https://github.com/tamirveiga/appJogoDaMemoria/blob/main/assets/demo.mp4*

---

## ✨ Principais Funcionalidades

###  **Jogo da Memória**

- Grid 4x4 com 8 pares de frutas
- Sistema de pontuação (+10 por acerto)
- Contador de tentativas
- Classificação por performance

###  **Sistema de Usuários**

- Cadastro e login seguro
- Perfil completo com estatísticas
- Ranking global por tentativas
- Administradores com CRUD completo

###  **Ranking Inteligente**

- Ordenação por **menor número de tentativas**
- Medalhas para top 3 jogadores
- Histórico de melhor pontuação
- Status de jogador baseado em performance

###  **Administração**

- Login especial para admins (`admin@admin.com` / `123456`)
- CRUD completo de usuários
- Controle de permissões
- Gerenciamento de contas

## 🛠️ Tecnologias Utilizadas

- **Kotlin** - Linguagem principal
- **Jetpack Compose** - Interface moderna
- **Room Database** - Persistência local
- **Firebase Firestore** - Sincronização em nuvem
- **MVVM** - Arquitetura robusta
- **Material 3** - Design system

##  Como Executar

1. **Clone o repositório**
2. **Abra no Android Studio**
3. **Sincronize as dependências**
4. **Execute em dispositivo/emulador** (API 24+)

##  Contas de Teste

**Administrador:**
- Email: `admin@admin.com`
- Senha: `123456`

**Usuário Normal:**

- Cadastre-se normalmente no app

##  Sistema de Classificação

-  **≤12 tentativas**: Mestre da Memória
-  **≤16 tentativas**: Jogador Experiente
-  **≤20 tentativas**: Bom Jogador
-  **>20 tentativas**: Continue Praticando
