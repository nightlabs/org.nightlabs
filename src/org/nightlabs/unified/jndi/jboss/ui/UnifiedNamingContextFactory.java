package org.nightlabs.unified.jndi.jboss.ui;

import java.net.URI;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.ObjectFactory;

import org.apache.log4j.Logger;
import org.jboss.naming.HttpNamingContextFactory;
import org.jboss.naming.NamingContextFactory;

/**
 * Central InitialContextFactory that creates one of: org.jboss.naming.NamingContextFactory and
 * org.jboss.naming.HttpNamingContextFactory depending on the given {@link Context#PROVIDER_URL}. The created context
 * is wrapped inside our own UnifiedLoginContext, which authenticates every method call and updates the lookup prefix
 * for EJB stubs depending on the protocol.
 *
 * @author Marius Heinzmann - marius[at]nightlabs[dot]de
 */
public class UnifiedNamingContextFactory
	implements InitialContextFactory, ObjectFactory
{
	private static Logger logger = Logger.getLogger(UnifiedNamingContextFactory.class);
	private static final String StoredJndiUri = "jfire.storedJndiUri";

	// Unfortunately HttpNamingContextFactory does NOT extend org.jnp.interfaces.NamingContextFactory and hence, we have
	// to keep to members pointing to the same kind of factory... WTF!
	private InitialContextFactory contextFactory;
	private ObjectFactory objectFactory;

	@SuppressWarnings("unchecked")
	public Context getInitialContext(Hashtable env) throws NamingException
	{
		if(logger.isDebugEnabled())
			logger.debug("getInitialContext("+env.toString()+")");

		// Get the login principal and credentials from the JNDI env
		Object credentials = env.get(Context.SECURITY_CREDENTIALS);
		Object principal = env.get(Context.SECURITY_PRINCIPAL);
		// Get the principal username
		String username = principal != null ? principal.toString() : null;
		String password = credentials != null ? credentials.toString() : null;

		// Unfortunately org.jboss.naming.NamingContextFactory modifies the java.naming.provider.url and cuts of the scheme!
		// Later on an new UnifiedNamingContext is created when a link is resolved and we won't be able to decide to which
		// ContextFactory to delegate!
		// WORKAROUND: Hence we store the original URI for later usage.
		String storedJndiUrl = (String) env.get(StoredJndiUri);
		URI jndiURI;
		if (storedJndiUrl != null)
			jndiURI = URI.create(storedJndiUrl);
		else
		{
			jndiURI = URI.create((String) env.get(Context.PROVIDER_URL));
			env.put(StoredJndiUri, jndiURI.toString());
		}

		// Depending on the JNDI lookup URI we're creating the corresponding InitialContextFactory and wrap it in our own
		// which extends the method invocation by authentication.
		// There is currently no need for a registry for the different kinds of InitialContextFactories, since we only
		// support JNP and HTTP(S) communication! (marius)
		final String scheme = jndiURI.getScheme();

		if (scheme == null)
			throw new IllegalStateException("No Scheme is defined -> Cannot decide which InitialContextFactory to create!");

		String additionalPrefix = null;
		if(scheme.equalsIgnoreCase("jnp"))
		{
			contextFactory = new NamingContextFactory();
			objectFactory = new NamingContextFactory();
			// don't need to set an additional prefix, since the lookup should already be ejb/byRemoteInterface/%clazzname%
		}
		else if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
		{
			contextFactory = new HttpNamingContextFactory();
			objectFactory = new HttpNamingContextFactory();

			if (scheme.equalsIgnoreCase("http"))
				additionalPrefix = "http";
			else
				additionalPrefix = "https";
		}

		if (contextFactory == null)
			throw new IllegalStateException("The given scheme is unknown -> Cannot decide which InitialContextFactory " +
					"to create!\n\t ProviderURL="+jndiURI);

		return new UnifiedLoginContext(contextFactory.getInitialContext(env), new UserDescriptor(username, password),
				additionalPrefix);
	}

	@SuppressWarnings("unchecked")
	public Object getObjectInstance(
			Object obj,
			Name name,
			Context nameCtx,
			Hashtable environment) throws Exception
	{
		if(logger.isDebugEnabled())
			logger.debug("getObjectInstance("+obj.toString()+", "+name.toString()+", "+nameCtx.toString()+", "+environment.toString());

		return objectFactory.getObjectInstance(obj, name, nameCtx, environment);
	}

}
