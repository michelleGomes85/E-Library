package br.elibrary.model.service;

import java.util.List;

import br.elibrary.model.Copy;
import br.elibrary.model.enuns.CopyStatus;

public interface CopyService {
	
    Copy create(Copy copy);
    
    Copy update(Copy copy);
    
    void delete(Copy copy);
    
    void deleteById(Long id);
    
    Copy findById(Long id);
    
    List<Copy> findAll();
    
    List<Copy> findByBookId(Long bookId);
    
    List<Copy> findByStatus(CopyStatus status);
    
    List<Copy> findAvailableCopiesByBookId(Long bookId);
}
