package br.elibrary.service;

import jakarta.ejb.Remote;

@Remote
public interface LibraryServiceTest {
	String consultarStatus();
}
