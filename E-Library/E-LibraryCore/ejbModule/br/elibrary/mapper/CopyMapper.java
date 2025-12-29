package br.elibrary.mapper;

import br.elibrary.dto.CopyDTO;
import br.elibrary.model.Book;
import br.elibrary.model.Copy;
import jakarta.persistence.EntityManager;

public class CopyMapper {

    public static CopyDTO toDTO(Copy copy) {
        if (copy == null) return null;

        CopyDTO dto = new CopyDTO();
        dto.setId(copy.getId());
        dto.setInternalCode(copy.getInternalCode());
        dto.setStatus(copy.getStatus());

        if (copy.getBook() != null) {
            dto.setBookId(copy.getBook().getId());
            dto.setBookTitle(copy.getBook().getTitle());
            dto.setBookIsbn(copy.getBook().getIsbn());
        }

        return dto;
    }

    public static Copy toEntity(CopyDTO dto, EntityManager em) {
        if (dto == null) return null;

        Copy copy = new Copy();
        copy.setId(dto.getId());
        copy.setInternalCode(dto.getInternalCode());
        copy.setStatus(dto.getStatus());

        if (dto.getBookId() != null) {
            Book book = em.getReference(Book.class, dto.getBookId());
            copy.setBook(book);
        } 

        return copy;
    }
}