[← Voltar ao README principal](../README.md)

# 💻 Cliente Java SE Remoto — Balcão de Atendimento

O projeto `e-library-client-se` demonstra a versatilidade de um sistema distribuído, permitindo que uma aplicação desktop pura (Java Standard Edition) execute operações complexas no servidor remoto.

## 🔌 Comunicação RMI sobre HTTP
Diferente do projeto Spring que usa REST, este cliente utiliza o protocolo de **remoting do WildFly**.

### O Processo de Lookup JNDI:
Para acessar um serviço, o cliente realiza uma busca no diretório JNDI do servidor:
```java
String jndiName = "ejb:e-library/e-library-ejb/BookSB!br.elibrary.service.BookService";
BookService service = (BookService) context.lookup(jndiName);
```

[← Voltar ao README principal](../README.md)