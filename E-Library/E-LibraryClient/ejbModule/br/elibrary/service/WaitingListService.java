package br.elibrary.service;

import jakarta.ejb.Local;

@Local
public interface WaitingListService {
	void subscribe(Long userId, Long bookId);
}
