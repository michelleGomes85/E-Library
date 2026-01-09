[← Voltar ao README principal](../README.md)

# 📡 API RESTful (JAX-RS)

O módulo `e-library-api` é a **camada de integração síncrona** do sistema. Sua única responsabilidade é **orquestrar** os EJBs já existentes no `E-LibraryCore`, expondo-os como serviços HTTP/JSON — **sem acesso direto ao `EntityManager`**, sem regras de negócio duplicadas e sem violar os contratos de domínio.

É o ponto de entrada oficial para todos os clientes modernos:  
→ `E-LibraryGraph` (GraphQL/BFF),  
→ `E-LibraryImportWeb` (importação de doações),  
→ e futuros frontends (mobile, React, etc.).

## 🚀 Integração com o Ecossistema

Enquanto o cliente Java SE utiliza o protocolo nativo RMI (mais pesado), esta API utiliza **HTTP/JSON**, o que garante:
- **Leveza:** Ideal para o tráfego entre servidores (WildFly ↔ Spring).
- **Padronização:** Facilita a expansão para futuros clientes mobile ou front-ends em React/Angular.

---

## 🧭 Princípios Arquiteturais

- **Orquestração, não reimplementação**:  
  Cada método de `Resource` delega 100% da lógica para os EJBs (`BookSB`, `CopySB`, `LoanSB`, `CatalogStatusSB`).  
  Exemplo: `PUT /exemplares/{id}/status` chama `copySB.updateStatus(id, newStatus)` — **quem valida a transição é o EJB**, não o REST.

- **DTOs como contrato imutável**:  
  Todos os dados de entrada/saída usam os mesmos DTOs do `e-library-client` (`BookDTO`, `CopyDTO`, `LoanDTO`). Isso garante consistência entre REST, GraphQL e cliente remoto.

- **Códigos HTTP semanticamente corretos**:  
  A API traduz exceções do contrato (`InvalidStatusTransitionException`) em respostas HTTP padronizadas.

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

## Teste `curl`

## Livros

#### Cadastrar Livro (POST)

```code
curl -i -X POST "http://localhost:8080/E-LibraryAPI/api/livros" -d "titulo=O Hobbit&autor=J.R.R. Tolkien&isbn=9780007525492&editora=HarperCollins&ano=1937"
````

### Buscar por ID (GET)

```code
curl -i -X GET "http://localhost:8080/E-LibraryAPI/api/livros/1"
```

### Atualizar Livro (PUT)

```code
curl -i -X PUT "http://localhost:8080/E-LibraryAPI/api/livros/1" -d "titulo=O Hobbit - Edicao Especial&autor=J.R.R. Tolkien&isbn=9780007525492&editora=HarperCollins&ano=2024"
```

#### Listar (GET)

##### Listar Todos

```code
curl -i -X GET "http://localhost:8080/E-LibraryAPI/api/livros"
```

##### Listar por Autor

```code
curl -i -X GET "http://localhost:8080/E-LibraryAPI/api/livros?autor=Tolkien"
```

##### Listar por Categoria

```code
curl -i -X GET "http://localhost:8080/E-LibraryAPI/api/livros?categoria=Fantasia"
```

#### Listar por Autor e categoria

```code
curl -i -X GET "http://localhost:8080/E-LibraryAPI/api/livros?autor=Tolkien&categoria=Fantasia"
```

## Exemplares

### Cadastrar exemplar para um livro (POST)

```code
curl -i -X POST "http://localhost:8080/E-LibraryAPI/api/livros/1/exemplares"
```

### Buscar Exemplar por ID (GET)

```code
curl -i -X GET "http://localhost:8080/E-LibraryAPI/api/exemplares/10"
```

### Atualizar Status de Exemplar (PUT)

```code
curl -i -X PUT "http://localhost:8080/E-LibraryAPI/api/exemplares/10/status" -d "status=BORROWED"
```

### Listar exemplares de um livro por status 

```code
curl -i -X GET "http://localhost:8080/E-LibraryAPI/api/livros/15/exemplares?status=RESERVED"
```

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

[← Voltar ao README principal](../README.md)
