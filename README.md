# 📚 E-Library — Sistema de Gerenciamento de Biblioteca Distribuída

Sistema distribuído de gerenciamento de biblioteca desenvolvido para o campus, com arquitetura cliente-servidor baseada em **EJB (Jakarta EE)**. A lógica de negócio, regras e persistência estão centralizadas em um **Servidor de Aplicação**, enquanto clientes Web (JSF) e Desktop (Java SE) consomem os serviços remotamente.

## 🧱 Fase 1 — Backend (Lógica de Negócio e Persistência)

### ✅ Estrutura do Projeto
- Projeto **EAR** contendo:
  - Módulo **EJB**: entidades, session beans (`@Stateless`, `@Stateful`, `@Singleton`)
  - Módulo **Web**: aplicação JSF (Managed Beans + páginas `.xhtml`)
- Banco de dados: **ostgreSQL** com script de seed incluso.
- Código-fonte versionado no GitHub com commits semânticos.

---

### 📦 Modelo de Dados (JPA Entities)

| Entidade      | Atributos                                                                                     | Relacionamentos                                                                                                 |
|---------------|-----------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| `User`        | `id`, `name`, `registration`, `email`, `passwordHash`, `type` (`UserType`), `rules` (`Rules`) | `@OneToMany` → `Loan` (cascade = `REMOVE`)                                                                     |
| `Book`        | `id`, `isbn`, `title`, `author`, `publisher`, `year`                                          | `@OneToMany` → `Copy` (cascade = `ALL` + `orphanRemoval`)<br>`@ManyToMany` ↔ `Category`                         |
| `Category`    | `id`, `name`                                                                                  | `@ManyToMany` ↔ `Book`                                                                                          |
| `Copy`        | `id`, `internalCode`, `status` (`CopyStatus`)                                                 | `@ManyToOne` → `Book`<br>`@OneToMany` → `Loan` (cascade = `REMOVE`)                                            |
| `Loan`        | `id`, `issueDate`, `dueDate`, `returnDate`, `status` (`LoanStatus`)                           | `@ManyToOne` → `User`<br>`@ManyToOne` → `Copy`                                                                 |

---

### 🏷️ Enumerações (`br.elibrary.model.enuns`)

| Enumeração       | Valores (`name()` → `label`)                                        | Uso em Entidade(s)             |
|------------------|----------------------------------------------------------------------|--------------------------------|
| `UserType`       | `STUDENT` → `"Estudante"`<br>`TEACHER` → `"Professor"`              | `User.type`                    |
| `Rules`          | `COMMON_USER` → `"Usuário Comum"`<br>`ADMIN` → `"Administrador"`    | `User.rules`                   |
| `CopyStatus`     | `AVAILABLE` → `"DISPONIVEL"`<br>`BORROWED` → `"EMPRESTADO"`<br>`RESERVED` → `"RESERVADO"` | `Copy.status`                  |
| `LoanStatus`     | `ACTIVE` → `"Ativo"`<br>`RETURNED` → `"Devolvido"`<br>`OVERDUE` → `"Atrasado"` | `Loan.status`                  |

> 🔹 Todos os enums usam `@Enumerated(EnumType.STRING)` para armazenamento legível.
> 🔹 Sobrescrita de `toString()` para exibição amigável em interfaces.

---

### ⚙️ Session Beans — Detalhamento por Classe

#### 📘 `BookSB` (`@Stateless`, `@Remote(BookService.class)`)

Gerencia operações transacionais de livros com integração ao cache global (`CatalogStatusSB`).

| Método | Parâmetros | Retorno | Comportamento |
|--------|------------|---------|---------------|
| `create` | `Book book` | `Book` | Persiste o livro e notifica `CatalogStatusSB.onBookCreated()` |
| `update` | `Book book` | `Book` | Mescla alterações (`em.merge`) |
| `delete` | `Book book` | `void` | Carrega o livro com suas cópias, conta total e disponíveis, remove o livro (e cópias em cascata), notifica `CatalogStatusSB.onBookDeleted(total, available)` |
| `findById` | `Long id` | `Book` | Busca por ID (`em.find`) |
| `findAll` | — | `List<Book>` | Lista todos os livros, ordenados por título |
| `findByTitle` | `String title` | `List<Book>` | Busca case-insensitive com `%LIKE%`, ordenado por título |
| `findBooksWithCopyStats` | — | `List<Object[]>` | JPQL com `GROUP BY`: retorna `(Book, totalCopies, availableCopies)` para dashboards |
| `findFirstAvailableCopy` | `Long bookId` | `Copy` | Busca primeira cópia com `status = AVAILABLE`; retorna `null` se indisponível |

---

#### 📄 `CopySB` (`@Stateless`, `@Remote(CopyService.class)`)
Controla o ciclo de vida dos exemplares com atualização em tempo real do estoque.

| Método | Parâmetros | Retorno | Comportamento |
|--------|------------|---------|---------------|
| `create` | `Copy copy` | `Copy` | Persiste e chama `CatalogStatusSB.onCopyCreated()` |
| `update` | `Copy copy` | `Copy` | Mescla alterações |
| `delete` | `Copy copy` | `void` | Obtém status do exemplar gerenciado, remove e notifica `CatalogStatusSB.onCopyDeleted(status)` |
| `deleteById` | `Long id` | `void` | Busca por ID, remove e notifica com base no status real |
| `findById` | `Long id` | `Copy` | Busca por ID |
| `findAll` | — | `List<Copy>` | Ordenado por `internalCode` |
| `findByBookId` | `Long bookId` | `List<Copy>` | Lista todas as cópias de um livro |
| `findByStatus` | `CopyStatus status` | `List<Copy>` | Filtra por status (`AVAILABLE`, `BORROWED`, `RESERVED`) |
| `findAvailableCopiesByBookId` | `Long bookId` | `List<Copy>` | Filtra cópias **disponíveis** de um livro (usado em frontend e empréstimos) |

---

#### 🏷️ `CategorySB` (`@Stateless`)
Gerencia categorias de livros, suportando o relacionamento `N:M`.

| Método | Parâmetros | Retorno | Comportamento |
|--------|------------|---------|---------------|
| `create` | `Category category` | `Category` | Persiste categoria |
| `update` | `Category category` | `Category` | Mescla alterações |
| `delete` | `Category category` | `void` | Remove categoria (livros não são afetados — `mappedBy` sem `cascade`) |
| `findById` | `Long id` | `Category` | Busca por ID |
| `findAll` | — | `List<Category>` | Ordenado alfabeticamente por `name` |

---

#### 📊 `LoanSB` (`@Stateless`)
Especializado em consultas avançadas com JPQL — sem operações de escrita.

| Método | Parâmetros | Retorno | Comportamento |
|--------|------------|---------|---------------|
| `findBorrowedCopiesByUser` | `Long userId` | `List<Copy>` | Retorna cópias com empréstimo ativo (`status = ACTIVE`) para o usuário |
| `findBooksWithNoAvailableCopies` | — | `List<Book>` | Livros onde **nenhuma cópia está disponível** (todas emprestadas/reservadas ou sem cópias) — via subquery com `COUNT = 0` |
| `findActiveLoansByUser` | `Long userId` | `List<Loan>` | Lista empréstimos ativos do usuário |
| `findActiveLoanByCopyId` | `Long copyId` | `Loan` | Localiza empréstimo ativo associado a uma cópia (usado em `returnCopy`) |

---

#### 👤 `UserSB` (`@Stateless`, `@Remote(UserService.class)`)
CRUD de usuários com segurança e validações.

| Método | Parâmetros | Retorno | Comportamento |
|--------|------------|---------|---------------|
| `create` | `User user` | `User` | Valida matrícula única; faz hash da senha com `BCrypt`; persiste |
| `update` | `User user` | `User` | Atualiza dados; se senha informada (não vazia), valida ≥6 chars e gera novo hash |
| `delete` | `User user` | `void` | Remove usuário (empréstimos são excluídos em cascata) |
| `findAll` | — | `List<User>` | Ordenado por `registration` |
| `findByRegistration` | `String registration` | `User` | Busca por matrícula (usado em `login`) |
| `findById` | `Long id` | `User` | Busca por ID |

---

#### 🔐 `UserSessionSB` (`@Stateful`)
Gerencia sessão de usuário com estado (`currentUser`) e operações de empréstimo.

| Método | Parâmetros | Retorno | Comportamento |
|--------|------------|---------|---------------|
| `login` | `String registration`, `String passwordPlain` | `boolean` | Busca por matrícula; valida senha com `BCrypt.checkpw`; guarda `currentUser` |
| `getLoggedInUser` | — | `User` | Retorna `currentUser` ou `null` |
| `logout` | — | `void` | Anotado com `@Remove` → bean destruído pelo contêiner |
| `borrowCopy` | `Long copyId` | `boolean` | Verifica `status == AVAILABLE`; cria `Loan`; atualiza cópia para `BORROWED`; notifica `CatalogStatusSB.onCopyStatusChanged(AVAILABLE → BORROWED)` |
| `returnCopy` | `Long copyId` | `boolean` | Verifica `status == BORROWED`; localiza `Loan` ativo; finaliza empréstimo; atualiza cópia para `AVAILABLE`; notifica `onCopyStatusChanged(BORROWED → AVAILABLE)` |
| `getActiveLoans` | — | `List<Loan>` | Delega para `LoanSB.findActiveLoansByUser(currentUser.id)` |

---

#### 📈 `CatalogStatusSB` (`@Singleton`, `@Startup`, `@ConcurrencyManagement(CONTAINER)`)
Cache global thread-safe de métricas do acervo.

| Método | Parâmetros | Retorno | Comportamento |
|--------|------------|---------|---------------|
| `getTotalBooks` | — | `int` | Leitura atômica (`@Lock(READ)`) |
| `getTotalCopies` | — | `int` | Leitura atômica |
| `getAvailableCopies` | — | `int` | Leitura atômica |
| `refreshCache` | — | `void` | Recalcula os 3 contadores via JPQL (usado em `@PostConstruct`) |
| `onBookCreated` | — | `void` | `totalBooks++` (`@Lock(WRITE)`) |
| `onCopyCreated` | — | `void` | `totalCopies++`, `availableCopies++` |
| `onCopyStatusChanged` | `CopyStatus old`, `CopyStatus new` | `void` | Ajusta `availableCopies` na transição DISPONÍVEL ⇄ OUTRO |
| `onCopyDeleted` | `CopyStatus status` | `void` | `totalCopies--`; se `status == AVAILABLE`, `availableCopies--` |
| `onBookDeleted` | `int totalCopies`, `int availableCopies` | `void` | `totalBooks--`, `totalCopies -= n`, `availableCopies -= m` |
