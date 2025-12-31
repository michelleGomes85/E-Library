package br.elibrary.notification.consumer.store;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.elibrary.events.BookAvailabilityEvent;
import br.elibrary.events.LoanOverdueEvent;
import br.elibrary.notification.consumer.model.NotificationRecord;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Singleton
public class NotificationStore {
    
    @PersistenceContext(unitName = "E-Library")
    private EntityManager em;

    private List<BookAvailabilityEvent> availabilityHistory = Collections.synchronizedList(new ArrayList<>());
    private List<LoanOverdueEvent> overdueHistory = Collections.synchronizedList(new ArrayList<>());

    @PostConstruct
    public void init() {
        loadHistoryFromDatabase();
    }

    private void loadHistoryFromDatabase() {
    	
        List<NotificationRecord> records = em.createQuery(
        		
            "SELECT n FROM NotificationRecord n ORDER BY n.createdAt DESC", NotificationRecord.class)
            .getResultList();

        for (NotificationRecord record : records) {
        	
            if ("LIVRO_DISPONIVEL".equals(record.getType())) {
                availabilityHistory.add(parseAvailability(record));
            } else if ("EMPRESTIMO_EM_ATRASO".equals(record.getType())) {
                overdueHistory.add(parseOverdue(record));
            }
        }
    }

    private BookAvailabilityEvent parseAvailability(NotificationRecord record) {
    	
        // Formato: livroId | isbn | titulo | dataHora
    	
        String[] parts = record.getPayload().split(" \\| ");
        BookAvailabilityEvent event = new BookAvailabilityEvent(
            Long.parseLong(parts[0]), parts[1], parts[2]);
        event.setCreatedAt(LocalDateTime.parse(parts[3]));
        return event;
    }

    private LoanOverdueEvent parseOverdue(NotificationRecord record) {
    	
        // Formato: usuarioId | exemplarId | dataPrevDevolucao | diasEmAtraso
    	
        String[] parts = record.getPayload().split(" \\| ");
        LoanOverdueEvent event = new LoanOverdueEvent(
            Long.parseLong(parts[0]), 
            Long.parseLong(parts[1]), 
            LocalDate.parse(parts[2]), 
            Long.parseLong(parts[3]));
        event.setCreatedAt(record.getCreatedAt());
        return event;
    }

    public void addAvailability(BookAvailabilityEvent event) {
        availabilityHistory.add(0, event);
    }

    public void addOverdue(LoanOverdueEvent event) {
    	boolean existsInMemory = overdueHistory.stream()
    	        .anyMatch(e -> e.getUserId().equals(event.getUserId()) && 
    	                       e.getCopyId().equals(event.getCopyId()) &&
    	                       e.getCreatedAt().toLocalDate().equals(LocalDate.now()));

    	    if (!existsInMemory) {
    	        overdueHistory.add(0, event);
    	    }
    }

	public List<BookAvailabilityEvent> getAvailabilityHistory() {
		return availabilityHistory;
	}

	public void setAvailabilityHistory(List<BookAvailabilityEvent> availabilityHistory) {
		this.availabilityHistory = availabilityHistory;
	}

	public List<LoanOverdueEvent> getOverdueHistory() {
		return overdueHistory;
	}

	public void setOverdueHistory(List<LoanOverdueEvent> overdueHistory) {
		this.overdueHistory = overdueHistory;
	}
	
	public void clearAll() {
		availabilityHistory.clear();
		overdueHistory.clear();
	}
}