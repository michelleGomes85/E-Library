# 📚 E-Library — Ecossistema de Gestão de Biblioteca Distribuída

> **Projeto Académico para a disciplina de Serviços de Suporte a Aplicações Distribuídas (SSAD)** > Arquitetura Híbrida: **Jakarta EE 10 (WildFly 31)** + **Spring Boot 3 (GraphQL)**

---

## 📑 Índice de Documentação Detalhada

Para facilitar a compreensão técnica de cada camada, a documentação foi dividida em módulos específicos:

1. [🏗️ Arquitetura e Contratos (Core & Client)](docs/ARCHITECTURE.md) - Justificativa de DTOs e EJBs.
2. [🌐 Módulo Web (JSF)](docs/WEB.md) - Interface administrativa e filtros de segurança.
3. [💻 Cliente Java SE](docs/SE.md) - Acesso remoto via JNDI/RMI.
4. [📡 API RESTful](docs/API.md) - A camada de exposição JAX-RS para integração.
5. [📊 Gateway GraphQL (Spring)](docs/GRAPHQL.md) - Camada de agregação moderna (BFF).

---

## 📌 Visão Geral

O **E-Library** é um sistema distribuído robusto que demonstra a integração entre o ecossistema corporativo clássico (Jakarta EE) e padrões modernos de consumo de dados (GraphQL). O sistema gere livros, exemplares, utilizadores e empréstimos, garantindo a integridade dos dados através de transações distribuídas e alta performance via cache em memória.

O sistema opera através de um **Enterprise Archive (EAR)** central, que é o ponto único de verdade, consumido por três frentes:
1. **Web (JSF):** Gestão administrativa.
2. **Desktop (Java SE):** Operações de balcão via chamadas remotas.
3. **Gateway (Spring Boot):** Agregador de serviços para interfaces modernas.

---

## 📂 Organização do Projeto (Maven Multi-Module)

O projeto está estruturado sob um **POM Pai** que gere o ciclo de vida de todos os módulos, garantindo que a biblioteca de contratos (`Client`) seja compilada antes dos consumidores.

### 🏗️ O Projeto EAR (Enterprise Archive)

Os módulos abaixo são empacotados juntos para deploy no WildFly:

* **`E-Library` (PAI):** Contém o `pom.xml` raiz que coordena as versões e a ordem de build.
* **`E-LibraryClient`:** O "Contrato". Contém Interfaces Remotas, DTOs e Enums. É uma dependência obrigatória para todos os outros módulos.
* **`E-LibraryCore`:** O "Coração". Contém as Entidades JPA e a implementação dos Session Beans (`@Stateless`, `@Stateful`, `@Singleton`).
* **`E-LibraryWeb`:** A interface **JSF**. Consome os EJBs localmente para a administração do sistema.
* **`E-LibraryAPI`:** A camada **JAX-RS**. Expõe a lógica de negócio do Core como serviços REST (JSON).
* **`E-LibraryEAR`:** O projeto de empacotamento que gera o ficheiro `.ear` final contendo todos os módulos acima.

---

## 💻 Consumidores Externos

Estes projetos operam de forma independente do servidor de aplicações, mas dependem dos serviços expostos pelo EAR:

### 1. E-LibrarySE (Cliente Remoto)
* **Tecnologia:** Java SE puro.
* **Comunicação:** Utiliza o protocolo nativo do WildFly (JNDI/RMI).
* **Dependência:** Usa o `E-LibraryClient.jar` para aceder às interfaces remotas.
* **Objetivo:** Simular um terminal de balcão que executa métodos no servidor como se fossem locais.

### 2. E-LibraryGraphQL (Agregador Spring Boot)
* **Tecnologia:** Spring Boot 3 + Spring For GraphQL.
* **Comunicação:** REST (JSON) consumindo a `E-LibraryAPI`.
* **Papel Estratégico:** Este módulo **não substitui** a API existente. Ele **agrega** valor, servindo como uma camada de orquestração que unifica diversos serviços REST num único endpoint GraphQL, otimizando a experiência do front-end e evitando tráfego desnecessário de dados (*overfetching*).

---

## ⚙️ Setup e Deploy

### 📥 Pré-requisitos
* **Java 17+**
* **WildFly 31.0.1.Final**
* **PostgreSQL 14+**

---

### 🔧 Passo 1: Configurar o WildFly

1. Execute:
   ```bash
   cd wildfly-31.0.1.Final/bin
   ./add-user.sh 
   ```

2. Crie usuário Management:
  - Username: ssad
  - Password: ssad
  - Confirme tudo com yes.

- Acesse http://localhost:9990 para confirmar.

---

### 🗃️ Passo 2: Criar o Banco (PostgreSQL)

- Crie via linha de comando:
  ```bash
  CREATE USER aluno WITH PASSWORD 'aluno';
  CREATE DATABASE elibrary OWNER elibrary ENCODING 'UTF8';
  \c elibrary
  \i elibrary_dump.sql # para popular com dados
  ```
  - Ou pela interface no `pgadmin`

---

### 🔌 Passo 3: Configurar o DataSource E-LibraryCoreDS

#### Instale o driver PostgreSQL no WildFly
  1. Entrando na interface por: http://localhost:9990, faça login com o usuário criado antes
  2. Vá em `Deployments` opção `+` e `upload deployment` e selecione o arquivo `.jar` do driver do postgres

#### DashSource 
  1. Vá em `Subsystems > Datasources & Driver > Datasources` e `add Datasource`:
  2. Crie o DataSource:
      - **Name:** `E-LibraryCoreDS`
      - **JNDI Name:** `java:/E-LibraryCoreDS`
      - **Driver:** `postgresql` # criado antes
      - **Connection URL:** `jdbc:postgresql://localhost:5432/elibrary`
      - **Username/Password:** `aluno` / `aluno`
      - Clique em `Test Connection ✅`
---

### 🚀 Passo 4: Build e Deploy do EAR

Na raiz do projeto pai:

```bash
mvn clean install
```
- Faça o mesmo em cada projeto em sequência. 
- Por ultimo o EAR onde vai criar o arquivo `e-library.ear`
- Copie para a pasta de `deploy` do wildfly

```bash
cp ear/target/e-library.ear $WILDFLY_HOME/standalone/deployments/
```

## 📁 Recursos do Projeto

| Recurso | Arquivo/Localização | Descrição |
|---------|---------------------|-----------|
| 📄 **Relatório técnico** | [`relatorio-tecnico.pdf`](relatorio_tecnico.pdf) | Justificativa do uso de `@Singleton`, `@Stateful` e `@Stateless`. |
| 💾 **Backup do banco** | [`elibrary_dump.sql`](elibrary_dump.sql) | Script de criação e *seed* do banco PostgreSQL (com usuários, livros, exemplares e empréstimos de exemplo). |

