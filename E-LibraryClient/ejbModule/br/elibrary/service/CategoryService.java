package br.elibrary.service;

import java.util.List;

import br.elibrary.model.Category;

public interface CategoryService {
	
	Category create(Category category);

	Category update(Category category);

	void delete(Category category);

	Category findById(Long id);

	List<Category> findAll();
}
