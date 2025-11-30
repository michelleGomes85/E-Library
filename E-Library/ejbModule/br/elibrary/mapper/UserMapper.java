package br.elibrary.mapper;

import br.elibrary.dto.UserDTO;
import br.elibrary.model.User;

public class UserMapper {

	public static UserDTO toDTO(User entity) {

		if (entity == null)
			return null;
		
		UserDTO dto = new UserDTO();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setRegistration(entity.getRegistration());
		dto.setEmail(entity.getEmail());
		dto.setType(entity.getType());
		dto.setRules(entity.getRules());
		
		return dto;
	}

	public static User toEntity(UserDTO dto) {
		
		if (dto == null)
			return null;
		
		User entity = new User();
		entity.setId(dto.getId());
		entity.setName(dto.getName());
		entity.setRegistration(dto.getRegistration());
		entity.setEmail(dto.getEmail());
		entity.setType(dto.getType());
		entity.setRules(dto.getRules());
		
		return entity;
	}
}