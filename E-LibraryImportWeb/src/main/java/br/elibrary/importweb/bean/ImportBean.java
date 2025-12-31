package br.elibrary.importweb.bean;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import br.elibrary.importweb.bean.model.BookDTO;
import br.elibrary.importweb.bean.model.ImportReport;
import br.elibrary.importweb.client.LibraryRestClient;
import br.elibrary.importweb.parser.JsonParser;
import br.elibrary.importweb.parser.XmlParser;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named("importBean")
@ViewScoped
public class ImportBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<ImportReport> reports = new ArrayList<>();
	private final LibraryRestClient client = new LibraryRestClient();

	public void handleFileUpload(FileUploadEvent event) {

		UploadedFile file = event.getFile();
		reports.clear();

		try (InputStream is = file.getInputStream()) {
			List<BookDTO> books;

			String fileName = file.getFileName().toLowerCase();
			if (fileName.endsWith(".json")) {
				books = new JsonParser().parse(is);
			} else if (fileName.endsWith(".xml")) {
				books = new XmlParser().parse(is);
			} else {
				throw new Exception("Arquivo não suportado. Use somente .json ou .xml");
			}

			for (BookDTO book : books) {
				processImport(book);
			}

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Importação completa"));

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
		            new FacesMessage(
		                FacesMessage.SEVERITY_ERROR,
		                "Falha na importação",
		                "Não foi possível processar o arquivo. Verifique se o conteúdo está correto."
		            )
		        );
		}
	}

	private void processImport(BookDTO book) {
		try {
			Long bookId = client.registerBook(book.getTitle(), book.getAuthor(), book.getIsbn(), book.getPublisher(),
					book.getYear());

			int copiesCount = (book.getQuantity() != null) ? book.getQuantity() : 0;

			for (int i = 0; i < copiesCount; i++) {
				client.registerCopy(bookId);
			}

			reports.add(new ImportReport(book.getTitle(), book.getAuthor(), book.getIsbn(), book.getPublisher(),
					book.getYear(), copiesCount, "SUCESSO", "Livro e " + copiesCount + " exemplares cadastrados."));

		} catch (Exception e) {
			reports.add(new ImportReport(book.getTitle(), book.getAuthor(), book.getIsbn(), book.getPublisher(),
					book.getYear(), book.getQuantity(), "ERRO", e.getMessage()));
		}
	}

	public List<ImportReport> getReports() {
		return reports;
	}
}