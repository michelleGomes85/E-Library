package br.elibrary.graphql.dto;

public record LoanDTO(
    Long id,
    String loanDate,
    String expectedReturnDate,
    String returnDate,
    CopyDTO copy
) {}