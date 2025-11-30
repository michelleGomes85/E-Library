package br.elibrary.mapper;

import br.elibrary.dto.LoanDTO;
import br.elibrary.model.Copy;
import br.elibrary.model.Loan;
import br.elibrary.model.User;
import jakarta.persistence.EntityManager;

public class LoanMapper {

	public static LoanDTO toDTO(Loan loan) {
		if (loan == null)
			return null;

		LoanDTO dto = new LoanDTO();
		dto.setId(loan.getId());
		dto.setStatus(loan.getStatus());
		dto.setIssueDate(loan.getIssueDate());
		dto.setDueDate(loan.getDueDate());
		dto.setReturnDate(loan.getReturnDate());

		if (loan.getCopy() != null) {
			dto.setCopyId(loan.getCopy().getId());
			dto.setCopyInternalCode(loan.getCopy().getInternalCode());
			if (loan.getCopy().getBook() != null) {
				dto.setBookTitle(loan.getCopy().getBook().getTitle());
			}
		}

		if (loan.getUser() != null) {
			dto.setUserId(loan.getUser().getId());
			dto.setUserName(loan.getUser().getName());
		}

		return dto;
	}

	public static Loan toEntity(LoanDTO dto, EntityManager em) {

		if (dto == null)
			return null;

		Loan loan = new Loan();
		loan.setId(dto.getId());
		loan.setStatus(dto.getStatus());
		loan.setIssueDate(dto.getIssueDate());
		loan.setDueDate(dto.getDueDate());
		loan.setReturnDate(dto.getReturnDate());

		if (dto.getCopyId() != null) {
			Copy copy = em.getReference(Copy.class, dto.getCopyId());
			loan.setCopy(copy);
		}

		if (dto.getUserId() != null) {
			User user = em.getReference(User.class, dto.getUserId());
			loan.setUser(user);
		}

		return loan;
	}
}