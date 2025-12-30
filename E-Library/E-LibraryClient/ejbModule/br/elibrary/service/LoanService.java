package br.elibrary.service;

import java.util.List;

import br.elibrary.dto.BookDTO;
import br.elibrary.dto.CopyDTO;
import br.elibrary.dto.LoanDTO;
import jakarta.ejb.Local;

@Local
public interface LoanService {

    List<CopyDTO> findBorrowedCopiesByUser(Long userId);

    List<BookDTO> findBooksWithNoAvailableCopies();
    
    List<LoanDTO> findActiveLoansByUser(Long userId);
    
    LoanDTO findActiveLoanByCopyId(Long copyId);
    
    boolean hasActiveLoan(Long copyId);
}
