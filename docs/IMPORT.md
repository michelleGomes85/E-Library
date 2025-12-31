[← Voltar ao README principal](../README.md)

# 📦 Módulo de Importação de Doações — Web + REST

O projeto `e-library-import-web` é uma **aplicação web JSF/PrimeFaces autônoma**, separada do EAR, responsável por processar doações de livros recebidas de editoras parceiras em **formato XML ou JSON**.

É uma interface dedicada, que permite o upload de arquivos e o processamento em lote, **sem duplicar lógica de negócio** e **sem acoplar-se ao Core**.

---

## 🔄 Arquitetura de Integração

O módulo **não faz parte do EAR** e **não depende de classes internas do Core**. Sua única integração com o sistema central é via **chamadas HTTP à `E-LibraryAPI`**:

1. O usuário faz upload de um arquivo (`*.xml` ou `*.json`) via formulário `<p:fileUpload>`.

2. O `ImportBean` (Managed Bean) valida o tipo de arquivo e o conteúdo.

3. Cada item do arquivo é convertido para um `BookDTO` (do contrato 
`e-library-client`).

4. O `RestClient` do módulo chama:
   - `POST /api/livros` para cada livro novo (ou tentativa de cadastro);
   - `POST /api/livros/{id}/exemplares` para cada exemplar solicitado.

5. Com base nas respostas HTTP (`201`, `409`, `400`), o sistema classifica cada registro.

📌 **Nenhum EJB é injetado. Nenhum `EntityManager` é usado. Toda regra de negócio permanece no EAR.**

---

## 📄 Formatos Suportados

### XML (Exemplo)
```xml
<biblioteca>
  <livro>
    <isbn>9780132350884</isbn>
    <titulo>Clean Code</titulo>
    <autor>Robert C. Martin</autor>
    <editora>Prentice Hall</editora>
    <anoPublicacao>2008</anoPublicacao>
    <quantidadeExemplares>2</quantidadeExemplares>
  </livro>
</biblioteca>
```

### JSON (Exemplo)

```json
{
  "livros": [
    {
      "isbn": "9780134685991",
      "titulo": "Effective Java",
      "autor": "Joshua Bloch",
      "editora": "Addison-Wesley",
      "anoPublicacao": 2018,
      "quantidadeExemplares": 3
    }
  ]
}
```

## 📊 Fluxo de Processamento

Para cada livro no arquivo:

1. Tentativa de cadastro:

    - `POST /api/livros` com dados do livro.
        - Se resposta 201 Created: livro novo cadastrado.
        - Se resposta 409 Conflict (ISBN duplicado): livro já existe

2. Criação de exemplares

    - Para cada unidade em quantidadeExemplares:
        - `POST /api/livros/{livroId}/exemplares`
        (status inicial DISPONIVEL é garantido pelo Core).

## 📋 Relatório de Importação

Ao final do processamento, o sistema exibe um relatório em tempo real na interface web, com detalhes por item:

```bash 
✅ Clean Code (ISBN: 9780132350884)
   → Livro novo cadastrado (ID: 142)
   → 2 exemplares criados (Códigos: C-2801, C-2802)

ℹ️ Effective Java (ISBN: 9780134685991)
   → Livro já existente (ID: 87)
   → 3 exemplares criados (Códigos: C-2803, C-2804, C-2805)

❌ Livro Inválido (ISBN: XYZ)
   → Erro: ISBN inválido (400 Bad Request)
```

O relatório é exibido em uma <p:dataTable> com ícones visuais (✅, ℹ️, ❌) e pode ser exportado como excel via <p:dataExporter>.

## 🛡️ Controle de Acesso e Segurança
    - Arquivos temporários são apagados após processamento;
    - Não há persistência local — tudo é feito em memória durante a requisição.

[← Voltar ao README principal](../README.md)