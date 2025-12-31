[← Voltar ao README principal](../README.md)

# 💻 Cliente Java SE Remoto — Terminal de Balcão

O projeto `e-library-se` é uma aplicação **Java SE pura (console)** que simula um terminal físico de atendimento presencial em uma biblioteca — como um computador no balcão de empréstimos. Ele demonstra o uso clássico de **EJB remoto via JNDI/RMI**, mantendo baixo acoplamento graças ao contrato compartilhado (`e-library-client`).

É o **único cliente que acessa diretamente os EJBs do Core**, e o faz exclusivamente por meio de interfaces anotadas com `@Remote`, garantindo que:

- A lógica de negócio permaneça centralizada no servidor;
- O cliente não tenha dependência de JPA, Jakarta EE ou servidor de aplicação;
- A comunicação seja tipada, segura e verificada em tempo de compilação.

## 🔌 Comunicação via JNDI + EJB Remoto

O cliente conecta-se ao WildFly utilizando o protocolo **HTTP Upgrade para Remoting (http-remoting)** — padrão do WildFly 31 — sem necessidade de configuração adicional no servidor (desde que o usuário de management esteja criado).

### Fluxo de Lookup

1. O cliente carrega as propriedades de conexão (normalmente de `jndi.properties` ou variáveis de ambiente):

   ```properties
   java.naming.factory.initial=org.wildfly.naming.client.WildFlyInitialContextFactory
   java.naming.provider.url=http-remoting://localhost:8080
   ```

2. Realiza o lookup JNDI usando o nome global dos EJBs remotos:

    ```properties
    InitialContext ctx = new InitialContext();
    UserSessionService userSession = (UserSessionService) 
    ctx.lookup("ejb:e-library/e-library-core/UserSessionSB!br.elibrary.service.UserSessionService");
    ```

> 📌 O nome segue o padrão do WildFly: 
    ```properties
        ejb:<ear-name>/<ejb-module>/<bean-simple-name>!<fully-qualified-remote-interface>
    ```

3. Métodos são invocados como se fossem locais — com marshalling automático de parâmetros e retorno via serialização Java (os DTOs são Serializable).

## 🎯 Casos de Uso Implementados

- **Login de operador:** autenticação com UserSessionSB.login(username, password), que retorna um UserDTO e inicia uma sessão stateful no servidor.
    - Cadastro de novo livro e/ou exemplar
    - Visualização geral do estado da biblioteca    

## 📦 Dependências Mínimas

- O cliente depende apenas de:
    - **e-library-client.jar** (contrato: interfaces, DTOs, enums);
    - **wildfly-naming-client** (para o InitialContext);
    - **wildfly-ejb-client-bom** (opcional, para configuração avançada de conexão).

    > Nenhuma dependência de JPA, Jakarta Faces, REST ou servidor está presente — o que permite executá-lo até em ambientes leves (ex: Raspberry Pi no balcão da biblioteca).

[← Voltar ao README principal](../README.md)