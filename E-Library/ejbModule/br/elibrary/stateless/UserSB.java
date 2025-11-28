package br.elibrary.stateless;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import br.elibrary.model.User;
import br.elibrary.model.enuns.Rules;
import br.elibrary.model.service.UserService;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
@Remote(UserService.class)
public class UserSB implements UserService {
	
    @PersistenceContext(unitName = "E-Library")
    private EntityManager em;
    
	@Override
	public User create(User user) {
		
		if (findByRegistration(user.getRegistration()) != null) {
            throw new IllegalArgumentException("Matrícula já cadastrada: " + user.getRegistration());
        }
		
		String hashed = BCrypt.hashpw(user.getPasswordHash(), BCrypt.gensalt());
        user.setPasswordHash(hashed);
        
        if (user.getRules() == null)
        	user.setRules(Rules.COMMON_USER);
		
		em.persist(user);
		
		return user;
	}

	@Override
	public User update(User user) {
		
	    User existing = em.find(User.class, user.getId());
	    if (existing == null) {
	        throw new IllegalArgumentException("Usuário não encontrado para atualização.");
	    }

	    existing.setName(user.getName());
	    existing.setEmail(user.getEmail());
	    existing.setRegistration(user.getRegistration());
	    existing.setType(user.getType());

	    String incomingPassword = user.getPasswordHash();
	    if (incomingPassword != null && !incomingPassword.trim().isEmpty()) {
	    	
	        if (incomingPassword.length() < 6) {
	            throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.");
	        }
	        
	        String hashed = BCrypt.hashpw(incomingPassword, BCrypt.gensalt());
	        existing.setPasswordHash(hashed);
	    }

	    existing.setRules(user.getRules());

	    return em.merge(existing);
	}

	@Override
	public void delete(User user) {
		em.remove(em.merge(user));
	}
	
    @Override
    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u ORDER BY u.registration", User.class).getResultList();
    }

    @Override
    public User findByRegistration(String registration) {
        TypedQuery<User> query = em.createQuery(
            "SELECT u FROM User u WHERE u.registration = :reg", User.class);
        
        query.setParameter("reg", registration);
        return query.getResultStream().findFirst().orElse(null);
    }

    @Override
    public User findById(Long id) {
        return em.find(User.class, id);
    }

}
