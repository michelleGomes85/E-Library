package br.elibrary.rest.service;

import java.util.List;

import br.elibrary.dto.DashboardDTO;
import br.elibrary.dto.LoanDTO;
import br.elibrary.service.CatalogStatusService;
import br.elibrary.service.LoanService;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LibraryRestService {
	
    @EJB
    private LoanService loanService;
    
    @EJB
    private CatalogStatusService catalogStatusService;
    
    public List<LoanDTO> findActiveLoansByUser(Long userId) {
    	return loanService.findActiveLoansByUser(userId);
    }
    
    public DashboardDTO getLibraryDashboard() {
    	return catalogStatusService.getFullDashboard(); 
    }
}
