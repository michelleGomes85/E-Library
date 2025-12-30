package br.elibrary.graphql.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import br.elibrary.graphql.dto.BookDTO;
import br.elibrary.graphql.dto.BookFilter;
import br.elibrary.graphql.dto.BookInput;
import br.elibrary.graphql.dto.CopyDTO;
import br.elibrary.graphql.dto.DashboardDTO;
import br.elibrary.graphql.dto.LoanDTO;

@Service
public class LibraryIntegrationService {

	private final RestClient restClient = RestClient.create("http://localhost:8080/E-LibraryAPI/api");

	public BookDTO createBook(BookInput input) {
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("titulo", input.title());
		formData.add("autor", input.author());
		formData.add("isbn", input.isbn());
		formData.add("editora", input.publisher());
		formData.add("ano", input.publicationYear() != null ? input.publicationYear().toString() : "");

		try {
			return restClient.post().uri("/livros").contentType(MediaType.APPLICATION_FORM_URLENCODED).body(formData)
					.retrieve().body(BookDTO.class);
		} catch (HttpClientErrorException e) {
			String errorMessage = e.getResponseBodyAsString();
			throw new RuntimeException(errorMessage);
		} catch (Exception e) {
			throw new RuntimeException("Erro inesperado na integração: " + e.getMessage());
		}
	}

	public CopyDTO updateCopyStatus(Long copyId, String status) {

		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

		formData.add("status", status);

		try {
			return restClient.put().uri("/exemplares/{id}/status", copyId)
					.contentType(MediaType.APPLICATION_FORM_URLENCODED).body(formData).retrieve().body(CopyDTO.class);
		} catch (HttpClientErrorException e) {
			String errorMessage = e.getResponseBodyAsString().isEmpty() ? e.getStatusCode().toString()
					: e.getResponseBodyAsString();
			throw new RuntimeException(errorMessage);
		} catch (Exception e) {
			throw new RuntimeException("Erro na integração: " + e.getMessage());
		}
	}

	public BookDTO getBookByIsbn(String isbn) {
	    try {
	        return restClient.get()
	                .uri("/livros/isbn/{isbn}", isbn)
	                .retrieve()
	                .body(BookDTO.class);
	    } catch (HttpClientErrorException e) {
	        String errorMessage = e.getResponseBodyAsString();
	        throw new RuntimeException(errorMessage);
	    } catch (Exception e) {
	        throw new RuntimeException("Erro na integração: " + e.getMessage());
	    }
	}
	
	public List<BookDTO> getAvailableBooks(BookFilter filter) {
	    try {
	        return restClient.get()
	                .uri(uriBuilder -> uriBuilder
	                        .path("/livros/available")
	                        .queryParam("author", filter != null ? filter.author() : null)
	                        .queryParam("status", filter != null ? filter.status() : "AVAILABLE")
	                        .build())
	                .retrieve()
	                .body(new ParameterizedTypeReference<List<BookDTO>>() {});
	    } catch (Exception e) {
	        throw new RuntimeException("Erro ao filtrar livros: " + e.getMessage());
	    }
	}
	
	public List<LoanDTO> getUserActiveLoans(Long userId) {
		
	    try {
	        return restClient.get()
	                .uri("biblioteca/emprestimo/usuario/{userId}", userId)
	                .retrieve()
	                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (request, response) -> {
	                    throw new RuntimeException("Erro ao buscar empréstimos: " + response.getStatusCode());
	                })
	                .body(new ParameterizedTypeReference<List<LoanDTO>>() {});
	    } catch (Exception e) {
	        throw new RuntimeException("Erro na integração de empréstimos: " + e.getMessage());
	    }
	}
	
	public DashboardDTO getLibraryDashboard() {
		try {
			return restClient.get()
					.uri("/biblioteca/dashboard")
					.retrieve()
					.body(DashboardDTO.class);
		} catch (Exception e) {
			throw new RuntimeException("Erro ao integrar dashboard: " + e.getMessage());
		}
	}
}