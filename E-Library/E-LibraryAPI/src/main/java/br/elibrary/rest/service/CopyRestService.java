package br.elibrary.rest.service;

import java.util.List;

import br.elibrary.dto.CopyDTO;
import br.elibrary.model.enuns.CopyStatus;
import br.elibrary.service.CopyService;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CopyRestService {

    @EJB
    private CopyService copySB;

    public CopyDTO create(Long bookId) {
    	
        CopyDTO dto = new CopyDTO();
        dto.setBookId(bookId);
		
        return copySB.create(dto);
    }

    public CopyDTO findById(Long id) {
        return copySB.findById(id);
    }

    public CopyDTO updateStatus(Long id, String newStatus) {
    	
        CopyStatus status = CopyStatus.valueOf(newStatus.toUpperCase());
        
        return copySB.updateStatus(id, status);
    }

    public List<CopyDTO> findByBookIdAndStatus(Long bookId, String status) {
        return copySB.findByBookIdAndStatus(bookId, status);
    }
}