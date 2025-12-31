package br.elibrary.events;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Classe base de todos os eventos da biblioteca.
 * Representa um fato ocorrido no sistema.
 */
public abstract class LibraryEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private EventType eventType;
    private LocalDateTime dateTime;

    protected LibraryEvent(EventType eventType) {
        this.eventType = eventType;
        this.dateTime = LocalDateTime.now();
    }

    public EventType getEventType() {
        return eventType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
