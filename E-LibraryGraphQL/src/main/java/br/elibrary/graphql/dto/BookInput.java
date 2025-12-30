package br.elibrary.graphql.dto;

public record BookInput(
    String isbn, 
    String title, 
    String author, 
    String publisher, 
    Integer publicationYear
) {}