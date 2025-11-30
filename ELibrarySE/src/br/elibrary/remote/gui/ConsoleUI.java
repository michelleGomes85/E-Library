package br.elibrary.remote.gui;

import java.util.List;
import java.util.Scanner;

import br.elibrary.dto.BookDTO;
import br.elibrary.dto.CopyDTO;
import br.elibrary.dto.UserDTO;
import br.elibrary.model.enuns.CopyStatus;
import br.elibrary.remote.service.RemoteServiceLocator;
import br.elibrary.service.BookService;
import br.elibrary.service.CatalogStatusService;
import br.elibrary.service.CopyService;
import br.elibrary.service.UserSessionService;

public class ConsoleUI {

    private final Scanner scanner = new Scanner(System.in);
    private UserSessionService session;
    private BookService bookService;
    private CopyService copyService;
    private CatalogStatusService catalogService;

    public void start() {
        System.out.println("=====================================");
        System.out.println("      E-Library — Balcão de Atendimento");
        System.out.println("=====================================");

        initializeServices();
        loginLoop();

        if (session != null) {
            showMenu();
        }

        scanner.close();
    }

    private void initializeServices() {
        try {
            bookService = RemoteServiceLocator.getBookService();
            copyService = RemoteServiceLocator.getCopyService();
            catalogService = RemoteServiceLocator.getCatalogStatusService();
        } catch (Exception e) {
            System.err.println("Falha ao inicializar serviços: " + e.getMessage());
            System.exit(1);
        }
    }

    private void loginLoop() {
    	
        while (true) {
            System.out.print("Matrícula: ");
            String matricula = scanner.nextLine().trim();

            if (matricula.equalsIgnoreCase("sair")) {
                System.out.println("Até logo!");
                System.exit(0);
            }

            System.out.print("Senha: ");
            String senha = scanner.nextLine().trim();

            if (senha.equalsIgnoreCase("sair")) {
                System.out.println("Até logo!");
                System.exit(0);
            }

            try {
                session = RemoteServiceLocator.getUserSessionService();
                boolean ok = session.login(matricula, senha);

                if (ok) {
                    UserDTO user = session.getLoggedInUser();
                    System.out.println("\nBem-vindo, " + user.getName() + "!");
                    return;
                } else {
                    System.out.println("\nMatrícula ou senha inválidos. Tente novamente.");
                    System.out.println("   (Digite 'sair' em matrícula ou senha para encerrar)\n");
                }
            } catch (Exception e) {
                System.err.println("Erro no login: " + e.getMessage());
            }
        }
    }

    private void showMenu() {
    	
        while (true) {
            System.out.println("\n================ MENU ================");
            System.out.println("1. Status da Biblioteca");
            System.out.println("2. Cadastrar Novo Livro");
            System.out.println("3. Cadastrar Novo Exemplar");
            System.out.println("4. Sair");
            System.out.print("Escolha uma opção: ");
            String op = scanner.nextLine().trim();

            switch (op) {
                case "1":
                    showStatus();
                    break;
                case "2":
                    registerBook();
                    break;
                case "3":
                    registerCopy();
                    break;
                case "4":
                    logout();
                    return;
                default:
                    System.out.println("Opção inválida. Escolha 1, 2, 3 ou 4.");
            }
        }
    }

    private void showStatus() {
        try {
            System.out.println("\n --- Status da Biblioteca ---");
            System.out.println("   • Livros cadastrados: " + catalogService.getTotalBooks());
            System.out.println("   • Cópias totais: " + catalogService.getTotalCopies());
            System.out.println("   • Cópias disponíveis: " + catalogService.getAvailableCopies());
        } catch (Exception e) {
            System.err.println("Erro ao buscar status: " + e.getMessage());
        }
    }

    private void registerBook() {
        try {
            System.out.println("\n --- Cadastrar Novo Livro ---");
            System.out.print("Título: ");
            String title = scanner.nextLine().trim();

            System.out.print("Autor: ");
            String author = scanner.nextLine().trim();

            System.out.print("Editora: ");
            String publisher = scanner.nextLine().trim();

            System.out.print("Ano (ex: 2023): ");
            Integer year = Integer.valueOf(scanner.nextLine().trim());

            System.out.print("ISBN (opcional): ");
            String isbn = scanner.nextLine().trim();

            BookDTO book = new BookDTO();
            book.setTitle(title);
            book.setAuthor(author);
            book.setPublisher(publisher);
            book.setYear(year);
            if (!isbn.isEmpty()) book.setIsbn(isbn);

            book = bookService.create(book);
            System.out.println("Livro cadastrado com sucesso! ID: " + book.getId());

        } catch (NumberFormatException e) {
            System.out.println("Ano inválido. Use um número (ex: 2023).");
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar livro: " + e.getMessage());
        }
    }

    private void registerCopy() {
        try {
            System.out.println("\n --- Cadastrar Novo Exemplar ---");

            List<BookDTO> books = bookService.findAll();
            if (books.isEmpty()) {
                System.out.println(" Nenhum livro cadastrado. Cadastre um livro primeiro.");
                return;
            }

            System.out.println(" Livros disponíveis:");
            
            System.out.printf("%-5s %-30s %-20s%n", "ID", "Título", "ISBN");
            
            System.out.println("----------------------------------------------");
            
            for (BookDTO b : books) {
                String isbn = (b.getIsbn() != null && !b.getIsbn().isEmpty()) ? b.getIsbn() : "–";
                System.out.printf("%-5d %-30.30s %-20s%n", b.getId(), b.getTitle(), isbn);
            }

            System.out.print("\nSelecione o ID do livro: ");
            Long bookId = Long.valueOf(scanner.nextLine().trim());

            BookDTO selectedBook = null;
            for (BookDTO b : books) {
                if (b.getId().equals(bookId)) {
                    selectedBook = b;
                    break;
                }
            }

            if (selectedBook == null) {
                System.out.println("Livro com ID " + bookId + " não encontrado.");
                return;
            }

            System.out.print("Código interno do exemplar (ex: CP-001): ");
            String internalCode = scanner.nextLine().trim();
            if (internalCode.isEmpty()) {
                System.out.println("Código interno é obrigatório.");
                return;
            }

            // Escolher status
            System.out.println("\nSelecione o status inicial do exemplar:");
            System.out.println("1. DISPONÍVEL");
            System.out.println("2. EMPRESTADO");
            System.out.println("3. RESERVADO");
            System.out.print("Opção (1-3): ");
            int statusOption = Integer.parseInt(scanner.nextLine().trim());
            CopyStatus status;
            switch (statusOption) {
                case 1: status = CopyStatus.AVAILABLE; break;
                case 2: status = CopyStatus.BORROWED; break;
                case 3: status = CopyStatus.RESERVED; break;
                default:
                    System.out.println("Opção inválida. Usando status padrão: DISPONÍVEL.");
                    status = CopyStatus.AVAILABLE;
            }

            CopyDTO copy = new CopyDTO();
            copy.setInternalCode(internalCode);
            copy.setBookId(bookId);
            copy.setStatus(status);

            copy = copyService.create(copy);
            System.out.println("Exemplar cadastrado com sucesso! ID: " + copy.getId());

        } catch (NumberFormatException e) {
            System.out.println("ID ou opção inválida. Use apenas números.");
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar exemplar: " + e.getMessage());
        }
    }

    private void logout() {
        try {
            if (session != null) {
                session.logout();
            }
            System.out.println("\nSessão encerrada. Até logo!");
        } catch (Exception e) {
            System.err.println("Erro ao sair: " + e.getMessage());
        }
    }
}