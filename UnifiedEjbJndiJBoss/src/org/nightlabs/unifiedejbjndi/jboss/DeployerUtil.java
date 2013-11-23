package org.nightlabs.unifiedejbjndi.jboss;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.jboss.ejb3.Ejb3ModuleMBean;
import org.jboss.ejb3.SessionContainer;

/**
 * This utility class contains several methods used by the UnifiedEJB*Deployers.
 *
 * @author marco schulze - marco at nightlabs dot de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]de
 */
public final class DeployerUtil
{
	private DeployerUtil() {};

	/**
	 * Get the JNDI {@link Context} instance representing the subcontext specified by the given <code>name</code>.
	 * If this subcontext does not yet exist, it is created. If it does already exist, it is silently returned.
	 * If the specified name exists but points to sth. that is not an instance of <code>Context</code>, a
	 * {@link ClassCastException} is thrown.
	 *
	 * @param initialContext the initial context for accessing the JNDI.
	 * @param name the absolute name (intermediate subcontexts separated by '/') of the subcontext to reference - e.g. "my/jndi/path".
	 * @return the subcontext specified by the given <code>name</code>.
	 * @throws NamingException if accessing JNDI fails.
	 * @throws ClassCastException if the specified <code>name</code> already exists in JNDI but is not a {@link Context}.
	 */
	public static Context getSubcontext(InitialContext initialContext, String name)
	throws NamingException, ClassCastException
	{
		if (initialContext == null)
			throw new IllegalArgumentException("initialContext must not be null!");

		if (name == null)
			throw new IllegalArgumentException("name must not be null!");

		Context subcontext = initialContext;
		for (String part : name.split("/")) {
			if (part.length() == 0)
				continue;

			Context ctx;
			try {
				ctx = (Context) subcontext.lookup(part);
			} catch (NameNotFoundException x) {
				ctx = subcontext.createSubcontext(part);
			}
			subcontext = ctx;
		}
		return subcontext;
	}

	/**
	 * Get the {@link Ejb3ModuleMBean} for the specified name.
	 *
	 * @param ejb3ModuleName the name of the EJB3 module MBean.
	 * @return the MBean for the specified name.
	 */
	public static Ejb3ModuleMBean getEjb3ModuleMBean(ObjectName ejb3ModuleName, MBeanServer server)
	{
		return MBeanServerInvocationHandler.newProxyInstance(
				server,
				ejb3ModuleName,
				Ejb3ModuleMBean.class,
				false
		);
	}

	/**
	 * Get all {@link SessionContainer}s from the specified {@link Ejb3ModuleMBean}.
	 *
	 * @param ejb3ModuleMBean the {@link Ejb3ModuleMBean} from which to obtain the {@link SessionContainer}s.
	 * @return a {@link Collection} of {@link SessionContainer}s; never <code>null</code>.
	 */
	public static Collection<SessionContainer> getSessionContainers(Ejb3ModuleMBean ejb3ModuleMBean)
	{
		if (ejb3ModuleMBean == null)
			throw new IllegalArgumentException("ejb3ModuleMBean must not be null!");

		Collection<SessionContainer> result = new LinkedList<SessionContainer>();
		for (Iterator<?> it = ejb3ModuleMBean.getContainers().entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<?, ?> me = (Map.Entry<?, ?>) it.next();
			Object value = me.getValue();
			if (value instanceof SessionContainer) {
				result.add((SessionContainer) value);
			}
		}
		return result;
	}

	/**
	 * Returns the set of interfaces of the given <code>ejbInterfaceType</code> that are declared for the EJB with the
	 * given <code>ejbClass</code>. In case no Interface of the given type is defined an empty set is returned.
	 *
	 * @param ejbInterfaceType The type of interface whose declared interfaces are returned. (like remote or local)
	 * @param ejbClass The EJB's class to search.
	 * @return The set of interfaces of the given <code>ejbInterfaceType</code> or an empty set.
	 */
	public static Set<Class<?>> getEjbInterfaces(EjbInterfaceType ejbInterfaceType, Class<?> ejbClass)
	{
		Set<Class<?>> ejbInterfaces;
		switch (ejbInterfaceType) {
			case local:
				ejbInterfaces = getLocalInterfaces(ejbClass);
				break;
			case remote:
				ejbInterfaces = getRemoteInterfaces(ejbClass);
				break;

			default:
				throw new IllegalStateException("Unknown ejbInterfaceType: " + ejbInterfaceType);
		}
		return ejbInterfaces;
	}

	/**
	 * Get all remote interfaces that are either directly annotated with {@link Remote} or
	 * referenced by a <code>@Remote</code> annotation in the EJB class. This method
	 * can be used with any class; non-EJB-classes or EJB-classes having solely local interfaces
	 * (no remote interfaces) will cause the result to be an empty {@link Set}.
	 *
	 * @param ejbClass the EJB class. A non-EJB class or <code>null</code> leads to an empty result (silently; without exception).
	 * @return a {@link Set} of {@link Class} containing all EJB remote interfaces; never <code>null</code>.
	 */
	public static Set<Class<?>> getRemoteInterfaces(Class<?> ejbClass)
	{
		Set<Class<?>> remoteInterfaces = new HashSet<Class<?>>();

		{
			// Collect all remote-interfaces that are declared via the @Remote annotation directly
			// in the class or a superclass.
			Class<?> clazz = ejbClass;
			while (clazz != null) {
				for (Annotation annotation : clazz.getDeclaredAnnotations()) {
					if (annotation instanceof Remote) {
						Remote remoteAnnotation = (Remote) annotation;
						for (Class<?> remoteInterface : remoteAnnotation.value())
							remoteInterfaces.add(remoteInterface);
					}
				}

				clazz = clazz.getSuperclass();
			}
		}

		{
			// Collect all remote-interfaces that are tagged by the @Remote annotation.
			Set<Class<?>> interfaces = new HashSet<Class<?>>();
			collectAllInterfaces(interfaces, ejbClass);
			iterateInterfaces: for (Class<?> iface : interfaces) {
				for (Annotation annotation : iface.getDeclaredAnnotations()) {
					if (annotation instanceof Remote) {
						remoteInterfaces.add(iface);
						continue iterateInterfaces;
					}
				}
			}
		}

		return remoteInterfaces;
	}

	/**
	 * Get all local interfaces that are either directly annotated with {@link Local} or
	 * referenced by a <code>@Local</code> annotation in the EJB class. This method
	 * can be used with any class; non-EJB-classes or EJB-classes having solely local interfaces
	 * (no local interfaces) will cause the result to be an empty {@link Set}.
	 *
	 * @param ejbClass the EJB class. A non-EJB class or <code>null</code> leads to an empty result (silently; without exception).
	 * @return a {@link Set} of {@link Class} containing all EJB local interfaces; never <code>null</code>.
	 */
	public static Set<Class<?>> getLocalInterfaces(Class<?> ejbClass)
	{
		Set<Class<?>> localInterfaces = new HashSet<Class<?>>();

		{
			// Collect all local-interfaces that are declared via the @Local annotation directly
			// in the class or a superclass.
			Class<?> clazz = ejbClass;
			while (clazz != null) {
				for (Annotation annotation : clazz.getDeclaredAnnotations()) {
					if (annotation instanceof Local) {
						Local localAnnotation = (Local) annotation;
						for (Class<?> localInterface : localAnnotation.value())
							localInterfaces.add(localInterface);
					}
				}

				clazz = clazz.getSuperclass();
			}
		}

		{
			// Collect all local-interfaces that are tagged by the @Local annotation.
			Set<Class<?>> interfaces = new HashSet<Class<?>>();
			collectAllInterfaces(interfaces, ejbClass);
			iterateInterfaces: for (Class<?> iface : interfaces) {
				for (Annotation annotation : iface.getDeclaredAnnotations()) {
					if (annotation instanceof Local) {
						localInterfaces.add(iface);
						continue iterateInterfaces;
					}
				}
			}
		}

		return localInterfaces;
	}

	/**
	 * Collect all interfaces that are implemented directly or via a superclass or via
	 * other interfaces.
	 *
	 * @param interfaces the {@link Set} to be populated which must not be <code>null</code>.
	 * @param theClass the {@link Class} to analyse; can be <code>null</code>.
	 */
	private static void collectAllInterfaces(Set<Class<?>> interfaces, Class<?> theClass)
	{
		if (interfaces == null)
			throw new IllegalArgumentException("inferfaces must not be null!");

		Class<?> clazz = theClass;
		while (clazz != null) {
			for (Class<?> iface : clazz.getInterfaces()) {
				if (interfaces.add(iface))
					collectAllInterfaces(interfaces, iface);
			}

			clazz = clazz.getSuperclass();
		}
	}

}
