package br.elibrary.service.impl;

import br.elibrary.service.LibraryServiceTest;
import jakarta.ejb.Stateless;

@Stateless
public class LibraryServiceBean implements LibraryServiceTest {

    @Override
    public String consultarStatus() {
        return "EJB Core: Sistema E-Library operando com Java 21 no WildFly!";
    }
}
