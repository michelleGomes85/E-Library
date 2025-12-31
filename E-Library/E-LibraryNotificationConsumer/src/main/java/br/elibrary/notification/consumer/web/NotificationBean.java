package br.elibrary.notification.consumer.web;

import java.io.Serializable;
import java.util.List;

import br.elibrary.events.BookAvailabilityEvent;
import br.elibrary.events.LoanOverdueEvent; 
import br.elibrary.notification.consumer.store.NotificationStore;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class NotificationBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private NotificationStore notificationStore;

    /**
     * Retorna o histórico de livros que ficaram disponíveis (Tabela 1)
     */
    public List<BookAvailabilityEvent> getNotifications() {
        return notificationStore.getAvailabilityHistory();
    }

    /**
     * Retorna o histórico de empréstimos em atraso (Tabela 2)
     */
    public List<LoanOverdueEvent> getOverdueNotifications() {
        return notificationStore.getOverdueHistory();
    }

    /**
     * Limpa ambos os históricos no Singleton
     */
    public void clear() {
        notificationStore.clearAll();
    }
}