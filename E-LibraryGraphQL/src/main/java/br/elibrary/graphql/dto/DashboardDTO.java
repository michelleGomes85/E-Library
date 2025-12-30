package br.elibrary.graphql.dto;

public record DashboardDTO(
    Integer totalBooks,
    Integer totalCopies,
    Integer totalAvailable,
    Integer totalReserved,
    Integer totalBorrowed
) {}