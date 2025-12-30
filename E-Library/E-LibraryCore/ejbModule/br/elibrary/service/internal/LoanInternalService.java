package br.elibrary.service.internal;

import br.elibrary.model.Loan;
import jakarta.ejb.Local;

@Local
public interface LoanInternalService {

    Loan findActiveLoanByCopyIdEntity(Long copyId);
}
