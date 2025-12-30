package br.elibrary.graphql.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import br.elibrary.graphql.dto.BookDTO;
import br.elibrary.graphql.dto.BookFilter;
import br.elibrary.graphql.dto.BookInput;
import br.elibrary.graphql.dto.CopyDTO;
import br.elibrary.graphql.dto.DashboardDTO;
import br.elibrary.graphql.dto.LoanDTO;
import br.elibrary.graphql.service.LibraryIntegrationService;

@Controller
public class LibraryGraphQLController {

    @Autowired
    private LibraryIntegrationService integrationService;

    @MutationMapping 
    public BookDTO createBook(@Argument BookInput book) {
        return integrationService.createBook(book);
    }
    
    @MutationMapping
    public CopyDTO updateCopyStatus(@Argument Long copyId, @Argument String status) {
        return integrationService.updateCopyStatus(copyId, status);
    }
    
    @QueryMapping
    public BookDTO bookByIsbn(@Argument String isbn) {
        return integrationService.getBookByIsbn(isbn);
    }
    
    @QueryMapping
    public List<BookDTO> availableBooks(@Argument BookFilter filter) {
        return integrationService.getAvailableBooks(filter);
    }
    
    @QueryMapping
    public List<LoanDTO> activeLoansByUser(@Argument Long userId) {
        return integrationService.getUserActiveLoans(userId);
    }
    
    @QueryMapping
    public DashboardDTO libraryDashboard() {
        return integrationService.getLibraryDashboard();
    }
}
