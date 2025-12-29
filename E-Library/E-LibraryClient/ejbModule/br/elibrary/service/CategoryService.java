package br.elibrary.service;

import java.util.List;

import br.elibrary.dto.CategoryDTO;

public interface CategoryService {
	
	CategoryDTO create(CategoryDTO category);

	CategoryDTO update(CategoryDTO category);

	void delete(CategoryDTO category);
	
	void deleteById(Long id);

	CategoryDTO findById(Long id);

	List<CategoryDTO> findAll();
}
