package ssad.remote.main;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

public class TestRemoteAccess {

	public static void main(String[] args) {
		Properties properties = new Properties();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
		properties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
		
		InitialContext context;
		
		try {
			context = new InitialContext(properties);
			
		} catch (Exception e) {
		}

	}

}
