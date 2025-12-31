[← Voltar ao README principal](../README.md)

# 📧 Envio de E-mails por Notificação

Este módulo implementa uma funcionalidade de **envio de e-mails**, ativada exclusivamente pela interface web de notificações. 

- Não altera a lógica de negócio do Core;  
- Não modifica as mensagens JMS (`BookAvailabilityEvent`, `LoanOverdueEvent`);  
- Não acessa dados sensíveis do usuário (o e-mail é digitado manualmente pelo administrador);  
- Respeita o princípio de desacoplamento — o envio ocorre **após o consumo da mensagem**, no `E-LibraryNotificationConsumer`.

> ⚠️ **Importante**: O sistema **nunca lê e-mails de usuários do banco de dados**. O destinatário é informado **manualmente pelo administrador** na interface, garantindo conformidade com LGPD e segurança da informação.

---

## 🧩 Arquitetura da Funcionalidade

```
[Interface Web JSF]
        ↓
[NotificationBean] → [EmailService] → SMTP (Gmail, Mailtrap, etc.)
        ↑
[Evento JMS (após persistência)]
```

- As notificações são recebidas via JMS e registradas em memória (`@Singleton NotificationStore`);

- O administrador acessa `/notifications.xhtml`, digita um e-mail e clica em **"Enviar"**;

- O `EmailService` utiliza **Jakarta Mail** para enviar a mensagem;

- **Nenhum dado é persistido** — o e-mail existe apenas durante a interação do usuário.

---

## 🔐 Configuração de Credenciais (Segura e Simples)

As credenciais de SMTP (ex: usuário e senha do Gmail) são carregadas de um arquivo **externo ao código-fonte**, localizado na raiz do projeto:


### 📄 Conteúdo de `local-config.properties`
```properties
mail.user=email que vai enviar
mail.password=abcdefghij123456  # ← senha de app do Google (16 dígitos)
```

## 🛠️ Como Funciona por Dentro

### Configuração

O **EmailService** tenta carregar **local-config.properties** do classpath — ou seja, de **src/main/resources/**:

### Envio com Jakarta Mail

Usa-se a API padrão **(jakarta.mail)** com SMTP do Gmail (ou outro provedor):

```java
props.put("mail.smtp.host", "smtp.gmail.com");
props.put("mail.smtp.port", "587");
props.put("mail.smtp.auth", "true");
props.put("mail.smtp.starttls.enable", "true");
```

## 🧪 Como Testar Localmente

### 1. Crie o arquivo `local-config.properties` na raiz do projeto 
`E-LibraryNotificationConsumer/`

```bash
mail.user=seu.email@gmail.com
mail.password=sua-senha-de-app-de-16-digitos
```

> 📌 Gere a Senha de App em:
> 🔗 https://myaccount.google.com/apppasswords


### 2. Mande uma cópia para: `classpath`

```bash
cp local-config.properties src/main/resources/
```

### 3. Envie o email: 

    - Acesse http://localhost:8080/E-LibraryNotificationConsumer;
    - Digite um e-mail (ex: seu.email@gmail.com);
    - Clique em "Enviar";

[← Voltar ao README principal](../README.md)

