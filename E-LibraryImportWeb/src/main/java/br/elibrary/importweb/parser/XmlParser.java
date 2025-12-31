package br.elibrary.importweb.parser;

import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import br.elibrary.importweb.bean.model.BookDTO;
import br.elibrary.importweb.bean.model.LibraryWrapper;

public class XmlParser {

    public List<BookDTO> parse(InputStream is) throws Exception {
        XmlMapper xmlMapper = new XmlMapper();
        LibraryWrapper wrapper = xmlMapper.readValue(is, LibraryWrapper.class);
        return wrapper.getBooks();
    }
}