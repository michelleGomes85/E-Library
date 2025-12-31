package br.elibrary.events;

/**
 * Destinos JMS utilizados pelo sistema de eventos.
 */
public final class EventDestinations {

    private EventDestinations() {
    }
    
    /**
     * Criar no Wildfly: localhost:9990
     * 
     * -- Precisa ser a versão standalone-full.xml do servidor
     * 
     * Caminho:
     * 
     * Subsystems -> Messaging -> Server -> default -> Destinations -> Clica em view
     * 
     * Aba: JMS Queue -> add
     */
    
    /**
     * Name: BookAvailableQueue
     * Entries: java:/jms/queue/BookAvailableQueue
     */
    public static final String LIBRARY_NOTICIFICATION_QUEUE =
            "java:/jms/queue/BookAvailableQueue";
}
