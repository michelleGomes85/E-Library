package br.elibrary.rest.resource;

import br.elibrary.dto.CopyDTO;
import br.elibrary.exception.BusinessException;
import br.elibrary.rest.service.CopyRestService;
import jakarta.inject.Inject;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/exemplares")
@Produces(MediaType.APPLICATION_JSON)
public class CopyResource {

    @Inject
    private CopyRestService copyRestService;

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
    	
        CopyDTO dto = copyRestService.findById(id);
        
        if (dto == null)
            return Response.status(404).build();
        
        return Response.ok(dto).build();
    }

    @PUT
    @Path("/{id}/status")
    public Response updateStatus(
            @PathParam("id") Long id,
            @FormParam("status") String status) {

        if (status == null)
            return Response.status(400).entity("Status é obrigatório").build();

        try {
            CopyDTO updated = copyRestService.updateStatus(id, status);
            return Response.ok(updated).build();
        } catch (BusinessException e) {
            return Response.status(409).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(400).entity("Erro ao atualizar status").build();
        }
    }
}