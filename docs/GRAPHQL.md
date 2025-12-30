[← Voltar ao README principal](../README.md)

# 📊 Módulo GraphQL (Spring Boot)

## 🎯 Objetivo e Justificativa

Este projeto foi desenvolvido utilizando **Spring Boot 3** e **Spring For GraphQL**. 

> **Conforme as diretrizes do projeto:** Esta camada **não substitui** a API Jakarta EE. Ela atua agregando valor à mesma, servindo como uma camada de orquestração 
(**BFF - Backend For Frontend**).

### 🔄 Funcionamento da Integração

O Spring não acessa o banco de dados diretamente. Ele utiliza um `RestClient` para consumir a **API REST do WildFly**.

**Vantagens desta Abordagem:**

1. **Unificação:** O cliente front-end pode pedir dados de Livros e do Dashboard em uma única requisição GraphQL.

2. **Desacoplamento:** Se a lógica interna do EJB mudar, desde que o contrato (DTO) seja mantido, o GraphQL continua funcionando sem alterações.

3. **Flexibilidade:** Permite que o front-end escolha exatamente quais campos deseja receber, reduzindo o tráfego de rede (Overfetching).

----

## 🚀 Como Executar

1.  **Classe Principal:** Localize e execute a classe `LibraryGraphqlApplication.java` dentro do projeto `E-LibraryGraphQL`.

2.  **Interface de Teste (Playground):** Com o projeto a correr, aceda ao **GraphiQL** através do navegador no endereço:

    > `http://localhost:8081/graphiql`

----

## 🧪 Exemplos de Teste

Abaixo estão os modelos de operações disponíveis. Substitua os valores entre `< >` pelos dados reais do seu teste.

### 1. Cadastrar Novo Livro

```graphql
mutation {
  createBook(book: {
    isbn: "<ISBN_AQUI>",
    title: "<TITULO_DO_LIVRO>",
    author: "<NOME_DO_AUTOR>",
    publicationYear: <ANO_INTEIRO>
  }) {
    id
    title
    isbn
  }
}
```

### 2. Alterar Status de um Exemplar

```graphql
mutation {
  updateCopyStatus(copyId: <ID_DO_EXEMPLAR>, status: "<STATUS_DESEJADO>") {
    id
    internalCode
    status
  }
}
```

> Dica: Use status como AVAILABLE, BORROWED ou RESERVED.

### 3. Buscar Livro por ISBN

```graphql
query {
  bookByIsbn(isbn: "<ISBN_PARA_BUSCA>") {
    title
    author
    isbn
    copies {
      status
      internalCode
    }
  }
}
```

### 4. Listar Livros (Com ou Sem Filtro)

- Sem filtro 

```graphql
query {
  availableBooks {
    title
    author
    copies {
      internalCode
      status
    }
  }
}
```
- Filtrando por Autor e Status:

```graphql
query {
  availableBooks(filter: { author: "<PARTE_DO_NOME>", status: "AVAILABLE" }) {
    title
    author
  }
}
```

### 5. Listar Empréstimos Ativos de um Utilizador

```graphql
query {
  activeLoansByUser(userId: <ID_DO_UTILIZADOR>) {
    id
    bookTitle
    copyCode
    loanDate
    dueDate
    status
  }
}
```

### 6. Consultar Dashboard (Estado Geral da Biblioteca)

```graphql
query {
  libraryDashboard {
    totalBooks
    totalCopies
    totalAvailable
    totalReserved
    totalBorrowed
  }
}
```


[← Voltar ao README principal](../README.md)