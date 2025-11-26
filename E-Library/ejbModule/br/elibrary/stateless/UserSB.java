package br.elibrary.stateless;

import org.mindrot.jbcrypt.BCrypt;

import br.elibrary.model.User;
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
		
		em.persist(user);
		return user;
	}

	@Override
	public User update(User user) {
		em.merge(user);
		return user;
	}

	@Override
	public void delete(User user) {
		em.remove(em.merge(user));
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
