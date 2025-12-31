[← Voltar ao README principal](../README.md)

# 📬 Mensageria Assíncrona (JMS) — Notificações Desacopladas

O sistema implementa **notificações assíncronas** via **JMS (Jakarta Messaging)** para dois cenários críticos:  
1. **Disponibilidade de livros** para usuários em lista de espera;  
2. **Empréstimos em atraso**.  

Essas notificações são **totalmente desacopladas** da lógica principal de empréstimo e devolução — o Core apenas *publica mensagens*, e um serviço externo as *consome*. Nenhum fluxo de negócio é bloqueado ou alterado pela mensageria.

---

## 🧩 Arquitetura de Componentes

### 1. `E-LibraryEvents` — O Contrato de Eventos  
Biblioteca compartilhada (JAR) que define os **eventos padronizados** usados na comunicação JMS:  
- `BookAvailableEvent` (para `LIVRO_DISPONIVEL`)  
- `LoanOverdueEvent` (para `EMPRESTIMO_EM_ATRASO`)  

Contém apenas campos serializáveis (String, Long, LocalDateTime) — **sem entidades JPA, sem dependências do Core**.  
É usada tanto pelo produtor (Core) quanto pelo consumidor (`E-LibraryNotificationConsumer`).

---

### 2. Produtor (no `E-LibraryCore`)

A publicação ocorre **após o commit da transação principal**, garantindo consistência:

#### ✅ Disponibilidade (`LIVRO_DISPONIVEL`)
- **Gatilho**: alteração de status de um exemplar para `DISPONIVEL`.  
- **Condições obrigatórias**:  
  1. Antes da alteração, **nenhum exemplar** do livro estava disponível;  
  2. Existe **pelo menos um usuário** inscrito na lista de espera para esse livro.  
- **Ação**:  
  ```java
  jmsContext.createProducer().send(livraryNotificationQueue, event);
  ```
    > Local: dentro do método `onCopyStatusChanged` do `CatalogStatusSB`, após qualquer mudança de status.

#### ✅ Atraso (`EMPRESTIMO_EM_ATRASO`)

- **Gatilho**: identificação de empréstimo com `loan.getDueDate().isBefore(today)`.  

- **Como é detectado**:  
  - Durante consultas ativas (ex: `findActiveLoansByUser`);  
  - Ou via método `verifyDelayAutomatic()` (marcado com `@Schedule` todo dia a meia-noite).  

- **Ação**:  
  ```java
    jmsContext.createProducer().send(livraryNotificationQueue, event);
  ```
    > Nenhuma exceção JMS quebra a transação principal — publicação é "fire-and-forget". Erros são logados, mas não impedem o empréstimo/devolução.

### 3. 📥 Consumidor (E-LibraryNotificationConsumer)

Aplicação Jakarta EE (MDB + JSF) implantada fora do EAR principal, mas no mesmo WildFly (com perfil `standalone-full.xml`).

1. **Message-Driven Bean (MDB)**

    - Anotado com **@MessageDriven** e vinculado à fila `java:/jms/queue/libraryNotificationQueue`;
    
    - Desserializa mensagens com base em `BookAvailableEvent/LoanOverdueEvent`;
    
    - Ações:
        - Persiste notificação em tabela notificacao_historico (campos: tipo, conteudo_json, data_hora);
        - Registra no log;
        - Dispara envio de e-mail (veja EMAIL.md).

2. **Interface Web (JSF/PrimeFaces)**

    - Página `/notificacoes.xhtml` com <p:dataTable> listando histórico:
        - Tipo (LIVRO_DISPONIVEL / EMPRESTIMO_EM_ATRASO)
        - Conteúdo (ex: ISBN, título, dias em atraso)
        - Data/hora

## 📦 Estrutura da Mensagem (Serialização JSON)

As mensagens são serializadas como JSON via ObjectMapper (Jackson), garantindo legibilidade e interoperabilidade:

#### 1. LIVRO_DISPONIVEL

```json
{
  "tipoEvento": "LIVRO_DISPONIVEL",
  "livroId": 142,
  "isbn": "9780132350884",
  "titulo": "Clean Code",
  "dataHora": "2026-01-01T10:30:00"
}
```

#### 2. EMPRESTIMO_EM_ATRASO

```json
{
  "tipoEvento": "EMPRESTIMO_EM_ATRASO",
  "usuarioId": 101,
  "exemplarId": 2801,
  "dataPrevDevolucao": "2025-12-20",
  "diasEmAtraso": 12
}
```

[← Voltar ao README principal](../README.md)