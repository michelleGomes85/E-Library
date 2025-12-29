package br.elibrary.remote.utils;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class EJBRemoteClient {

	private static final String APP_NAME = "E-LibraryEAR-0.0.1-SNAPSHOT";
	private static final String MODULE_NAME = "br.elibrary-E-LibraryCore-0.0.1-SNAPSHOT";
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
		
	    String distinctPart = (DISTINCT_NAME == null || DISTINCT_NAME.isEmpty()) ? "" : DISTINCT_NAME + "/";
	    

	    String jndiName = String.format("ejb:%s/%s/%s%s!%s", 
	                                    APP_NAME, MODULE_NAME, distinctPart, beanName, remoteInterface.getName());
	    
	    try {
	        return remoteInterface.cast(context.lookup(jndiName));
	    } catch (NamingException e) {
	        throw new RuntimeException("Erro JNDI: " + jndiName, e);
	    }
	}
}
