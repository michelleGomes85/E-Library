package br.elibrary.rest.resource;

import java.util.List;
import br.elibrary.dto.LoanDTO;
import br.elibrary.dto.DashboardDTO; 
import br.elibrary.rest.service.LibraryRestService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/biblioteca")
@Produces(MediaType.APPLICATION_JSON)
public class LibraryResource {

    @Inject
    private LibraryRestService libraryRestService;

    /**
     * Retorna o status consolidado da biblioteca (Dashboard).
     * Ideal para o painel principal do sistema.
     */
    @GET
    @Path("/dashboard")
    public Response getDashboard() {
        try {
            DashboardDTO dashboard = libraryRestService.getLibraryDashboard();
            return Response.ok(dashboard).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Erro ao carregar dashboard: " + e.getMessage()).build();
        }
    }

    /**
     * Retorna a lista de empréstimos ativos de um usuário específico.
     */
    @GET
    @Path("/emprestimo/usuario/{userId}")
    public Response getActiveLoans(@PathParam("userId") Long userId) {
        try {
            List<LoanDTO> loans = libraryRestService.findActiveLoansByUser(userId);
            return Response.ok(loans).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Erro ao carregar empréstimos: " + e.getMessage()).build();
        }
    }
}