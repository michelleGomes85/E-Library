package br.elibrary.events;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Evento disparado quando um empréstimo
 * ultrapassa a data prevista de devolução.
 * 
 * tipoEvento = EMPRESTIMO_EM_ATRASO
 * usuarioId
 * exemplarId
 * dataPrevDevolucao
 * diasEmAtraso
 */
public class LoanOverdueEvent extends LibraryEvent {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private Long copyId;
    private LocalDate expectedReturnDate;
    private long overdueDays;
    private LocalDateTime createdAt;
    
    private String email;
    private boolean send;
    
    public LoanOverdueEvent(Long userId,
                            Long copyId,
                            LocalDate expectedReturnDate,
                            long overdueDays) {

        super(EventType.LOAN_OVERDUE);
        this.userId = userId;
        this.copyId = copyId;
        this.expectedReturnDate = expectedReturnDate;
        this.overdueDays = overdueDays;
        
        this.send = false;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCopyId() {
        return copyId;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public long getOverdueDays() {
        return overdueDays;
    }

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setCopyId(Long copyId) {
		this.copyId = copyId;
	}

	public void setExpectedReturnDate(LocalDate expectedReturnDate) {
		this.expectedReturnDate = expectedReturnDate;
	}

	public void setOverdueDays(long overdueDays) {
		this.overdueDays = overdueDays;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isSend() {
		return send;
	}

	public void setSend(boolean send) {
		this.send = send;
	}
}
