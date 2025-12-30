[← Voltar ao README principal](../README.md)

# 🏗️ Arquitetura e Camada de Dados

## 🧩 O Papel Estratégico do `e-library-client`
Este módulo é o **Contrato Compartilhado**. Ele é uma biblioteca JAR pura (sem dependências Jakarta) que contém as interfaces e os DTOs.

### 🧠 Entity ↔ DTO: Por que separar?

O projeto utiliza Mappers explícitos para garantir que:

- **Segurança:** Senhas e dados internos não saiam do servidor.

- **Performance:** Evita o erro `LazyInitializationException` ao trafegar objetos que possuem relacionamentos com o banco de dados.

- **Estabilidade:** O cliente Java SE e o Spring Boot não precisam conhecer as anotações do JPA, apenas a estrutura de dados (POJO).

## 🧩 EJB Session Beans

| Tipo | Bean | Motivação |
|------|------|-----------|
| **`@Singleton`** | `CatalogStatusSB` | **Cache de Agregação**: Mantém contadores atômicos em memória para o Dashboard, evitando `COUNT(*)` constantes no banco. |
| **`@Stateful`** | `UserSessionSB` | **Sessão Conversacional**: Mantém o estado do usuário logado e seu carrinho de empréstimos durante a interação. |
| **`@Stateless`** | `BookSB`, `LoanSB` | **Escalabilidade**: Processamento de lógica sem estado, ideal para CRUDs e listagens. |

[← Voltar ao README principal](../README.md)