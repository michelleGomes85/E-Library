package br.elibrary.graphql.dto;

import java.util.List;

public record BookDTO(
    Long id, 
    String isbn, 
    String title, 
    String author, 
    List<CopyDTO> copies
) {}
