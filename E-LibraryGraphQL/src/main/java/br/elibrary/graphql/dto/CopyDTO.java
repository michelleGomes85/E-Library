package br.elibrary.graphql.dto;

public record CopyDTO(
    Long id, 
    String internalCode, 
    String status
) {}