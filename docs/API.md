[← Voltar ao README principal](../README.md)

# 📡 API RESTful (JAX-RS)

O módulo `E-LibraryAPI` é a camada de interoperabilidade do ecossistema. Ele expõe as regras de negócio processadas pelos EJBs no **WildFly** através de uma interface **RESTful**, permitindo que sistemas modernos como o **Spring Boot** consumam os dados via JSON.

## 🚀 Integração com o Ecossistema
Enquanto o cliente Java SE utiliza o protocolo nativo RMI (mais pesado), esta API utiliza **HTTP/JSON**, o que garante:
- **Leveza:** Ideal para o tráfego entre servidores (WildFly ↔ Spring).
- **Padronização:** Facilita a expansão para futuros clientes mobile ou front-ends em React/Angular.

---

## 📍 Endpoints Disponíveis

Abaixo estão os recursos expostos e a lógica que executam no servidor:

- Acesso começando como `http://localhost:8080/E-LibraryAPI/api/` para cada endpoint: 

### 📚 Catálogo de Livros (`/livros`)

| Método | Endpoint | Descrição | Parâmetros / Observações |
|:--- |:--- |:--- |:--- |
| `POST` | `/livros` | **Cadastrar Livro** | Recebe `titulo`, `autor`, `isbn`, `editora` e `ano` via Form. |
| `GET` | `/livros/{id}` | **Buscar por ID** | Retorna o JSON do livro ou 404. |
| `PUT` | `/livros/{id}` | **Atualizar Livro** | Atualiza os dados do livro identificado pelo ID. |
| `GET` | `/livros` | **Listar Todos** | Filtros opcionais: `autor` e `categoria` (Query Param). |
| `GET` | `/livros/isbn/{isbn}` | **Buscar por ISBN** | Busca detalhes específicos via código ISBN. |
| `GET` | `/livros/available` | **Filtrar Disponíveis** | Filtros: `author` e `status` (ex: AVAILABLE). |

### 📖 Gestão de Exemplares (Sub-recurso)

| Método | Endpoint | Descrição | Parâmetros / Observações |
|:--- |:--- |:--- |:--- |
| `POST` | `/livros/{bookId}/exemplares` | **Criar Exemplar** | Adiciona uma cópia física ao livro. |
| `GET` | `/livros/{bookId}/exemplares` | **Listar Exemplares** | Lista cópias de um livro. Filtro opcional: `status`. |

### 📋 Gestão Global de Exemplares (`/exemplares`)

| Método | Endpoint | Descrição | Parâmetros / Observações |
|:--- |:--- |:--- |:--- |
| `GET` | `/exemplares/{id}` | **Consultar Exemplar** | Retorna os detalhes de uma cópia específica (Status e Código). |
| `PUT` | `/exemplares/{id}/status` | **Atualizar Status** | Altera o estado do exemplar (ex: para BORROWED) |

### 📊 Gerenciamento e Dashboard
| Método | Endpoint | Descrição | Origem do Dado |
|:---|:---|:---|:---|
| `GET` | `/biblioteca/dashboard` | Retorna o status consolidado (Total de livros, cópias, disponíveis, reservados e emprestados). | **CatalogStatusSB** (Singleton Cache) |
| `GET` | `/biblioteca/emprestimo/usuario/{id}` | Lista todos os empréstimos ativos de um usuário específico. | **LoanSB** (Stateless) |

---

## 🛠️ Detalhes Técnicos de Implementação

### 1. Injeção de Dependência (CDI & EJB)
Os Resources JAX-RS não processam lógica de banco de dados. Eles injetam as interfaces do `E-LibraryClient` e delegam a execução para os EJBs:
```java
@Inject
private BookService bookService; 
```

### 2. Produção de Dados (JSON-B)

A API utiliza o **Jakarta JSON Binding** para converter automaticamente os **DTOs** retornados pelos EJBs em strings JSON. Isso garante que as anotações de persistência (@Entity) nunca sejam expostas, enviando apenas dados puros e serializáveis.

### 3. Tratamento de Erros

A camada API captura exceções de negócio vindas do Core (ex: Livro não encontrado ou Exemplar já emprestado) e as traduz em códigos de status HTTP apropriados (`404 Not Found`, `400 Bad Request`, `500 Internal Server Error`).