package br.elibrary.service;

import br.elibrary.dto.DashboardDTO;
import br.elibrary.model.enuns.CopyStatus;

public interface CatalogStatusService {

    int getTotalBooks();

    int getTotalCopies();

    int getAvailableCopies();

    void refreshCache();

    void onCopyCreated();

    void onCopyStatusChanged(CopyStatus oldStatus, CopyStatus newStatus);
    
    void onBookCreated();
    
    void onCopyDeleted();                    
    
    void onCopyDeleted(CopyStatus status);  
    
    void onBookDeleted(int totalCopies, int availableCopies);
    
	DashboardDTO getFullDashboard();
}