package br.elibrary.rest;

import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import br.elibrary.service.LibraryServiceTest;

@Path("/hello")
public class LibraryResource {

    @EJB
    private LibraryServiceTest libraryService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHello() {
        return "Resposta REST: " + libraryService.consultarStatus();
    }
}