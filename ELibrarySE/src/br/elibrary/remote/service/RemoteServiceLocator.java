package br.elibrary.remote.service;

import br.elibrary.remote.utils.EJBRemoteClient;
import br.elibrary.service.BookService;
import br.elibrary.service.CatalogStatusService;
import br.elibrary.service.CopyService;
import br.elibrary.service.UserSessionService;

public class RemoteServiceLocator {

	private static BookService bookService;
	private static CopyService copyService;
	private static CatalogStatusService catalogStatusService;
	private static UserSessionService userSessionService;

	public static synchronized BookService getBookService() {

		if (bookService == null)
			bookService = EJBRemoteClient.lookup(BookService.class, "BookSB");

		return bookService;
	}

	public static synchronized CopyService getCopyService() {
		if (copyService == null)
			copyService = EJBRemoteClient.lookup(CopyService.class, "CopySB");

		return copyService;
	}

	public static synchronized CatalogStatusService getCatalogStatusService() {

		if (catalogStatusService == null)
			catalogStatusService = EJBRemoteClient.lookup(CatalogStatusService.class, "CatalogStatusSB");

		return catalogStatusService;
	}

	public static synchronized UserSessionService getUserSessionService() {

		if (userSessionService == null)
			userSessionService = EJBRemoteClient.lookup(UserSessionService.class, "UserSessionSB");
		
		return userSessionService;
	}
}
