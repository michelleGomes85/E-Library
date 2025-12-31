package br.elibrary.importweb.client;

import br.elibrary.importweb.bean.model.BookDTO;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;

public class LibraryRestClient {

    private static final String BASE_URL = "http://localhost:8080/E-LibraryAPI/api";

    private final Client client;
    private final WebTarget baseTarget;

    public LibraryRestClient() {
        this.client = ClientBuilder.newClient();
        this.baseTarget = client.target(BASE_URL);
    }

    public String testConnection() {

        Response response = baseTarget
                .path("/hello")
                .request(MediaType.TEXT_PLAIN)
                .get();

        if (response.getStatus() != 200) {
            throw new RuntimeException("Erro REST: HTTP " + response.getStatus());
        }

        return response.readEntity(String.class);
    }
    
    public Long registerBook(String title, String author, String isbn, String publisher, Integer year) {
    	
        Form form = new Form();
        
        form.param("titulo", title);
        form.param("autor", author);
        form.param("isbn", isbn);
        form.param("editora", publisher);
        form.param("ano", year != null ? year.toString() : "");

        Response response = baseTarget
                .path("/livros") 
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));

        if (response.getStatus() == 201) {
        	
            BookDTO saved = response.readEntity(BookDTO.class);
            
            return saved.getId();
        } else {
            String erro = response.readEntity(String.class);
            throw new RuntimeException("Falha ao cadastrar livro: " + erro);
        }
    }

    public void registerCopy(Long bookId) {
    	
        Response response = baseTarget
                .path("/livros/" + bookId + "/exemplares")
                .request()
                .post(Entity.json(""));

        if (response.getStatus() != 201) {
            String errorDetail = response.readEntity(String.class);
            throw new RuntimeException("Falha ao cadastrar exemplar (ID " + bookId + "): " + errorDetail);
        }
    }
}

