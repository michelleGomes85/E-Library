package br.elibrary.stateless;


import java.util.List;

import br.elibrary.model.WaitingList;
import br.elibrary.service.WaitingListService;
import br.elibrary.service.internal.WaitingInternalListService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
public class WaitingListSB implements WaitingInternalListService, WaitingListService {

    @PersistenceContext
    private EntityManager em;

    /**
     * Inscreve um usuário na lista de espera de um livro.
     * Evita duplicidade (mesmo user + mesmo book).
     */
    @Override
    public void subscribe(Long userId, Long bookId) {

        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(w) FROM WaitingList w " +
            "WHERE w.userId = :userId AND w.bookId = :bookId",
            Long.class
        );

        query.setParameter("userId", userId);
        query.setParameter("bookId", bookId);

        Long count = query.getSingleResult();

        if (count > 0) {
            return;
        }

        WaitingList waitingList = new WaitingList(userId, bookId);
        em.persist(waitingList);
    }

    /**
     * Retorna todos os usuários na lista de espera de um livro,
     * ordenados pela data de inscrição.
     */
    public List<WaitingList> findByBookId(Long bookId) {

        TypedQuery<WaitingList> query = em.createQuery(
            "SELECT w FROM WaitingList w " +
            "WHERE w.bookId = :bookId " +
            "ORDER BY w.subscriptionDate ASC",
            WaitingList.class
        );

        query.setParameter("bookId", bookId);

        return query.getResultList();
    }

    /**
     * Remove um usuário específico da lista de espera de um livro.
     */
    @Override
    public void remove(Long userId, Long bookId) {

        em.createQuery(
            "DELETE FROM WaitingList w " +
            "WHERE w.userId = :userId AND w.bookId = :bookId"
        )
        .setParameter("userId", userId)
        .setParameter("bookId", bookId)
        .executeUpdate();
    }

    /**
     * Remove todos os usuários da lista de espera de um livro.
     * 
     * Usado após o envio da notificação.
     */
    public void removeByBookId(Long bookId) {

        em.createQuery(
            "DELETE FROM WaitingList w WHERE w.bookId = :bookId"
        )
        .setParameter("bookId", bookId)
        .executeUpdate();
    }
    
    /**
     * Remove todos os usuários da lista de espera de um livro.
     * Usado após o envio da notificação.
     */
    @Override
    public void removeAllFromWaitingList(Long bookId) {

        em.createQuery(
            "DELETE FROM WaitingList w WHERE w.bookId = :bookId"
        )
        .setParameter("bookId", bookId)
        .executeUpdate();
    }
    
    /**
     * Retorna os usuários na lista de espera de um livro,
     * ordenados pela data de inscrição.
     */
    @Override
    public List<WaitingList> findUsersWaitingForBook(Long bookId) {

        TypedQuery<WaitingList> query = em.createQuery(
            "SELECT w FROM WaitingList w " +
            "WHERE w.bookId = :bookId " +
            "ORDER BY w.subscriptionDate ASC",
            WaitingList.class
        );

        query.setParameter("bookId", bookId);
        
        return query.getResultList();
    }
}

