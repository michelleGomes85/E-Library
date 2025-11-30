package br.elibrary.service;

import java.util.List;

import br.elibrary.dto.CopyDTO;
import br.elibrary.model.enuns.CopyStatus;

public interface CopyService {
	
    CopyDTO create(CopyDTO copy);
    
    CopyDTO update(CopyDTO copy);
    
    void delete(CopyDTO copy);
    
    void deleteById(Long id);
    
    CopyDTO findById(Long id);
    
    List<CopyDTO> findAll();
    
    List<CopyDTO> findByBookId(Long bookId);
    
    List<CopyDTO> findByStatus(CopyStatus status);
    
    List<CopyDTO> findAvailableCopiesByBookId(Long bookId);
}
