[← Voltar ao README principal](../README.md)

# 🌐 Módulo Web — JSF + PrimeFaces

O módulo `e-library-web` é a interface principal do sistema, desenvolvida com **JSF 3.0** e **PrimeFaces 12+**. Ela foi projetada para ser o painel administrativo e de autoatendimento do usuário via navegador.

## 🧩 Camada de Managed Beans
A lógica de apresentação é isolada em beans gerenciados que se comunicam com o back-end via injeção de dependência (`@EJB`).

- **DashboardBean (`@RequestScoped`):** Consome o Singleton `CatalogStatusSB` para exibir as métricas em tempo real no topo da página.

- **BookBean (`@ViewScoped`):** Gerencia o ciclo de vida do CRUD de livros, garantindo que o estado da edição seja mantido durante as interações com modais do PrimeFaces.

- **LoginBean (`@SessionScoped`):** Interage com o `UserSessionSB` (Stateful) para manter a identidade do usuário durante toda a navegação.

## 🔐 Segurança e Controle de Acesso

A segurança não é baseada apenas em esconder botões, mas sim em **Filtros de Servlet** que interceptam as requisições:

| Filtro | Responsabilidade |
|--------|------------------|
| **`AuthFilter`** | Verifica se existe um `UserDTO` na sessão. Caso contrário, redireciona para o login. |
| **`AdminFilter`** | Verifica se o usuário logado possui a Role `ADMIN`. Impede que usuários comuns acessem `/admin/*`. |

## 🔁 Converters (Otimização de UI)
O uso do `BookConverter` é fundamental para a experiência do usuário. Ele permite que o componente `<p:selectOneMenu>` trabalhe com objetos `BookDTO` completos:
1. **No envio:** Transforma o objeto em seu ID (String).
2. **No retorno:** Recebe o ID e busca o DTO atualizado no serviço, garantindo a integridade referencial.

---

## 🎨 Componentes Principais

- **Templates Faclets:** Uso de `ui:composition` para manter um cabeçalho e rodapé únicos em todo o sistema.

- **DataTables:** Listagens com ordenação e paginação via AJAX, consumindo métodos otimizados dos EJBs Stateless.

[← Voltar ao README principal](../README.md)