package br.elibrary.notification.consumer.web;

import java.io.Serializable;
import java.util.List;

import br.elibrary.events.BookAvailabilityEvent;
import br.elibrary.events.LoanOverdueEvent;
import br.elibrary.notification.consumer.service.EmailService;
import br.elibrary.notification.consumer.store.NotificationStore;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@ViewScoped
public class NotificationBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private NotificationStore notificationStore;

	@Inject
	private EmailService emailService;

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

	public void sendEmail(BookAvailabilityEvent event) {

		if (event.getEmail() == null || event.getEmail().trim().isEmpty()) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Informe um e-mail.", null);
			return;
		}

		try {
			String subject = "Livro disponível: " + event.getTitle();
			
			String body = "Olá,O livro " + event.getTitle() 
							+ " (ISBN: " + event.getIsbn() 
							+ ") está disponível." 
							+ "\n - ID do Livro: " + event.getBookId();
			
			emailService.send(event.getEmail(), subject, body);

			event.setSend(true);

			addMessage(FacesMessage.SEVERITY_INFO, "Email enviado com sucesso!", null);

		} catch (Exception e) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao enviar o email", "Verifique se o email inserido é válido");
		}
	}

	public void sendEmail(LoanOverdueEvent event) {
		if (event.getEmail() == null || event.getEmail().trim().isEmpty()) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Informe um e-mail.", null);
			return;
		}

		try {
			String subject = "Empréstimo em atraso";
			
			String body = "Atenção: seu empréstimo está com " + event.getOverdueDays() + " dia(s) de atraso."
					
					+ "\n - Usuário ID: " + event.getUserId() 
					+ "\n - Exemplar ID: " + event.getCopyId()
					+ "\n - Data Prevista de devolução: " + event.getExpectedReturnDate();

			emailService.send(event.getEmail(), subject, body);
			event.setSend(true);

			addMessage(FacesMessage.SEVERITY_INFO, "Email enviado com sucesso!", null);

		} catch (Exception e) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao enviar o email", "Verifique se o email inserido é válido");
		}
	}
	
	private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance()
            .addMessage(null, new FacesMessage(severity, summary, detail));
    }
}