package br.elibrary.service;

import java.time.LocalDate;

public interface NotificationPublisher {

    /**
     * Publica um evento informando que um livro
     * voltou a ficar disponível.
     *
     * Chamado quando um exemplar é devolvido
     * e antes não havia nenhuma cópia disponível.
     */
    void publishBookAvailable(Long bookId, String isbn, String title);

    /**
     * Publica um evento informando que um empréstimo
     * encontra-se em atraso.
     *
     * Chamado por um processo agendado (Timer EJB)
     * que verifica empréstimos vencidos.
     */
    void publishLoanOverdue(Long userId,
                            Long copyId,
                            LocalDate expectedReturnDate,
                            long overdueDays);
}

