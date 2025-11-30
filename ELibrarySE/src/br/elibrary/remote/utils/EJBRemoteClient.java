package br.elibrary.remote.utils;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class EJBRemoteClient {

	private static final String APP_NAME = "E-LibraryEAR";
	private static final String MODULE_NAME = "E-Library";
	private static final String DISTINCT_NAME = "";

	private static InitialContext context;

	static {
		try {
			Properties props = new Properties();
			props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
			props.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");

			context = new InitialContext(props);
		} catch (Exception e) {
			throw new RuntimeException("Falha ao inicializar contexto JNDI", e);
		}
	}

	/**
	 * Lookup genérico para qualquer EJB remoto
	 */
	public static <T> T lookup(Class<T> remoteInterface, String beanName) {
		
		String jndiName = String.format("ejb:%s/%s/%s/%s!%s", APP_NAME, MODULE_NAME, DISTINCT_NAME, beanName, remoteInterface.getName());
		
		try {
			
			Object lookedUp = context.lookup(jndiName);
			
			return remoteInterface.cast(lookedUp);
		} catch (NamingException e) {
			throw new RuntimeException("EJB '" + beanName + "' não encontrado via JNDI: " + jndiName, e);
		}
	}
}
