package org.nightlabs.unifiedjndi.jboss.client;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * Simple wrapper context that applies substitutes the lookup name of objects according to the given
 * {@link #additionalJNDIPrefix}, if the lookup name contains the {@link #EJB_BY_REMOTE_INTERFACE_PREFIX}.
 *
 * @author Marius Heinzmann - marius[at]nightlabs[dot]de
 */
public class UnifiedJNDIContext
	implements Context
{
	private static final String EJB_BY_REMOTE_INTERFACE_PREFIX = "ejb/byRemoteInterface";
	protected Context delegate;
	protected String additionalJNDIPrefix;

	public UnifiedJNDIContext(Context _delegate, String additionalTargetPrefix)
		throws NamingException
	{
		if (_delegate == null)
			throw new IllegalArgumentException("_delegate must not be null!");

		this.delegate = _delegate;
		this.additionalJNDIPrefix = additionalTargetPrefix == null ? "" : additionalTargetPrefix;

		// ensure that the additional prefix ends with a '/' when an additional prefix is given.
		if (additionalJNDIPrefix.length() > 0 && ! additionalJNDIPrefix.endsWith("/"))
			additionalJNDIPrefix += "/";
	}

	@Override
	public Object lookup(Name name) throws NamingException
	{
		return lookup(name.toString());
	}

	@Override
	public Object lookup(String name) throws NamingException
	{
		if (additionalJNDIPrefix != null && name.contains(EJB_BY_REMOTE_INTERFACE_PREFIX));
		{
			name = name.replace("byRemoteInterface/", "byRemoteInterface/" + additionalJNDIPrefix);
		}

		return delegate.lookup(name);
	}

	@Override
	public void bind(Name name, Object obj) throws NamingException
	{
		delegate.bind(name, obj);
	}

	@Override
	public void bind(String name, Object obj) throws NamingException
	{
		delegate.bind(name, obj);
	}

	@Override
	public void rebind(Name name, Object obj) throws NamingException
	{
		delegate.rebind(name, obj);
	}

	@Override
	public void rebind(String name, Object obj) throws NamingException
	{
		delegate.rebind(name, obj);
	}

	@Override
	public void unbind(Name name) throws NamingException
	{
		delegate.unbind(name);
	}

	@Override
	public void unbind(String name) throws NamingException
	{
		delegate.unbind(name);
	}

	@Override
	public void rename(Name oldName, Name newName) throws NamingException
	{
		delegate.rename(oldName, newName);
	}

	@Override
	public void rename(String oldName, String newName) throws NamingException
	{
		delegate.rename(oldName, newName);
	}

	@Override
	public NamingEnumeration<NameClassPair> list(Name name) throws NamingException
	{
		return delegate.list(name);
	}

	@Override
	public NamingEnumeration<NameClassPair> list(String name) throws NamingException
	{
		return delegate.list(name);
	}

	@Override
	public NamingEnumeration<Binding> listBindings(Name name) throws NamingException
	{
		return delegate.listBindings(name);
	}

	@Override
	public NamingEnumeration<Binding> listBindings(String name) throws NamingException
	{
		return delegate.listBindings(name);
	}

	@Override
	public void destroySubcontext(Name name) throws NamingException
	{
		delegate.destroySubcontext(name);
	}

	@Override
	public void destroySubcontext(String name) throws NamingException
	{
		delegate.destroySubcontext(name);
	}

	@Override
	public Context createSubcontext(Name name) throws NamingException
	{
		return delegate.createSubcontext(name);
	}

	@Override
	public Context createSubcontext(String name) throws NamingException
	{
		return delegate.createSubcontext(name);
	}

	@Override
	public Object lookupLink(Name name) throws NamingException
	{
		return delegate.lookupLink(name);
	}

	@Override
	public Object lookupLink(String name) throws NamingException
	{
		return delegate.lookup(name);
	}

	@Override
	public NameParser getNameParser(Name name) throws NamingException
	{
		return delegate.getNameParser(name);
	}

	@Override
	public NameParser getNameParser(String name) throws NamingException
	{
		return delegate.getNameParser(name);
	}

	@Override
	public Name composeName(Name name, Name prefix) throws NamingException
	{
		return delegate.composeName(name, prefix);
	}

	@Override
	public String composeName(String name, String prefix) throws NamingException
	{
		return delegate.composeName(name, prefix);
	}

	@Override
	public Object addToEnvironment(String propName, Object propVal) throws NamingException
	{
		return delegate.addToEnvironment(propName, propVal);
	}

	@Override
	public Object removeFromEnvironment(String propName) throws NamingException
	{
		return delegate.removeFromEnvironment(propName);
	}

	@Override
	public Hashtable<?,?> getEnvironment() throws NamingException
	{
		return delegate.getEnvironment();
	}

	@Override
	public void close() throws NamingException
	{
		delegate.close();
	}

	@Override
	public String getNameInNamespace() throws NamingException
	{
		return delegate.getNameInNamespace();
	}

}