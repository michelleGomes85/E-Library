package br.elibrary.rest.resource;

import java.net.URI;
import java.util.List;

import br.elibrary.dto.BookDTO;
import br.elibrary.exception.BusinessException;
import br.elibrary.rest.service.BookRestService;
import br.elibrary.rest.service.CopyRestService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/livros")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
public class BookResource {

    @Inject
    private BookRestService bookRestService;
    
    @Inject
    private CopyRestService copyRestService;

    @POST
    public Response create (
            @FormParam("titulo") String titulo,
            @FormParam("autor") String autor,
            @FormParam("isbn") String isbn,
            @FormParam("editora") String editora,
            @FormParam("ano") Integer ano) {

        if (titulo == null || autor == null || isbn == null)
            return Response.status(400).entity("Campos obrigatórios: titulo, autor, isbn").build();

        BookDTO dto = new BookDTO();
        
        dto.setTitle(titulo);
        dto.setAuthor(autor);
        dto.setIsbn(isbn);
        dto.setPublisher(editora);
        dto.setYear(ano);

        try {
            BookDTO saved = bookRestService.create(dto);
            URI location = URI.create("/api/livros/" + saved.getId());
            return Response.created(location).entity(saved).build();
        } catch (Exception e) {
        	return Response.status(409).entity(e.getMessage()).build();
		} 
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        
    	try {
    		BookDTO dto = bookRestService.findById(id);
    		if (dto == null)
                return Response.status(404).build();
            return Response.ok(dto).build();
		} catch (Exception e) {
			return Response.status(404).entity(e.getMessage()).build();
		}
    }

    @PUT
    @Path("/{id}")
    public Response update(
            @PathParam("id") Long id,
            @FormParam("titulo") String titulo,
            @FormParam("autor") String autor,
            @FormParam("isbn") String isbn,
            @FormParam("editora") String editora,
            @FormParam("ano") Integer ano) {

        BookDTO dto = new BookDTO();
        
        dto.setTitle(titulo);
        dto.setAuthor(autor);
        dto.setIsbn(isbn);
        dto.setPublisher(editora);
        dto.setYear(ano);

        try {
            BookDTO updated = bookRestService.update(id, dto);
            return Response.ok(updated).build();
        } catch (Exception e) {
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @GET
    public Response findAll(
            @QueryParam("autor") String autor,
            @QueryParam("categoria") String categoria) {
        List<BookDTO> dtos = bookRestService.findAll(autor, categoria);
        return Response.ok(dtos).build();
    }

    @POST
    @Path("/{bookId}/exemplares")
    public Response createCopy(@PathParam("bookId") Long bookId) {
    	
        if (bookId == null) {
            return Response.status(400).entity("bookId obrigatório").build();
        }
        
        try {
            copyRestService.create(bookId);
            return Response.status(201).build();
        } catch (Exception e) {
            return Response.status(400).entity("Erro ao criar exemplar").build();
        }
    }

    @GET
    @Path("/{bookId}/exemplares")
    public Response findCopiesByBook(
            @PathParam("bookId") Long bookId,
            @QueryParam("status") String status) {
    	
        if (bookId == null) {
            return Response.status(400).entity("bookId obrigatório").build();
        }
        
        var dtos = copyRestService.findByBookIdAndStatus(bookId, status);
        
        return Response.ok(dtos).build();
    }
    
    @GET
    @Path("/isbn/{isbn}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByIsbn(@PathParam("isbn") String isbn) {
    	try {
    		BookDTO dto = bookRestService.findByIsbn(isbn);
            return Response.ok(dto).build();
		} catch (BusinessException e) {
			return Response.status(404).entity(e.getMessage()).build();
		}
    }
    
    @GET
    @Path("/available")
    public Response getAvailable(
            @QueryParam("author") String author,
            @QueryParam("status") String status) {
        List<BookDTO> books = bookRestService.findAvailableBooks(author, status);
        return Response.ok(books).build();
    }
}