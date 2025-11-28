package br.elibrary.service;

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
}