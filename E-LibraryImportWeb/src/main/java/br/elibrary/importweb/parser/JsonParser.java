package br.elibrary.importweb.parser;

import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.elibrary.importweb.bean.model.BookDTO;
import br.elibrary.importweb.bean.model.LibraryWrapper;

public class JsonParser {

    public List<BookDTO> parse(InputStream is) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        LibraryWrapper wrapper = mapper.readValue(is, LibraryWrapper.class);
        return wrapper.getBooks();
    }
}