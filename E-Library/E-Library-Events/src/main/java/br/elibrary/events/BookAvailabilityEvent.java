package br.elibrary.events;

import java.time.LocalDateTime;

/**
 * Evento publicado quando um livro que estava
 * indisponível passa a ter ao menos uma cópia disponível.
 * 
 * tipoEvento = EMPRESTIMO_EM_ATRASO
 * usuarioId
 * exemplarId
 * dataPrevDevolucao
 * diasEmAtraso
 */
public class BookAvailabilityEvent extends LibraryEvent {

    private static final long serialVersionUID = 1L;
    
    private Long userId;
    private Long bookId;
    private String isbn;
    private String title;
    private LocalDateTime createdAt;
    
    private String email;
    private boolean send;

    public BookAvailabilityEvent(Long bookId, String isbn, String title) {
        super(EventType.BOOK_AVAILABLE);
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        
        this.send = false;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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
