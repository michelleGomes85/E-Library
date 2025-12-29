package br.elibrary.stateless;

import java.util.List;
import java.util.stream.Collectors;

import org.mindrot.jbcrypt.BCrypt;

import br.elibrary.dto.UserDTO;
import br.elibrary.mapper.UserMapper;
import br.elibrary.model.User;
import br.elibrary.model.enuns.Rules;
import br.elibrary.service.UserService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class UserSB implements UserService {

	@PersistenceContext(unitName = "E-Library")
	private EntityManager em;

	@Override
	public UserDTO create(UserDTO dto) {

		validateForCreate(dto);

		if (findByRegistration(dto.getRegistration()) != null)
			throw new IllegalArgumentException("Matrícula já cadastrada: " + dto.getRegistration());

		User entity = UserMapper.toEntity(dto);

		entity.setRules(Rules.COMMON_USER);

		if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {

			if (dto.getPassword().length() < 6)
				throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.");

			String hashed = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());
			entity.setPasswordHash(hashed);
		} else
			throw new IllegalArgumentException("Senha é obrigatória para novo usuário.");

		em.persist(entity);

		return UserMapper.toDTO(entity);
	}

	@Override
	public UserDTO update(UserDTO dto) {

		if (dto == null || dto.getId() == null)
			throw new IllegalArgumentException("ID do usuário é obrigatório.");

		User existing = em.find(User.class, dto.getId());
		if (existing == null)
			throw new IllegalArgumentException("Usuário não encontrado.");

		existing.setName(dto.getName());
		existing.setEmail(dto.getEmail());
		existing.setRegistration(dto.getRegistration());
		existing.setType(dto.getType());
		existing.setRules(dto.getRules());

		if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {

			if (dto.getPassword().length() < 6)
				throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.");

			String hashed = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());
			existing.setPasswordHash(hashed);
		}
		
		em.flush();          
	    em.refresh(existing);

		return UserMapper.toDTO(existing);
	}

	@Override
	public void delete(UserDTO dto) {
		if (dto != null && dto.getId() != null) {
			deleteById(dto.getId());
			em.flush();
		}
	}

	@Override
	public void deleteById(Long id) {
		User user = em.find(User.class, id);
		if (user != null) {
			em.remove(user);
		}
	}

	@Override
	public UserDTO findById(Long id) {
		User user = em.find(User.class, id);
		return UserMapper.toDTO(user);
	}

	@Override
	public List<UserDTO> findAll() {

		List<User> users = em.createQuery("SELECT u FROM User u ORDER BY u.registration", User.class).getResultList();

		return users.stream().map(UserMapper::toDTO).collect(Collectors.toList());
	}

	@Override
	public UserDTO findByRegistration(String registration) {

		User user = em.createQuery("SELECT u FROM User u WHERE u.registration = :reg", User.class)
				.setParameter("reg", registration).getResultStream().findFirst().orElse(null);

		return UserMapper.toDTO(user);
	}

	private void validateForCreate(UserDTO dto) {
		
		if (dto == null)
			throw new IllegalArgumentException("Dados do usuário são obrigatórios.");
		
		if (dto.getName() == null || dto.getName().isBlank())
			throw new IllegalArgumentException("Nome é obrigatório.");
		
		if (dto.getRegistration() == null || dto.getRegistration().isBlank())
			throw new IllegalArgumentException("Matrícula é obrigatória.");
		
		if (dto.getEmail() == null || dto.getEmail().isBlank())
			throw new IllegalArgumentException("Email é obrigatório.");
		
		if (dto.getType() == null)
			throw new IllegalArgumentException("Tipo de usuário é obrigatório.");
	}

}
