[← Voltar ao README principal](../README.md)

# 🏗️ Arquitetura e Camada de Dados

## 🧩 O Papel Estratégico do `e-library-client`

O módulo `e-library-client` é o **contrato público e imutável** do sistema. Trata-se de uma biblioteca pura em Java SE (sem dependências de Jakarta EE, JPA ou servidor de aplicação), projetada para ser compartilhada com todos os clientes externos.

Ele contém:
- Interfaces remotas anotadas com `@Remote`, usadas pelo cliente Java SE (`E-LibrarySE`);
- DTOs (Data Transfer Objects) — POJOs simples, serializáveis, sem lógica de negócio;
- Enums de domínio (ex: `CopyStatus`, `LoanStatus`);
- Exceções customizadas (ex: `BusinessException`)

Esse contrato permite que módulos externos (SE, Consumer, até testes) compilam e executam sem depender do WildFly ou do EAR — garantindo **desacoplamento binário** e **estabilidade de integração**.

### 🔄 Entity ↔ DTO: Por que a separação é obrigatória?

A conversão explícita entre entidades JPA e DTOs é feita via *mappers* (ex: `BookMapper`, `LoanMapper`). Essa camada de adaptação garante:

- **Segurança**: dados sensíveis (como senhas, histórico interno) nunca saem do Core. Apenas campos necessários à integração são expostos.
- **Performance**: evita problemas clássicos como `LazyInitializationException`, já que os DTOs são planos e carregados de forma controlada nos EJBs.
- **Estabilidade**: clientes não dependem de anotações JPA (`@OneToMany`, `@JoinColumn`) ou de ciclos de vida de entidades — apenas de uma estrutura de dados estável e serializável.
- **Padrão de arquitetura**: respeita o princípio **Clean Architecture**, onde o domínio (Core) é protegido por fronteiras explícitas (`Client` como porta de saída, `API` como porta de entrada).

> ✅ **Nota importante**: a API REST (`E-LibraryAPI`) também utiliza esses mesmos DTOs como contrato de entrada/saída — o que garante consistência entre REST, GraphQL e cliente remoto.

## 🧠 Estratégia de EJBs: Tipos e Responsabilidades

O sistema faz uso intencional dos três tipos de Session Beans:

- **`@Singleton` — `CatalogStatusSB`**  
  Responsável pelo *cache em memória* de métricas agregadas (ex: contagem de livros por status). Garante **consistência eventual** com o banco (atualização após operações de empréstimo/devolução) e **alta performance** no dashboard — evitando consultas `COUNT(*)` repetidas.

- **`@Stateful` — `UserSessionSB`**  
  Representa uma sessão conversacional com o usuário (ex: login no terminal SE). Mantém o estado do usuário autenticado e o *carrinho de empréstimos* durante a interação — essencial para fluxos multi-etapa, como seleção de múltiplos exemplares antes do checkout.

- **`@Stateless` — `BookSB`, `CopySB`, `LoanSB`**  
  Serviços transacionais sem estado, utilizados por todas as camadas (Web, REST, GraphQL, Importação). Encapsulam regras de negócio críticas:  
  • validação de ISBN único;  
  • transições válidas de status (`DISPONIVEL → RESERVADO → EMPRESTADO → DISPONIVEL`);  
  • verificação de integridade antes de exclusões (ex: livro com exemplares emprestados não pode ser removido).

Todos os EJBs seguem o princípio: **não expõem `EntityManager`**. O acesso ao banco é feito exclusivamente via métodos bem definidos, orquestrados por transações JTA (`@TransactionAttribute`).

[← Voltar ao README principal](../README.md)