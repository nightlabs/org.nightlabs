package org.nightlabs.unifiedejbjndi.jboss;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.ejb.Remote;
import javax.management.AttributeChangeNotification;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.LinkRef;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.jboss.deployment.DeploymentInfo;
import org.jboss.deployment.SubDeployer;
import org.jboss.ejb3.Ejb3ModuleMBean;
import org.jboss.ejb3.SessionContainer;
import org.jboss.system.ServiceMBeanSupport;

/**
 * This MBean maintains aliases for session beans (stateless or stateful EJBs) in JNDI.
 * <p>
 * The JEE standard does not specify any location in JNDI where EJBs can be reliably found.
 * There is not even a portable annotation available to declare a JNDI path in the EJB. This
 * makes it impossible to easily and reliably find an EJB in all JEE servers.
 * </p>
 * <p>
 * To solve this problem, this MBean registers a {@link LinkRef} for every EJB at a
 * straight-forward location: the remote interface's fully qualified name within the
 * path "ejb/byRemoteInterface/".
 * </p>
 * <p>
 * Of course, this strategy does not work, if there are multiple EJB classes implementing
 * the same remote interface or if an EJB is deployed multiple times (with different
 * configurations). In this case, error messages will be logged and the bean that was
 * first processed can be found in the ejb-by-remote-interface-location in JNDI.
 * </p>
 *
 * @author marco schulze - marco at nightlabs dot de
 */
public class UnifiedEjbJndiDeployer
extends ServiceMBeanSupport
implements UnifiedEjbJndiDeployerMBean
{

	@Override
	protected void createService() throws Exception {
		super.createService();

		if (ejb3DeployerObjectName == null)
			ejb3DeployerObjectName = new ObjectName("jboss.ejb3:service=EJB3Deployer");
	}

	private ObjectName ejb3DeployerObjectName = null;

	@Override
	protected void startService() throws Exception
	{
		long timestampBegin = System.currentTimeMillis();
		if (log.isDebugEnabled())
			log.debug("startService: Beginning.");

		super.startService();

		getServer().addNotificationListener(ejb3DeployerObjectName, ejb3DeployerListener, null, this);

		// Look for all already deployed EJBs so that we can register the listeners and add the JNDI-aliases.
		for (ObjectName objectName : getServer().queryNames(new ObjectName("jboss.j2ee:*"), null)) {
			if (objectName.getKeyProperty("module") != null) {
				synchronized (ejb3ModulesWithListeners) {
					ejb3ModulesWithListeners.add(objectName);
				}
				getServer().addNotificationListener(objectName, ejb3ModuleListener, null, this);
				createJndiAliases(objectName);
			}
		}

		if (log.isDebugEnabled())
			log.debug("startService: Done in " + (System.currentTimeMillis() - timestampBegin) + " msec.");
	}

	@Override
	protected void stopService() throws Exception
	{
		long timestampBegin = System.currentTimeMillis();
		if (log.isDebugEnabled())
			log.debug("stopService: Beginning.");

		// Remove all JNDI-aliases that we have added.
		Collection<ObjectName> ejb3ModuleNames;
		synchronized (ejb3ModuleName2JndiAliases) {
			ejb3ModuleNames = new ArrayList<ObjectName>(ejb3ModuleName2JndiAliases.keySet());
		}

		for (ObjectName ejb3ModuleName : ejb3ModuleNames) {
			try {
				destroyJndiAliases(ejb3ModuleName);
			} catch (Throwable t) {
				log.warn("stopService: " + t, t);
			}
		}

		// Remove all listeners.
		Collection<ObjectName> ejb3ModuleNamesForListenerRemoval;
		synchronized (ejb3ModulesWithListeners) {
			ejb3ModuleNamesForListenerRemoval = new ArrayList<ObjectName>(ejb3ModulesWithListeners);
			ejb3ModulesWithListeners.clear();
		}
		for (ObjectName objectName : ejb3ModuleNamesForListenerRemoval) {
			try {
				getServer().removeNotificationListener(objectName, ejb3ModuleListener);
			} catch (Throwable t) {
				log.warn("stopService: " + t, t);
			}
		}

		getServer().removeNotificationListener(ejb3DeployerObjectName, ejb3DeployerListener);

		super.stopService();

		if (log.isDebugEnabled())
			log.debug("stopService: Done in " + (System.currentTimeMillis() - timestampBegin) + " msec.");
	}

	/**
	 * Compose an {@link ObjectName} for the EJB3 module referenced by the given {@link DeploymentInfo}.
	 *
	 * @param deploymentInfo the meta-data about the deployment of an EJB3 module.
	 * @return the name of the EJB3 module that was deployed or undeployed.
	 */
	private ObjectName getEjb3ModuleName(DeploymentInfo deploymentInfo)
	{
		try {
			return new ObjectName("jboss.j2ee:module="+ deploymentInfo.shortName +",service=EJB3");
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException(e); // should never happen - what we generate above should always be well-formed!
		}
	}

	private NotificationListener ejb3DeployerListener = new NotificationListener() {
		@Override
		public void handleNotification(Notification notification, Object handback) {
			try {
				if (SubDeployer.CREATE_NOTIFICATION.equals(notification.getType())) {
					DeploymentInfo deploymentInfo = (DeploymentInfo) notification.getUserData();
					ObjectName on = getEjb3ModuleName(deploymentInfo);

					synchronized (ejb3ModulesWithListeners) {
						ejb3ModulesWithListeners.add(on);
					}
					getServer().addNotificationListener(on, ejb3ModuleListener, null, this);
				}
				else if (SubDeployer.DESTROY_NOTIFICATION.equals(notification.getType())) {
					DeploymentInfo deploymentInfo = (DeploymentInfo) notification.getUserData();
					ObjectName on = getEjb3ModuleName(deploymentInfo);

					synchronized (ejb3ModulesWithListeners) {
						// Remove the ObjectName from our list so that stopService() doesn't try
						// to access non-existent things.
						ejb3ModulesWithListeners.remove(on);
					}
					// No need to remove the listener, because that is done automatically when
					// the corresponding MBean is destroyed.
				}
			} catch (Throwable t) {
				log.error("ejb3DeployerListener.handleNotification: " + t, t);
			}
		}
	};

	private Set<ObjectName> ejb3ModulesWithListeners = new HashSet<ObjectName>();

	/**
	 * Get all interfaces that are either directly annotated with {@link Remote} or
	 * referenced by a <code>@Remote</code> annotation in the EJB class. This method
	 * can be used with any class; non-EJB-classes or EJB-classes having solely local interfaces
	 * (no remote interfaces) will cause the result to be an empty {@link Set}.
	 *
	 * @param ejbClass the EJB class. A non-EJB class or <code>null</code> leads to an empty result (silently; without exception).
	 * @return a {@link Set} of {@link Class} containing all EJB remote interfaces; never <code>null</code>.
	 */
	private static Set<Class<?>> getRemoteInterfaces(Class<?> ejbClass)
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
				interfaces.add(iface);
				collectAllInterfaces(interfaces, iface);
			}

			clazz = clazz.getSuperclass();
		}
	}

	/**
	 * Get all {@link SessionContainer}s from the specified {@link Ejb3ModuleMBean}.
	 *
	 * @param ejb3ModuleMBean the {@link Ejb3ModuleMBean} from which to obtain the {@link SessionContainer}s.
	 * @return a {@link Collection} of {@link SessionContainer}s; never <code>null</code>.
	 */
	protected Collection<SessionContainer> getSessionContainers(Ejb3ModuleMBean ejb3ModuleMBean)
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
	 * Get the {@link Ejb3ModuleMBean} for the specified name.
	 *
	 * @param ejb3ModuleName the name of the EJB3 module MBean.
	 * @return the MBean for the specified name.
	 */
	protected Ejb3ModuleMBean getEjb3ModuleMBean(ObjectName ejb3ModuleName)
	{
		return MBeanServerInvocationHandler.newProxyInstance(
				server,
				ejb3ModuleName,
				Ejb3ModuleMBean.class,
				false
		);
	}

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
	private static Context getSubcontext(InitialContext initialContext, String name)
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

	private Map<ObjectName, Set<String>> ejb3ModuleName2JndiAliases = new HashMap<ObjectName, Set<String>>();

	/**
	 * Create JNDI aliases for all session beans (stateless or stateful) that are contained in the
	 * EJB3 module specified by the given <code>ejb3ModuleName</code>.
	 *
	 * @param ejb3ModuleName the name of the EJB3 module.
	 * @throws NamingException if accessing JNDI fails fundamentally.
	 */
	protected void createJndiAliases(ObjectName ejb3ModuleName)
	throws NamingException
	{
		InitialContext initialContext = new InitialContext();
		try {
			Context ejbByRemoteInterfaceContext = getSubcontext(initialContext, JNDI_PREFIX_BY_REMOTE_INTERFACE);

			Ejb3ModuleMBean ejb3ModuleMBean = getEjb3ModuleMBean(ejb3ModuleName);
			for (SessionContainer container : getSessionContainers(ejb3ModuleMBean)) {
				Class<?> ejbClass = container.getClazz();
				Set<Class<?>> remoteInterfaces = getRemoteInterfaces(ejbClass);

				for (Class<?> remoteInterface : remoteInterfaces) {
					try {
						String jndiName = container.getEjbJndiName(remoteInterface);
						LinkRef linkRef = new LinkRef(jndiName);
						String jndiSimpleAlias = remoteInterface.getName();
						String jndiQualifiedAlias = JNDI_PREFIX_BY_REMOTE_INTERFACE + jndiSimpleAlias;

						// If there exist multiple beans for the same remote-interface, the following method
						// causes an exception. Otherwise it doesn't, because we maintain our
						// ejb-by-remote-interface-JNDI-subcontext and ensure that entries for undeployed EJBs
						// are cleaned up.

						ejbByRemoteInterfaceContext.bind(jndiSimpleAlias, linkRef);

						synchronized (ejb3ModuleName2JndiAliases) {
							Set<String> jndiAliases = ejb3ModuleName2JndiAliases.get(ejb3ModuleName);
							if (jndiAliases == null) {
								jndiAliases = new HashSet<String>();
								ejb3ModuleName2JndiAliases.put(ejb3ModuleName, jndiAliases);
							}
							jndiAliases.add(jndiQualifiedAlias);
						}
					} catch (NamingException e) {
						log.error("Registering alias failed: " + e, e);
					}
				}
			}
		} finally {
			initialContext.close();
		}
	}

	protected void destroyJndiAliases(ObjectName ejb3ModuleName)
	throws NamingException
	{
		InitialContext initialContext = new InitialContext();
		try {
			Collection<String> jndiAliases = null;
			synchronized (ejb3ModuleName2JndiAliases) {
				Set<String> _jndiAliases = ejb3ModuleName2JndiAliases.remove(ejb3ModuleName);
				if (_jndiAliases != null)
					jndiAliases = new ArrayList<String>(_jndiAliases);
			}

			if (jndiAliases != null) {
				for (String jndiAlias : jndiAliases) {
					try {
						initialContext.unbind(jndiAlias);
					} catch (NamingException e) {
						log.error("Unregistering alias failed: " + e, e);
					}
				}
			}
		} finally {
			initialContext.close();
		}
	}

	/**
	 * The listener that is triggered whenever an EJB3 module is created/started/stopped/destroyed.
	 */
	private NotificationListener ejb3ModuleListener = new NotificationListener() {
		@Override
		public void handleNotification(Notification notification, Object handback) {
			try {
				if (!AttributeChangeNotification.ATTRIBUTE_CHANGE.equals(notification.getType()))
					return;

				if (!(notification instanceof AttributeChangeNotification))
					return;

				AttributeChangeNotification attributeChangeNotification = (AttributeChangeNotification) notification;
				Object newValue = attributeChangeNotification.getNewValue();
				if (!(newValue instanceof Integer))
					return;

				int newState = ((Integer)newValue).intValue();

				ObjectName ejb3ModuleName = (ObjectName)notification.getSource();

				if (Ejb3ModuleMBean.STARTED == newState)
					createJndiAliases(ejb3ModuleName);
				else if (Ejb3ModuleMBean.STOPPING == newState)
					destroyJndiAliases(ejb3ModuleName);

			} catch (Throwable t) {
				log.error("ejb3ModuleListener.handleNotification: " + t, t);
			}
		}
	};
}
