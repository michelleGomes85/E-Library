package br.elibrary.service;

import java.util.List;

import br.elibrary.model.Copy;
import br.elibrary.model.Loan;

public interface LoanService {

    List<Copy> findBorrowedCopiesByUser(Long userId);

    List<Object[]> findBooksWithNoAvailableCopies();
    
    List<Loan> findActiveLoansByUser(Long userId);
    
    Loan findActiveLoanByCopyId(Long copyId);
}