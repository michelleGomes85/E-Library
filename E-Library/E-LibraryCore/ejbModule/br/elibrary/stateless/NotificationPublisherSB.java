package br.elibrary.stateless;

import java.time.LocalDate;

import br.elibrary.events.BookAvailabilityEvent;
import br.elibrary.events.EventDestinations;
import br.elibrary.events.LoanOverdueEvent;
import br.elibrary.service.NotificationPublisher;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;

/**
 * NotificationPublisherSB
 * ----------------------------------------------------
 * Producer JMS responsável por publicar eventos
 * de notificação do sistema da biblioteca.
 *
 * Este componente é utilizado pelo CORE do sistema
 * sempre que ocorre um evento relevante de domínio,
 * como:
 *
 *  - Um livro que estava indisponível passa a ter
 *    ao menos uma cópia disponível
 *
 *  - Um empréstimo ultrapassa a data prevista
 *    de devolução
 *
 * IMPORTANTE:
 *  - Este componente NÃO contém lógica de negócio
 *  - Ele NÃO acessa banco de dados
 *  - Ele NÃO conhece consumidores
 *  - Ele NÃO usa REST nem GraphQL
 *
 * Ele apenas transforma eventos do domínio em
 * mensagens JMS, garantindo comunicação assíncrona
 * e desacoplada entre os sistemas.
 */
@Stateless
public class NotificationPublisherSB implements NotificationPublisher {

    /**
     * Contexto JMS gerenciado pelo container.
     */
    @Inject
    private JMSContext jmsContext;

    /**
     * Fila JMS para eventos de livro disponível.
     */
    @Resource(lookup = EventDestinations.LIBRARY_NOTICIFICATION_QUEUE)
    private Queue livraryNotificationQueue;

    /**
     * Publica o evento BOOK_AVAILABLE.
     *
     * Este método é chamado pelo Core quando,
     * após a devolução de um exemplar, o sistema
     * detecta que o livro saiu do estado de
     * indisponível para disponível.
     */
    @Override
    public void publishBookAvailable(Long bookId,
                                     String isbn,
                                     String title) {

        BookAvailabilityEvent event = new BookAvailabilityEvent(bookId, isbn, title);

        jmsContext.createProducer()
                  .send(livraryNotificationQueue, event);
    }

    /**
     * Publica o evento LOAN_OVERDUE.
     *
     * Este método é chamado por um Timer EJB
     * responsável por verificar diariamente
     * os empréstimos vencidos.
     */
    @Override
    public void publishLoanOverdue(Long userId,
                                   Long copyId,
                                   LocalDate expectedReturnDate,
                                   long overdueDays) {

        LoanOverdueEvent event =
                new LoanOverdueEvent(
                        userId,
                        copyId,
                        expectedReturnDate,
                        overdueDays
                );

        jmsContext.createProducer().send(livraryNotificationQueue, event);
    }
}
