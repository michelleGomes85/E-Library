package br.elibrary.stateless;

import java.util.List;
import java.util.stream.Collectors;

import br.elibrary.dto.CategoryDTO;
import br.elibrary.mapper.CategoryMapper;
import br.elibrary.model.Category;
import br.elibrary.service.CategoryService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class CategorySB implements CategoryService {

	@PersistenceContext
	private EntityManager em;

	@Override
	public CategoryDTO create(CategoryDTO dto) {

		if (dto == null || dto.getName() == null || dto.getName().trim().isEmpty())
			throw new IllegalArgumentException("Nome da categoria é obrigatório.");

		Category entity = CategoryMapper.toEntity(dto);

		em.persist(entity);

		return CategoryMapper.toDTO(entity);
	}

	@Override
	public CategoryDTO update(CategoryDTO dto) {

		if (dto == null || dto.getId() == null)
			throw new IllegalArgumentException("ID da categoria é obrigatório para atualização.");

		Category existing = em.find(Category.class, dto.getId());
		if (existing == null)
			throw new IllegalArgumentException("Categoria não encontrada.");

		existing.setName(dto.getName());

		return CategoryMapper.toDTO(existing);
	}

	@Override
	public void delete(CategoryDTO dto) {
		if (dto != null && dto.getId() != null) {
			deleteById(dto.getId());
		}
	}

	@Override
	public void deleteById(Long id) {
		Category category = em.find(Category.class, id);
		if (category != null)
			em.remove(category);
	}

	@Override
	public CategoryDTO findById(Long id) {
		Category category = em.find(Category.class, id);
		return CategoryMapper.toDTO(category);
	}

	@Override
	public List<CategoryDTO> findAll() {

		List<Category> categories = 
				em.createQuery("SELECT c FROM Category c ORDER BY c.name", Category.class)
				.getResultList();

		return categories.stream().map(CategoryMapper::toDTO).collect(Collectors.toList());
	}
}
