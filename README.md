# E-Library — Sistema de Gerenciamento de Biblioteca Distribuída

## 🔹 Visão Geral
Sistema distribuído para gerenciamento de biblioteca, com backend centralizado em EJB (Jakarta EE) e clientes Web (JSF) e Desktop (Java SE). Suporta usuários (alunos/professores), livros, exemplares, empréstimos e categorias.

## 🔹 Tecnologias
- **Backend (EAR):** Jakarta EE 9+, Java 17, EJB (Stateless, Stateful, Singleton), JPA 3.0 (Hibernate)
- **Banco de Dados:** PostgreSQL
- **Frontend Web:** JSF 3.0 + PrimeFaces
- **Cliente Desktop:** Java SE (Swing/JavaFX) com EJB remoto
- **Build/Deploy:** Maven, WildFly/GlassFish

## 🔹 Estrutura do Projeto (EAR — Enterprise Application)

O projeto segue a arquitetura clássica Jakarta EE com um **EAR (Enterprise Archive)** que empacota dois módulos:

| Módulo | Tipo | Responsabilidade |
|--------|------|------------------|
| **`E-Library`** | `EJB Module` (.jar) | Contém toda a lógica de negócio: entidades JPA (`br.elibrary.model`), Session Beans (`br.elibrary.stateless`, `br.elibrary.stateful`, `br.elibrary.singleton`) e serviços (`br.elibrary.service`). É o núcleo do sistema e roda no servidor de aplicação (ex: WildFly). |
| **`E-LibraryClient`** | `WAR Module` (.war) | Aplicação web JSF + PrimeFaces. Acessa os EJBs localmente (via injeção `@EJB` ou CDI `@Inject`) para fornecer interface ao usuário (login, busca, empréstimo, administração). |
| **`E-LibraryEAR`** | `EAR` (.ear) | Arquivo de empacotamento que agrupa `E-Library.jar` e `E-LibraryClient.war`, garantindo que ambos sejam implantados juntos no servidor de aplicação. Permite compartilhamento de contexto (ex: `@EJB` sem lookup remoto). |

✅ **Vantagens dessa estrutura:**
- Reutilização direta dos EJBs no cliente web (sem chamadas remotas, mais rápido).
- Separação clara de responsabilidades: persistência/negócio (EJB) vs. apresentação (Web).
- Escalabilidade: futuramente, o `E-Library.jar` pode ser exposto via EJB remoto para o cliente desktop (`E-LibraryDesktop.jar` — desenvolvido separadamente).

## 🔹 Estrutura do Projeto (Backend)

### 📁 Pacotes Principais
- `br.elibrary.model`

  Contém as entidades JPA e enums do domínio.

### 📄 Entidades Principais
| Entidade | Descrição | Relacionamentos |
|--------|-----------|-----------------|
| `User` | Representa usuários (alunos/professores), com credenciais e papel (comum/admin). | 1:N com `Loan` |
| `Book` | Informações bibliográficas de um título. | 1:N com `Copy`, N:M com `Category` |
| `Copy` | Exemplar físico de um livro, com status (DISPONIVEL/EMPRESTADO/RESERVADO). | N:1 com `Book`, 1:N com `Loan` |
| `Loan` | Registro de empréstimo, associando usuário, exemplar e datas. | N:1 com `User` e `Copy` |
| `Category` | Categorias temáticas dos livros. | N:M com `Book` |

### 📄 Enums

- `UserType`: `STUDENT`, `TEACHER`
- `Rules`: `COMMON_USER`, `ADMIN`
- `CopyStatus`: `AVAILABLE`, `BORROWED`, `RESERVED`
- `LoanStatus`: `ACTIVE`, `RETURNED`, `OVERDUE` 

### 📁 Serviços (Session Beans)

O backend utiliza três tipos de Session Beans, conforme exigido pelo enunciado:

#### 1. **Stateless Session Beans** (`@Stateless`) — Lógica de CRUD e Consulta
- **`BookSB`**: gerencia livros. Implementa CRUD completo + buscas avançadas com estatísticas (ex: `findByTitleOrAuthorWithStats`, `findUnavailableBooksWithStats`). Integra-se com o Singleton via callbacks (ex: `onBookCreated`).

- **`CopySB`**: gerencia exemplares. CRUD + buscas por livro/status. Atualiza o Singleton ao criar/remover exemplares.

- **`LoanSB`**: consultas avançadas via JPQL:
  - `findBorrowedCopiesByUser(userId)`: exemplares atualmente emprestados para um usuário.
  - `findBooksWithNoAvailableCopies()`: livros com zero exemplares disponíveis (fila de espera).
  - `findActiveLoansByUser(userId)`: empréstimos ativos do usuário.
  
- **`UserSB`**: CRUD de usuários com hashing de senha via BCrypt.

- **`CategorySB`**: CRUD de categorias (funcionalidade estendida).

#### ✅ `CatalogStatusSB` — `@Singleton`, `@Startup`
- Gerencia em memória (thread-safe):
  - `totalBooks`, `totalCopies`, `availableCopies`
- Contadores atualizados via:
  - Inicialização (`@PostConstruct` → `refreshCache()`)
  - Callbacks disparados pelos Stateless Beans (`onCopyCreated`, `onCopyStatusChanged`, etc.)
- Métodos de leitura com `@Lock(READ)`, escrita com `@Lock(WRITE)`
- Usa `AtomicInteger` para operações atômicas sem bloqueio explícito.

#### ✅ `UserSessionSB` — `@Stateful`

- Estado: `private User currentUser`
- `login(registration, password)`: busca usuário, valida senha com BCrypt, armazena sessão.

- `borrowCopy(copyId)`:
  1. Verifica autenticação e disponibilidade do exemplar
  2. Cria `Loan` com `issueDate = hoje`, `dueDate = +14 dias`
  3. Atualiza `Copy.status = BORROWED`
  4. Notifica `CatalogStatusSB`
  
- `returnCopy(copyId)`:
  1. Localiza empréstimo ativo
  2. Atualiza `returnDate`, `status = RETURNED`
  3. Libera exemplar (`status = AVAILABLE`)
  4. Notifica `CatalogStatusSB`
- `logout()` com `@Remove` → finaliza bean e libera sessão.

- `getActiveLoans()` delega para `LoanSB` → reuso de lógica.

## 🔹 Fase 2 — Aplicação Web (JSF + PrimeFaces)

### 🔐 Autenticação e Controle de Acesso

#### ✅ Managed Beans
- **`UserSessionBean`** (`@SessionScoped`): mantém o estado da sessão do usuário (instância de `User` e referência ao `UserSessionService` Stateful).
- **`LoginBean`** (`@RequestScoped`):
  - `doLogin()`: autentica via `UserSessionService.login()`, armazena usuário na sessão e redireciona para `admin/index.xhtml` ou `user/index.xhtml` com base em `Rules`.
  - `doLogout()`: chama `logout()` no Stateful Bean, limpa sessão e redireciona para `/login`.

#### ✅ Filtros de Segurança (Servlet Filters)
- **`AuthFilter`** (`@WebFilter({"/user/*", "/admin/*"})`):
  - Bloqueia acesso não autenticado → redireciona para `login.xhtml`.
- **`AdminFilter`** (`@WebFilter("/admin/*")`):
  - Permite acesso apenas a usuários com `Rules.ADMIN` → caso contrário, redireciona para `access-denied.xhtml`.

#### ✅ Integração com EJB Stateful
- O `LoginBean` injeta `@EJB UserSessionService` (proxy para `UserSessionSB`).
- Após login, o `UserSessionBean` mantém a referência ao Stateful Bean — permitindo que outros beans (ex: `BorrowBean`) chamem `borrowCopy()` diretamente, preservando o estado da sessão.

> ✅ **Conformidade**: Atende integralmente o item 5 do enunciado: login, dashboard por perfil, navegação segura, injeção de EJBs.

