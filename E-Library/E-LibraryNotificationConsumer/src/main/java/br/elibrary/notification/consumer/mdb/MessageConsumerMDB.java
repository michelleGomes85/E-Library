package br.elibrary.notification.consumer.mdb;

import java.time.LocalDateTime;

import br.elibrary.events.BookAvailabilityEvent;
import br.elibrary.events.EventDestinations;
import br.elibrary.events.LoanOverdueEvent;
import br.elibrary.notification.consumer.model.NotificationRecord;
import br.elibrary.notification.consumer.store.NotificationStore;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@MessageDriven(activationConfig = {
	    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = EventDestinations.LIBRARY_NOTICIFICATION_QUEUE),
	    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue") 
	})
	public class MessageConsumerMDB implements MessageListener {

	    @PersistenceContext(unitName = "E-Library")
	    private EntityManager em;

	    @Inject
	    private NotificationStore notificationStore;

	    @Override
	    public void onMessage(Message message) {
	        try {
	            if (message instanceof ObjectMessage objMessage) {
	                Object content = objMessage.getObject();
	                processNotification(content);
	            }
	        } catch (JMSException e) {
	            e.printStackTrace();
	        }
	    }

	    private void processNotification(Object content) {
	    	
	    	System.out.println("EVENTO CHAMADO");
	        if (content instanceof BookAvailabilityEvent event) {
	            saveAvailability(event);
	        } else if (content instanceof LoanOverdueEvent event) {
	            saveOverdue(event);
	        }
	    }

	    private void saveAvailability(BookAvailabilityEvent event) {
	    	
	        // Formato: livroId | isbn | titulo | dataHora
	        String data = String.join(" | ", 
	            event.getBookId().toString(),
	            event.getIsbn(),
	            event.getTitle(),
	            event.getDateTime().toString()
	        );

	        persistRecord("LIVRO_DISPONIVEL", data, event.getUserId());
	        
	        notificationStore.addAvailability(event);
	    }

	    private void saveOverdue(LoanOverdueEvent event) {
	    	
	    	if (isOverdueAlreadyNotifiedToday(event.getUserId(), event.getCopyId())) {
	            System.out.println("[MDB] Notificação de atraso ignorada: Já existe registro para hoje.");
	            return; 
	        }
	    	
	        // Formato: usuarioId | exemplarId | dataPrevDevolucao | diasEmAtraso
	        String data = String.join(" | ", 
	            event.getUserId().toString(),
	            event.getCopyId().toString(),
	            event.getExpectedReturnDate().toString(),
	            String.valueOf(event.getOverdueDays())
	        );

	        persistRecord("EMPRESTIMO_EM_ATRASO", data, event.getUserId());
	        notificationStore.addOverdue(event);
	    }
	    
	    private boolean isOverdueAlreadyNotifiedToday(Long userId, Long copyId) {
	    	
	        String payloadLike = userId + " | " + copyId + " | %";
	        
	        Long count = em.createQuery(
	            "SELECT COUNT(n) FROM NotificationRecord n " +
	            "WHERE n.type = 'EMPRESTIMO_EM_ATRASO' " +
	            "AND n.payload LIKE :payload " +
	            "AND CAST(n.createdAt AS date) = CURRENT_DATE", Long.class)
	            .setParameter("payload", payloadLike)
	            .getSingleResult();

	        return count > 0;
	    }

	    private void persistRecord(String type, String payload, Long UserId ) {
	        NotificationRecord record = new NotificationRecord();
	        record.setType(type);
	        record.setPayload(payload);
	        record.setCreatedAt(LocalDateTime.now());
	        record.setUserId(UserId);
	        
	        em.persist(record);
	    }
	}