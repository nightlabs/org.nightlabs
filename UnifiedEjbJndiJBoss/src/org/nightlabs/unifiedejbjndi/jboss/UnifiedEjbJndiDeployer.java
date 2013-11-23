package org.nightlabs.unifiedejbjndi.jboss;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
 * configurations). In this case, <code>WARN</code> messages are logged and the affected
 * remote interface is removed from the ejb-by-remote-interface-location in JNDI.
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

		// Look for all already deployed EJBs so that we can add the JNDI-aliases.
		for (ObjectName objectName : getServer().queryNames(new ObjectName("jboss.j2ee:*"), null)) {
			if (objectName.getKeyProperty("module") != null)
				createJndiAliases(objectName);
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
		// Because during remove, there might be new ones created, we don't simply iterate them, but perform a loop until
		// the map is empty.
		boolean ejb3ModuleNamesIsEmpty;
		do {
			Collection<ObjectName> ejb3ModuleNames;
			synchronized (ejbInterfaceType2ejb3ModuleName2JndiAliases) {
				int arrayListSize = 0;
				for (Map<ObjectName, Set<String>> ejb3ModuleName2JndiAliases : ejbInterfaceType2ejb3ModuleName2JndiAliases.values())
					arrayListSize += ejb3ModuleName2JndiAliases.size();

				ejb3ModuleNames = new ArrayList<ObjectName>(arrayListSize);

				for (Map<ObjectName, Set<String>> ejb3ModuleName2JndiAliases : ejbInterfaceType2ejb3ModuleName2JndiAliases.values())
					ejb3ModuleNames.addAll(ejb3ModuleName2JndiAliases.keySet());
			}
			ejb3ModuleNamesIsEmpty = ejb3ModuleNames.isEmpty();

			for (ObjectName ejb3ModuleName : ejb3ModuleNames) {
				try {
					destroyJndiAliases(ejb3ModuleName);
				} catch (Throwable t) {
					log.warn("stopService: " + t, t);
				}
			}
		} while (!ejb3ModuleNamesIsEmpty);

		// Remove the listener.
		getServer().removeNotificationListener(ejb3DeployerObjectName, ejb3DeployerListener);

		super.stopService();

		if (log.isDebugEnabled())
			log.debug("stopService: Done in " + (System.currentTimeMillis() - timestampBegin) + " msec.");
	}

	private NotificationListener ejb3DeployerListener = new NotificationListener() {
		@Override
		public void handleNotification(Notification notification, Object handback) {
			try {
				if (SubDeployer.CREATE_NOTIFICATION.equals(notification.getType())) {
					DeploymentInfo deploymentInfo = (DeploymentInfo) notification.getUserData();
					createJndiAliases(deploymentInfo.deployedObject);
				}
				else if (SubDeployer.DESTROY_NOTIFICATION.equals(notification.getType())) {
					DeploymentInfo deploymentInfo = (DeploymentInfo) notification.getUserData();
					destroyJndiAliases(deploymentInfo.deployedObject);
				}
			} catch (Throwable t) {
				log.error("ejb3DeployerListener.handleNotification: " + t, t);
			}
		}
	};

	/**
	 * Tracks which remote interface is implemented by which EJBs in order to provide helpful warning
	 * messages, if there is no unique interface-to-implementation-relationship (multiple deployments
	 * of the same EJB or multiple EJB classes implementing the same remote interface).
	 * <p>
	 * Important: Use this object as mutex for accessing it (synchronized block)!
	 * </p>
	 */
	private Map<EjbInterfaceType, Map<Class<?>, Set<EjbDescriptor>>> ejbInterfaceType2ejbInterface2ejbSet = new HashMap<EjbInterfaceType, Map<Class<?>,Set<EjbDescriptor>>>();

	/**
	 * Important: Use {@link #remoteInterface2ejbSet} as mutex for accessing this <code>Map</code>!
	 */
	private Map<EjbInterfaceType, Map<ObjectName, Map<Class<?>, Set<EjbDescriptor>>>> ejbInterfaceType2ejb3ModuleName2ejbInterface2ejbSet = new HashMap<EjbInterfaceType, Map<ObjectName,Map<Class<?>,Set<EjbDescriptor>>>>();

	{
		for (EjbInterfaceType ejbInterfaceType : EjbInterfaceType.values()) {
			ejbInterfaceType2ejbInterface2ejbSet.put(ejbInterfaceType, new HashMap<Class<?>, Set<EjbDescriptor>>());
			ejbInterfaceType2ejb3ModuleName2ejbInterface2ejbSet.put(ejbInterfaceType, new HashMap<ObjectName, Map<Class<?>, Set<EjbDescriptor>>>());
		}
	}

	private Map<EjbInterfaceType, Map<ObjectName, Set<String>>> ejbInterfaceType2ejb3ModuleName2JndiAliases = new HashMap<EjbInterfaceType, Map<ObjectName,Set<String>>>();

	{
		for (EjbInterfaceType ejbInterfaceType : EjbInterfaceType.values()) {
			ejbInterfaceType2ejb3ModuleName2JndiAliases.put(ejbInterfaceType, new HashMap<ObjectName, Set<String>>());
		}
	}

	protected void createJndiAliases(ObjectName ejb3ModuleName)
	throws NamingException
	{
		createJndiAliases(EjbInterfaceType.remote, ejb3ModuleName);
		createJndiAliases(EjbInterfaceType.local, ejb3ModuleName);
	}

	/**
	 * Create JNDI aliases for all session beans (stateless or stateful) that are contained in the
	 * EJB3 module specified by the given <code>ejb3ModuleName</code>.
	 * @param ejbInterfaceType process local or remote interfaces.
	 * @param ejb3ModuleName the name of the EJB3 module.
	 *
	 * @throws NamingException if accessing JNDI fails.
	 */
	protected void createJndiAliases(EjbInterfaceType ejbInterfaceType, ObjectName ejb3ModuleName)
	throws NamingException
	{
		if (log.isDebugEnabled())
			log.debug("createJndiAliases("+ejbInterfaceType+"): Beginnning for: " + ejb3ModuleName);

		String jndiPrefix = getEjbAliasJndiPrefix(ejbInterfaceType);

		InitialContext initialContext = new InitialContext();
		try {
			Context ejbByRemoteInterfaceContext = DeployerUtil.getSubcontext(initialContext, jndiPrefix);

			Ejb3ModuleMBean ejb3ModuleMBean = DeployerUtil.getEjb3ModuleMBean(ejb3ModuleName, server);
			for (SessionContainer container : DeployerUtil.getSessionContainers(ejb3ModuleMBean)) {
				Class<?> ejbClass = container.getClazz();
				EjbDescriptor ejbDesc = new EjbDescriptor(container.getObjectName(), ejbClass, ejb3ModuleName);
				Set<Class<?>> ejbInterfaces = DeployerUtil.getEjbInterfaces(ejbInterfaceType, ejbClass);

				for (Class<?> ejbInterface : ejbInterfaces) {
					String jndiName = container.getEjbJndiName(ejbInterface);
					LinkRef linkRef = new LinkRef(jndiName);
					String jndiSimpleAlias = ejbInterface.getName();
					String jndiQualifiedAlias = jndiPrefix + jndiSimpleAlias;

					Collection<EjbDescriptor> ejbDescriptors;
					synchronized (ejbInterfaceType2ejbInterface2ejbSet) {
						Map<Class<?>, Set<EjbDescriptor>> ejbInterface2ejbSet = ejbInterfaceType2ejbInterface2ejbSet.get(ejbInterfaceType);
						Map<ObjectName, Map<Class<?>, Set<EjbDescriptor>>> ejb3ModuleName2ejbInterface2ejbSet = ejbInterfaceType2ejb3ModuleName2ejbInterface2ejbSet.get(ejbInterfaceType);

						Map<Class<?>, Set<EjbDescriptor>> m1 = ejb3ModuleName2ejbInterface2ejbSet.get(ejb3ModuleName);
						if (m1 == null) {
							m1 = new HashMap<Class<?>, Set<EjbDescriptor>>();
							ejb3ModuleName2ejbInterface2ejbSet.put(ejb3ModuleName, m1);
						}
						Set<EjbDescriptor> ejbSet_thisModule = m1.get(ejbInterface);
						if (ejbSet_thisModule == null) {
							ejbSet_thisModule = new HashSet<EjbDescriptor>();
							m1.put(ejbInterface, ejbSet_thisModule);
						}
						ejbSet_thisModule.add(ejbDesc);

						Set<EjbDescriptor> ejbSet_allModules = ejbInterface2ejbSet.get(ejbInterface);
						if (ejbSet_allModules == null) {
							ejbSet_allModules = new HashSet<EjbDescriptor>();
							ejbInterface2ejbSet.put(ejbInterface, ejbSet_allModules);
						}
						ejbSet_allModules.add(ejbDesc);

						if (ejbSet_allModules.size() == 1)
							ejbDescriptors = null;
						else
							ejbDescriptors = new ArrayList<EjbDescriptor>(ejbSet_allModules);
					}

					if (ejbDescriptors == null) {
						synchronized (ejbInterfaceType2ejb3ModuleName2JndiAliases) {
							Map<ObjectName, Set<String>> ejb3ModuleName2JndiAliases = ejbInterfaceType2ejb3ModuleName2JndiAliases.get(ejbInterfaceType);

							Set<String> jndiAliases = ejb3ModuleName2JndiAliases.get(ejb3ModuleName);
							if (jndiAliases == null) {
								jndiAliases = new HashSet<String>();
								ejb3ModuleName2JndiAliases.put(ejb3ModuleName, jndiAliases);
							}
							jndiAliases.add(jndiQualifiedAlias);
						}

						ejbByRemoteInterfaceContext.bind(jndiSimpleAlias, linkRef);
					}
					else {
						log.warn("createJndiAliases("+ejbInterfaceType+"): Duplicate use of EJB interface \"" + ejbInterface.getName() + "\"!");
						log.warn("createJndiAliases("+ejbInterfaceType+"): The following " + ejbDescriptors.size() + " EJBs share the same EJB interface:");
						for (EjbDescriptor ejbD : ejbDescriptors) {
							log.warn("createJndiAliases("+ejbInterfaceType+"):   * class \"" + ejbD.getEjbClass().getName() + "\" deployed as \"" + ejbD.getSessionContainerName() + "\"");
						}
						try {
							ejbByRemoteInterfaceContext.unbind(jndiSimpleAlias);
						} catch (NameNotFoundException x) {
							// According to the javadoc, this should never happen, but if it does, we silently ignore it ;-)
						}
					}
				}
			}
		} finally {
			initialContext.close();
		}

		if (log.isDebugEnabled())
			log.debug("createJndiAliases("+ejbInterfaceType+"): Done for: " + ejb3ModuleName);
	}

	private static String getEjbAliasJndiPrefix(EjbInterfaceType ejbInterfaceType) {
		String jndiPrefix;
		switch (ejbInterfaceType) {
			case local:
				jndiPrefix = JNDI_PREFIX_EJB_BY_LOCAL_INTERFACE;
				break;
			case remote:
				jndiPrefix = JNDI_PREFIX_EJB_BY_REMOTE_INTERFACE;
				break;

			default:
				throw new IllegalStateException("Unknown ejbInterfaceType: " + ejbInterfaceType);
		}
		return jndiPrefix;
	}

	protected void destroyJndiAliases(ObjectName ejb3ModuleName)
	throws NamingException
	{
		destroyJndiAliases(EjbInterfaceType.local, ejb3ModuleName);
		destroyJndiAliases(EjbInterfaceType.remote, ejb3ModuleName);
	}

	/**
	 * Remove all JNDI aliases that have been added for an EJB3 module. If the disappearing of this
	 * EJB3 module causes duplicate usages of remote interfaces not to be duplicate anymore, this
	 * method will re-initialise the affected EJB3 modules (and thus add the previously suppressed EJB
	 * aliases).
	 * @param ejbInterfaceType process local or remote interfaces.
	 * @param ejb3ModuleName the name of the EJB3 module.
	 *
	 * @throws NamingException if a fundamental JNDI error occurs.
	 */
	protected void destroyJndiAliases(EjbInterfaceType ejbInterfaceType, ObjectName ejb3ModuleName)
	throws NamingException
	{
		if (log.isDebugEnabled())
			log.debug("destroyJndiAliases("+ejbInterfaceType+"): Beginning for: " + ejb3ModuleName);

		Set<ObjectName> ejb3ModuleNamesRequiringReprocessing = new HashSet<ObjectName>();

		InitialContext initialContext = new InitialContext();
		try {
			synchronized (ejbInterfaceType2ejbInterface2ejbSet) {
				Map<Class<?>, Set<EjbDescriptor>> ejbInterface2ejbSet = ejbInterfaceType2ejbInterface2ejbSet.get(ejbInterfaceType);
				Map<ObjectName, Map<Class<?>, Set<EjbDescriptor>>> ejb3ModuleName2ejbInterface2ejbSet = ejbInterfaceType2ejb3ModuleName2ejbInterface2ejbSet.get(ejbInterfaceType);

				Map<Class<?>, Set<EjbDescriptor>> m1 = ejb3ModuleName2ejbInterface2ejbSet.remove(ejb3ModuleName);
				if (m1 != null) {
					for (Map.Entry<Class<?>, Set<EjbDescriptor>> me1 : m1.entrySet()) {
						Class<?> remoteInterface = me1.getKey();
						Set<EjbDescriptor> ejbSet_thisModule = me1.getValue();
						Set<EjbDescriptor> ejbSet_allModules = ejbInterface2ejbSet.get(remoteInterface);
						if (ejbSet_allModules != null) {
							int ejbSet_allModules_sizeBefore = ejbSet_allModules.size();
							ejbSet_allModules.removeAll(ejbSet_thisModule);

							if (ejbSet_allModules.size() == 1 && ejbSet_allModules_sizeBefore > 1) {
								// We need to create the JNDI aliases now, since they were unregistered before
								// due to duplicate remote interfaces, but don't are duplicate any longer.
								ejb3ModuleNamesRequiringReprocessing.add(
										ejbSet_allModules.iterator().next().getEjb3ModuleName()
								);
							}

							if (ejbSet_allModules.isEmpty())
								ejbInterface2ejbSet.remove(remoteInterface);
						}
					}
				}
			}

			Collection<String> jndiAliases = null;
			synchronized (ejbInterfaceType2ejb3ModuleName2JndiAliases) {
				Map<ObjectName, Set<String>> ejb3ModuleName2JndiAliases = ejbInterfaceType2ejb3ModuleName2JndiAliases.get(ejbInterfaceType);

				Set<String> _jndiAliases = ejb3ModuleName2JndiAliases.remove(ejb3ModuleName);
				if (_jndiAliases != null)
					jndiAliases = new ArrayList<String>(_jndiAliases);
			}

			if (jndiAliases != null) {
				for (String jndiAlias : jndiAliases) {
					try {
						initialContext.unbind(jndiAlias);
					} catch (NameNotFoundException x) {
						// According to the javadoc, we shouldn't get an exception, if it does not exist; but I just encountered one.
						// Hence, we silently ignore it. If it's not existing, we already have what we want. Marco.
					} catch (NamingException e) {
						log.error("destroyJndiAliases("+ejbInterfaceType+"): " + e, e);
					}
				}
			}
		} finally {
			initialContext.close();
		}

		if (!ejb3ModuleNamesRequiringReprocessing.isEmpty()) {
			for (ObjectName ejb3ModuleNameRequiringReprocessing : ejb3ModuleNamesRequiringReprocessing)
				destroyJndiAliases(ejbInterfaceType, ejb3ModuleNameRequiringReprocessing);

			for (ObjectName ejb3ModuleNameRequiringReprocessing : ejb3ModuleNamesRequiringReprocessing)
				createJndiAliases(ejbInterfaceType, ejb3ModuleNameRequiringReprocessing);
		}

		if (log.isDebugEnabled())
			log.debug("destroyJndiAliases("+ejbInterfaceType+"): Done for: " + ejb3ModuleName);
	}
}
