package br.elibrary.graphql.dto;

public record LoanDTO(
    Long id,
    String issueDate,
    String dueDate,
    String returnDate,
    String status,
    String bookTitle,
    Long copyId,
    String copyInternalCode
) {}
