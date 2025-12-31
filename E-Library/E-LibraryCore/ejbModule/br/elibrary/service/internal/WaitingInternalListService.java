package br.elibrary.service.internal;

import java.util.List;

import br.elibrary.model.WaitingList;
import jakarta.ejb.Local;

@Local
public interface WaitingInternalListService {


    void remove(Long userId, Long bookId);

    void removeByBookId(Long bookId);
    
    void removeAllFromWaitingList(Long bookId);
    
    List<WaitingList> findUsersWaitingForBook(Long bookId);
}