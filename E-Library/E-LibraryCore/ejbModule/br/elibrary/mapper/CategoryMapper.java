package br.elibrary.mapper;

import br.elibrary.dto.CategoryDTO;
import br.elibrary.model.Category;

public class CategoryMapper {

	public static CategoryDTO toDTO(Category entity) {
		if (entity == null)
			return null;
		
		CategoryDTO dto = new CategoryDTO();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		
		return dto;
	}

	public static Category toEntity(CategoryDTO dto) {
		
		if (dto == null)
			return null;
		
		Category entity = new Category();
		entity.setId(dto.getId());
		entity.setName(dto.getName());
		
		return entity;
	}
}