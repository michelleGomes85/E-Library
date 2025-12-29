package br.elibrary.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import br.elibrary.dto.BookDTO;
import br.elibrary.model.Book;
import br.elibrary.model.Category;
import jakarta.persistence.EntityManager;

public class BookMapper {

	public static BookDTO toDTO(Book book) {
		
		if (book == null)
			return null;

		BookDTO dto = new BookDTO();
		dto.setId(book.getId());
		dto.setIsbn(book.getIsbn());
		dto.setTitle(book.getTitle());
		dto.setAuthor(book.getAuthor());
		dto.setPublisher(book.getPublisher());
		dto.setYear(book.getYear());

		if (book.getCategories() != null) {
			dto.setCategoryIds(
					book.getCategories().stream()
					.filter(Objects::nonNull).map(Category::getId)
					.filter(Objects::nonNull).collect(Collectors.toList())
			);
			
			dto.setCategoryNameList(book.getCategories().stream()
					.filter(Objects::nonNull).map(Category::getName)
					.filter(Objects::nonNull).collect(Collectors.toList()));
		}

        if (book.getCopies() != null) {
            dto.setTotalCopies(book.getCopies().size());
            long available = book.getCopies().stream()
                .filter(copy -> copy.getStatus() != null && copy.getStatus().isAvailable())
                .count();
            dto.setAvailableCopies((int) available);
        }

		return dto;
	}

	public static Book toEntity(BookDTO dto, EntityManager em) {
		
		if (dto == null)
			return null;

		Book book = new Book();
		book.setId(dto.getId());
		book.setIsbn(dto.getIsbn());
		book.setTitle(dto.getTitle());
		book.setAuthor(dto.getAuthor());
		book.setPublisher(dto.getPublisher());
		book.setYear(dto.getYear());

		if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
			
			List<Category> managedCategories = 
					dto.getCategoryIds().stream().filter(Objects::nonNull)
					.map(id -> em.find(Category.class, id))
					.filter(Objects::nonNull).collect(Collectors.toList());
			
			book.setCategories(managedCategories);
			
		} else
			book.setCategories(new ArrayList<>());

		return book;
	}
	
	public static Book toEntitySimple(BookDTO dto) {
		
	    if (dto == null)
	        return null;

	    Book book = new Book();
	    book.setId(dto.getId());
	    book.setIsbn(dto.getIsbn());
	    book.setTitle(dto.getTitle());
	    book.setAuthor(dto.getAuthor());
	    book.setPublisher(dto.getPublisher());
	    book.setYear(dto.getYear());
	 
	    return book;
	}
}