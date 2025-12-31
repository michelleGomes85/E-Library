[← Voltar ao README principal](../README.md)

# 🌐 Módulo Web — JSF + PrimeFaces

O módulo `e-library-web` é a interface principal do sistema: uma aplicação web responsiva e interativa, construída com **Jakarta Faces 3.0 (JSF)** e **PrimeFaces 12+**, voltada tanto para operadores administrativos quanto para usuários finais (autoatendimento de empréstimos, lista de espera).

É implantado como parte do EAR e consome os EJBs do Core **exclusivamente via injeção local (`@EJB`)** — garantindo máxima performance e acesso transacional direto, sem passar pela camada REST.

## 🧠 Camada de Managed Beans

Os *Managed Beans* seguem rigorosamente os escopos do Jakarta Faces, alinhados ao ciclo de vida das interações do usuário:

- **`LoginBean` (`@SessionScoped`)**  
  Responsável pela autenticação e gerenciamento da sessão do usuário. Delega credenciais ao `UserSessionSB` (`@Stateful`), que mantém o estado do login e os dados do usuário durante toda a navegação. Após login bem-sucedido, armazena um `UserDTO` serializável na `HttpSession`, usado pelos filtros de segurança.

- **`BookBean` e `CopyBean` (`@ViewScoped`)**  
  Controlam operações de cadastro, edição e busca de livros e exemplares. O escopo `@ViewScoped` é essencial para manter o estado de formulários modais (ex: inclusão de categoria, edição em popup) sem perda de dados durante requisições AJAX.

- **`LoanBean` (`@ViewScoped`)**  
  Gerencia o fluxo de empréstimo: busca de usuário (via autocomplete), seleção de exemplares, validação de regras (ex: limite de empréstimos, atrasos pendentes) e confirmação. Utiliza o `LoanSB` (`@Stateless`) para processar a transação final.

- **`DashboardBean` (`@RequestScoped`)**  
  Obtém métricas em tempo real do `CatalogStatusSB` (`@Singleton`), como contagem de livros por status, empréstimos ativos e fila de espera. Como os dados são atualizados *in-memory* após cada operação transacional, o dashboard reflete o estado consistente do sistema sem sobrecarregar o banco.

## 🔐 Segurança por Filtros — Não por Frontend

A segurança é implementada de forma **defensiva e centralizada**, via *Servlet Filters*, não apenas por esconder botões no frontend:

- **`AuthFilter`**  
  Aplicado a todas as rotas exceto `/login.xhtml`. Verifica se existe um `UserDTO` na sessão HTTP. Caso ausente, redireciona para `/login.xhtml` com status `302`.

- **`AdminFilter`**  
  Aplicado a rotas sob `/admin/*`. Verifica se o `UserDTO` possui a role `"ADMIN"`. Caso contrário, responde com `403 Forbidden`.

Essa abordagem garante que mesmo requisições diretas (ex: via `curl` ou Postman) sejam bloqueadas — tornando a proteção independente da interface.

## 🔄 Converters — Integridade Referencial na UI

O `BookConverter` e o `UserConverter` são críticos para a usabilidade e consistência:

- Implementam `jakarta.faces.convert.Converter`;
- Em `getAsString()`: retornam o ID (ex: ISBN ou `userId`) como `String`;
- Em `getAsObject()`: recebem o ID e **invocam o serviço remoto** (ex: `BookSB.findById()`) para obter o DTO atualizado — evitando objetos *stale* ou desconectados.

Isso permite usar componentes como `<p:selectOneMenu>` com objetos completos, mantendo a integridade mesmo em cenários de longa duração de view.

## 🎨 Experiência do Usuário — Mais que Funcionalidade

- **Layout responsivo**: baseado em `primefaces.css` + `flex/grid`, com suporte a dispositivos móveis (ex: terminal de balcão em tablet).
- **Feedback visual**: mensagens de sucesso/erro via `<p:messages autoUpdate="true"/>`, e loading em operações assíncronas (`<p:ajaxStatus>`).
- **Reutilização**: template `main.xhtml` com `ui:insert` para conteúdo, cabeçalho com menu condicional (usuário comum vs admin), e rodapé com versão do sistema.
- **Validação no cliente e servidor**: uso de `required`, `size`, `f:validateRegex` + validações programáticas nos beans antes de chamar os EJBs.

> ✅ **Importante**: este módulo *não* acessa o `EntityManager`, DTOs não são entidades, e nenhuma lógica de negócio está duplicada — tudo é orquestrado pelos EJBs do `E-LibraryCore`.

[← Voltar ao README principal](../README.md)