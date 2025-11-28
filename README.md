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


