package org.nightlabs.unifiedejbjndi.jboss;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.naming.LinkRef;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.ejb.RemoteBindingImpl;
import org.jboss.annotation.ejb.RemoteBindings;
import org.jboss.annotation.ejb.RemoteBindingsImpl;
import org.jboss.deployment.DeploymentInfo;
import org.jboss.deployment.SubDeployer;
import org.jboss.ejb3.Ejb3ModuleMBean;
import org.jboss.ejb3.ProxyDeployer;
import org.jboss.ejb3.SessionContainer;
import org.jboss.ejb3.remoting.RemoteProxyFactory;
import org.jboss.system.ServiceMBeanSupport;

/**
 * This deployer maintains (creates and removes) HTTPInvokerProxys for Bean having remote interfaces.
 * <p>
 * The JEE standard does not specify any location in JNDI where EJBs can be reliably found.
 * There is not even a portable annotation available to declare a JNDI path in the EJB. This
 * makes it impossible to easily and reliably find an EJB in all JEE servers.
 * </p>
 * <p>
 * To solve this problem, this MBean registers a {@link LinkRef} for every EJB at a
 * straight-forward location: the remote interface's fully qualified name within the
 * path "ejb/byRemoteInterface/http" and/or "ejb/byRemoteInterface/https".
 * </p>
 * <p>
 * Of course, this strategy does not work, if there are multiple EJB classes implementing
 * the same remote interface or if an EJB is deployed multiple times (with different
 * configurations). In this case, <code>WARN</code> messages are logged and the affected
 * remote interface is removed from the ejb-by-remote-interface-location in JNDI.
 * </p>
 *
 * @author Marius Heinzmann - Marius[at]NightLabs[dot]de
 */
public class UnifiedEjbJndiHttpProxyDeployer
	extends ServiceMBeanSupport
	implements UnifiedEjbJndiHttpProxyDeployerMBean
{
	private static final String JBOSS_EJB3_SERVICE_EJB3_DEPLOYER = "jboss.ejb3:service=EJB3Deployer";
	private static final String SERVER_CONFIG_DIR_NAME = "conf";
	private static final String SERVER_CONFIG_PROPS_DIR_NAME = "props";
	private static final String HTTP_PROXY_PROPERTIES_FILE_NAME = "httpProxy.properties";
	/**
	 * The Invoker URL with which the generated Proxies shall communicate.
	 */
	private static final String HTTP_PROXY_PROPERTY_HTTP_INVOKER_URL = "httpProxy.invoker.url";
	/**
	 * The Invoker HTTPS URL with which the generated Proxies shall communicate for https transport.
	 */
	private static final String HTTP_PROXY_PROPERTY_HTTPS_INVOKER_URL = "httpsProxy.invoker.url";

//	private static Logger log = Logger.getLogger(UnifiedEjbJndiHttpProxyDeployer.class);

	// TODO: maybe we should create a surveillance thread that rereads the properties when needed and updates all the
	// known http proxies (marius).
	/**
	 * Properties from which the RemoteBindings of the Beans are created. <br>
	 * Currently only the invoker URLs are supported. See {@link #HTTP_PROXY_PROPERTY_HTTP_INVOKER_URL} and
	 * {@link #HTTP_PROXY_PROPERTY_HTTPS_INVOKER_URL}. <br>
	 *
	 * TODO: Extend the configuration to use different interceptor stacks, proxy facilities, etc.
	 */
	private Properties proxyProperties = new Properties();
	private boolean proxyPropertiesAvailable = false;

	/**
	 * The EJB3 Deployer's object name.
	 */
	private ObjectName ejb3DeployerObjectName = null;

	@Override
	protected void createService() throws Exception {
		super.createService();

		if (ejb3DeployerObjectName == null)
			ejb3DeployerObjectName = new ObjectName(JBOSS_EJB3_SERVICE_EJB3_DEPLOYER);

		// get the information needed for creating the HTTP Proxies from the properties file
		// in %server%/deploy/httpProxy.properties
		String serverHomeDir = System.getProperty("jboss.server.home.dir");
		if (serverHomeDir == null)
			throw new IllegalStateException("Couldn't get the server's home directory via the 'jboss.server.home.dir' " +
					"system property!");

		File configPropsDir = new File(new File(serverHomeDir, SERVER_CONFIG_DIR_NAME), SERVER_CONFIG_PROPS_DIR_NAME);
		File proxyPropertiesFile = new File(configPropsDir, HTTP_PROXY_PROPERTIES_FILE_NAME);
		if (proxyPropertiesFile.exists())
		{
			InputStream in = new FileInputStream(proxyPropertiesFile);
			try {
				proxyProperties.load(in);
			} finally {
				in.close();
			}

			if (proxyProperties.getProperty(HTTP_PROXY_PROPERTY_HTTP_INVOKER_URL) != null ||
					proxyProperties.getProperty(HTTP_PROXY_PROPERTY_HTTPS_INVOKER_URL) != null)
				proxyPropertiesAvailable = true;
		}
		if (! proxyPropertiesAvailable)
			log.warn("There are no http(s) proxy properties defined in %server%/conf/prop/httpProxy.properties, hence " +
					"proxy generation is disabled.");
	}

	@Override
	protected void startService() throws Exception
	{
		long timestampBegin = System.currentTimeMillis();
		if (log.isTraceEnabled())
			log.trace("startService: Beginning.");

		super.startService();

		if (proxyPropertiesAvailable)
		{
			getServer().addNotificationListener(ejb3DeployerObjectName, ejb3DeployerListener, null, this);

  		// Look for all already deployed EJBs so that we can add the JNDI-aliases.
  		for (ObjectName objectName : getServer().queryNames(new ObjectName("jboss.j2ee:*"), null)) {
  			if (objectName.getKeyProperty("module") != null)
  				createRemoteProxies(objectName);
  		}
		}

		if (log.isTraceEnabled())
			log.trace("startService: Done in " + (System.currentTimeMillis() - timestampBegin) + " msec.");
	}

	@Override
	protected void stopService() throws Exception
	{
		long timestampBegin = System.currentTimeMillis();
		if (log.isTraceEnabled())
			log.trace("stopService: Beginning.");

		// Remove all proxies that we have added.
		Collection<ObjectName> ejb3ModuleNames;
		synchronized (ejbInterface2ProxyDeployers) {
			ejb3ModuleNames = new ArrayList<ObjectName>(ejb3ModuleName2ejbInterface2ProxyDeployers.keySet());
		}

		for (ObjectName ejb3ModuleName : ejb3ModuleNames) {
			try {
				destroyRemoteProxies(ejb3ModuleName, true);
			} catch (Throwable t) {
				log.warn("stopService: " + t, t);
			}
		}

		// Remove the listener.
		getServer().removeNotificationListener(ejb3DeployerObjectName, ejb3DeployerListener);

		super.stopService();

		if (log.isTraceEnabled())
			log.trace("stopService: Done in " + (System.currentTimeMillis() - timestampBegin) + " msec.");
	}

	private NotificationListener ejb3DeployerListener = new NotificationListener() {
		@Override
		public void handleNotification(Notification notification, Object handback) {
			try {
				if (SubDeployer.START_NOTIFICATION.equals(notification.getType())) {
					DeploymentInfo deploymentInfo = (DeploymentInfo) notification.getUserData();
					createRemoteProxies(deploymentInfo.deployedObject);
				}
				else if (SubDeployer.DESTROY_NOTIFICATION.equals(notification.getType())) {
					DeploymentInfo deploymentInfo = (DeploymentInfo) notification.getUserData();
					destroyRemoteProxies(deploymentInfo.deployedObject);
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
	 * Important: Use this object as mutex for accessing it as well as the ejb3ModuleName2ejbInterface2ProxyDeployers map.
	 * (synchronized block)!
	 * </p>
	 * <p>Additional Information:
	 * 	The ProxyDeployer represents the interface through which all remote proxies for one remote interface are created,
	 * 	bound to JNDI and unbound. For every remote proxy, a {@link RemoteBinding} has to be declared. On ProxyDeployer.start()
	 * </p>
	 */
	private Map<Class<?>, Set<ProxyDeployer>> ejbInterface2ProxyDeployers = new HashMap<Class<?>,Set<ProxyDeployer>>();

	/**
	 * Maps from each module, like JFireBaseBean, etc, to the mapping of remote interface to ProxyDeployer.
	 *
	 * Important: Use {@link #ejbInterface2ProxyDeployers} as mutex for accessing this <code>Map</code>!
	 */
	private Map<ObjectName, Map<Class<?>, Set<ProxyDeployer>>> ejb3ModuleName2ejbInterface2ProxyDeployers =
		new HashMap<ObjectName, Map<Class<?>,Set<ProxyDeployer>>>();

	/**
	 * Maps all the ProxyDeployer to the EJBDescriptor containing information about the bean from which the ProxyDeployer
	 * was created to provide meaningful debug/error output.
	 */
	private Map<ProxyDeployer, EjbDescriptor> proxyDeployer2EjbDescriptor = new HashMap<ProxyDeployer, EjbDescriptor>();

	/**
	 * Create proxies for all session beans (stateless or stateful) that are contained in the
	 * EJB3 module specified by the given <code>ejb3ModuleName</code>.
	 * <p>Proxy settings: <br>
	 * 	To generate the proxies, the JBoss ProxyDeployer is used, it creates Proxies according to the {@link RemoteBinding}
	 * 	of the bean it is attached to. We therefore modify (override) these bindings.
	 * 	The chronological order of events is:
	 * 	<nl>
	 * 		<li>The JBoss EJB3 Deployer creates its default bindings and generates the proxies.</li>
	 * 		<li>We create our bindings, store the original ones in the {@link EjbDescriptor} and override the JBoss
	 * 			generated / existing ones.</li>
	 * 		<li>The ProxyDeployer leverages these RemoteBindings to generate the wanted proxies according to the information
	 * 			read from {@link #proxyProperties}.</li>
	 * 		<li>When this service is stopped or a Bean undeployed, we try to restore the original bindings.</li>
	 * 	</nl>
	 *
	 * 	We therefore
	 * </p>
	 *
	 * @param ejb3ModuleName The name of the EJB3 module.
	 * @throws NamingException If accessing JNDI fails.
	 */
	protected void createRemoteProxies(ObjectName ejb3ModuleName)
	throws NamingException
	{
		if (log.isDebugEnabled())
			log.debug("createRemoteProxies for remote http: Beginnning for: " + ejb3ModuleName);

		final String httpInvokerURL = proxyProperties.getProperty(HTTP_PROXY_PROPERTY_HTTP_INVOKER_URL);
		final String httpsInvokerURL = proxyProperties.getProperty(HTTP_PROXY_PROPERTY_HTTPS_INVOKER_URL);

		Ejb3ModuleMBean ejb3ModuleMBean = DeployerUtil.getEjb3ModuleMBean(ejb3ModuleName, server);
		for (SessionContainer container : DeployerUtil.getSessionContainers(ejb3ModuleMBean)) {
			Class<?> ejbClass = container.getClazz();
			// It seems like these old Bindings are always null. Do we get some kind of Proxy object or are there several Bean description objects for one Bean? (marius)
			RemoteBindings oldBindings = (RemoteBindings) container.getAnnotations().getClassAnnotations().get(RemoteBindings.class);
			EjbDescriptor ejbDesc = new EjbDescriptor(container.getObjectName(), ejbClass, ejb3ModuleName, oldBindings);
			Set<Class<?>> ejbInterfaces = DeployerUtil.getEjbInterfaces(EjbInterfaceType.remote, ejbClass);

			for (Class<?> ejbInterface : ejbInterfaces)
			{
				// For a better understanding have a look at org.jboss.ejb3.ProxyDeployer.

				String jndiSimpleName = ejbInterface.getName();

				// 1. Create RemoteBindings for every Bean (http, https)
				List<RemoteBinding> remoteBindings = new ArrayList<RemoteBinding>(2);
				if (httpInvokerURL != null && httpInvokerURL.trim().length() > 7) // "http://".length == 7
				{
					// FIXME: What in case we need to use a different stack than the default one ("")?
					remoteBindings.add(new RemoteBindingImpl(JNDI_HTTP_EJB_PREFIX + jndiSimpleName, "", httpInvokerURL, RemoteProxyFactory.class));
				}
				if (httpsInvokerURL != null && httpsInvokerURL.trim().length() > 7)
				{
					remoteBindings.add(new RemoteBindingImpl(JNDI_HTTPS_EJB_PREFIX + jndiSimpleName, "", httpsInvokerURL, RemoteProxyFactory.class));
				}
				// what about the old Bindings for which a proxy already exists?
				// -> When I set the annotations I override the existing remotebindings
				// -> oldBindings are stored in the EjbDescriptor and restored on undeploy of this service.
				container.getAnnotations().addClassAnnotation(RemoteBindings.class, new RemoteBindingsImpl(remoteBindings));

				// 1b. In case of clustering create Clustered annotations
				// TODO: add @Clustered Annotation like the @RemoteBinding above if needed.

				// 2. Create ProxyDeployer for each Bean
				final ProxyDeployer newProxyDeployer = new ProxyDeployer(container);

				// 3. proxyDeployer.initializeRemoteBindingMetadata
				newProxyDeployer.initializeRemoteBindingMetadata();

				// The collection of all ProxyDeployers managing Proxies for the same interface.
				Collection<ProxyDeployer> proxyDeployers;
				synchronized (ejbInterface2ProxyDeployers)
				{
					Map<Class<?>, Set<ProxyDeployer>> m1 = ejb3ModuleName2ejbInterface2ProxyDeployers.get(ejb3ModuleName);
					if (m1 == null) {
						m1 = new HashMap<Class<?>, Set<ProxyDeployer>>();
						ejb3ModuleName2ejbInterface2ProxyDeployers.put(ejb3ModuleName, m1);
					}
					Set<ProxyDeployer> proxyDeployers_thisModule = m1.get(ejbInterface);
					if (proxyDeployers_thisModule == null) {
						proxyDeployers_thisModule = new HashSet<ProxyDeployer>();
						m1.put(ejbInterface, proxyDeployers_thisModule);
					}
					proxyDeployers_thisModule.add(newProxyDeployer);

					Set<ProxyDeployer> proxyDeployers_allModules = ejbInterface2ProxyDeployers.get(ejbInterface);
					if (proxyDeployers_allModules == null)
					{
						proxyDeployers_allModules = new HashSet<ProxyDeployer>();
						ejbInterface2ProxyDeployers.put(ejbInterface, proxyDeployers_allModules);
					}
					proxyDeployers_allModules.add(newProxyDeployer);

					if (proxyDeployers_allModules.size() == 1)
						proxyDeployers = null;
					else
						proxyDeployers = new ArrayList<ProxyDeployer>(proxyDeployers_allModules);

					proxyDeployer2EjbDescriptor.put(newProxyDeployer, ejbDesc);
				}

				if (proxyDeployers == null)
				{
					try
					{
						// creates and binds the Proxies
						newProxyDeployer.start();
					}
					catch (Exception e)
					{
						log.error("Couldn't create the Http Proxies for " + ejbDesc.getEjbClass() + " deployed as "
								+ ejbDesc.getEjb3ModuleName() + "!", e);
					}
				}
				else {
					log.warn("createRemoteProxies for remote http: Duplicate use of EJB interface \"" + ejbInterface.getName() + "\"!");
					log.warn("createRemoteProxies for remote http: The following " + proxyDeployers.size() + " EJBs share the same EJB interface:");

					for (ProxyDeployer deployer : proxyDeployers) {
						final EjbDescriptor curEjbDesc = proxyDeployer2EjbDescriptor.get(deployer);
						log.warn("createRemoteProxies for remote http:   * class \"" + curEjbDesc.getEjbClass().getName() +
								"\" deployed as \"" + curEjbDesc.getSessionContainerName() + "\"");
					}
					// undeploy all proxies for the ambiguous interface
					for (ProxyDeployer deployer : proxyDeployers)
					{
						// the proxies weren't created and bound to JNDI so skip the currently processed RemoteInterface
						if (deployer == newProxyDeployer)
							continue;

						try {
							deployer.stop();
						}
						catch (NameNotFoundException x) {
							// If the proxy is already gone -> nothing to do.
						}
						catch (Exception e)
						{
							// It seems like only JNDIExceptions may be thrown, but I am not sure. (marius)
							// In case of only JNDIExceptions, we could silently ignore them, since if there wasn't a proxy anymore,
							// then we are already done.
							final EjbDescriptor curEjbDesc = proxyDeployer2EjbDescriptor.get(deployer);
							log.error("Couldn't stop the ProxyDeployer for " + curEjbDesc.getEjbClass().getName() +
									" deployed as " + curEjbDesc.getSessionContainerName() + "!", e);
						}
					}
				}
			}
		}

		if (log.isDebugEnabled())
			log.debug("createRemoteProxies for remote http: Done for: " + ejb3ModuleName);
	}

	/**
	 * Remove all proxies that have been added for an EJB3 module. If the disappearing of this
	 * EJB3 module causes duplicate usages of remote interfaces not to be duplicate anymore, this
	 * method will re-initialise the affected EJB3 modules (and thus add the previously suppressed proxies).
	 *
	 * @param ejb3ModuleName the name of the EJB3 module.
	 * @throws NamingException if a fundamental JNDI error occurs.
	 */
	protected void destroyRemoteProxies(ObjectName ejb3ModuleName)
	throws NamingException
	{
		destroyRemoteProxies(ejb3ModuleName, false);
	}

	/**
	 * Remove all proxies that have been added for an EJB3 module. If <code>createUniqueBindings == true</code> and the
	 * disappearing of this EJB3 module causes duplicate usages of remote interfaces not to be duplicate anymore, this
	 * method will re-initialise the affected EJB3 modules (and thus add the previously suppressed proxies).
	 *
	 * @param ejb3ModuleName The name of the EJB3 module.
	 * @param serviceStopped This has two effects, when the set to <code>true</code>:
	 * 	<nl>
	 * 		<li>NO additional proxies are generated if, after the removal off all proxies of the given ejb module, an interface
	 * 			to proxy mapping is now unique. Otherwise proxies are created and bound to JNDI).</li>
	 * 		<li>We reset the original RemoteBingings (JBoss generated or manually set) to all instances of the beans.</li>
	 * 	</nl>
	 * @throws NamingException if a fundamental JNDI error occurs.
	 */
	protected void destroyRemoteProxies(ObjectName ejb3ModuleName, boolean serviceStopped)
	throws NamingException
	{
		if (log.isDebugEnabled())
			log.debug("destroyRemoteProxies for remote http: Beginning for: " + ejb3ModuleName);

		Set<ObjectName> ejb3ModuleNamesRequiringReprocessing = new HashSet<ObjectName>();

		final Ejb3ModuleMBean ejb3ModuleMBean = DeployerUtil.getEjb3ModuleMBean(ejb3ModuleName, server);
		final Map<Class<?>, Set<ProxyDeployer>> interface2DeployerToRemove;
		synchronized (ejbInterface2ProxyDeployers)
		{
			interface2DeployerToRemove = ejb3ModuleName2ejbInterface2ProxyDeployers.remove(ejb3ModuleName);
			if (interface2DeployerToRemove != null && !interface2DeployerToRemove.isEmpty())
			{
				final Map<ObjectName, EjbDescriptor> beanName2EjbDescriptors = new HashMap<ObjectName, EjbDescriptor>();

				for (Map.Entry<Class<?>, Set<ProxyDeployer>> entry : interface2DeployerToRemove.entrySet())
				{
					Class<?> remoteInterface = entry.getKey();
					Set<ProxyDeployer> deployer_thisModule = entry.getValue();
					Set<ProxyDeployer> deployer_allModules = ejbInterface2ProxyDeployers.get(remoteInterface);
					if (deployer_allModules != null)
					{
						int deployer_allModules_sizeBefore = deployer_allModules.size();
						deployer_allModules.removeAll(deployer_thisModule);

						if (!serviceStopped && deployer_allModules.size() == 1 && deployer_allModules_sizeBefore > 1)
						{
							// We need to create the JNDI aliases now, since they were unregistered before
							// due to duplicate remote interfaces, but don't are duplicate any longer.
							ejb3ModuleNamesRequiringReprocessing.add(
									proxyDeployer2EjbDescriptor.get(deployer_allModules.iterator().next()).getEjb3ModuleName()
							);
						}

						if (deployer_allModules.isEmpty())
							ejbInterface2ProxyDeployers.remove(remoteInterface);

						for (ProxyDeployer deployer : deployer_thisModule)
						{
							try
							{
								// unbind proxy from JNDI
								deployer.stop();
								// remove ejb Information
								final EjbDescriptor ejbDesc = proxyDeployer2EjbDescriptor.remove(deployer);
								if (serviceStopped)
								{
									if (beanName2EjbDescriptors.get(ejbDesc.getSessionContainerName()) ==  null)
										beanName2EjbDescriptors.put(ejbDesc.getSessionContainerName(), ejbDesc);
								}
							}
							catch (NameNotFoundException x) {
								// According to the javadoc, we shouldn't get an exception, if it does not exist; but I just encountered one.
								// Hence, we silently ignore it. If it's not existing, we already have what we want. Marco.
							}
							catch (Exception e)
							{
								final EjbDescriptor curEjbDesc = proxyDeployer2EjbDescriptor.get(deployer);
								log.error("Couldn't stop the ProxyDeployer for " + curEjbDesc.getEjbClass().getName() +
										" deployed as " + curEjbDesc.getSessionContainerName() + "!\n" +
										"If it is already or still unbound, then this message can be ignored.", e);
							}
						}
					}
				}

				// reset old RemoteBindings
				// I've not encountered a case, where the ejbDescriptor.getOriginalBindings wasn't null... (marius)
				if (serviceStopped)
				{ // Reset the original RemoteBindings to all instances of the beans modified. This is only needed when
					// THIS service shuts down, otherwise the Beans have been removed and we can skip this.
					for (SessionContainer container : DeployerUtil.getSessionContainers(ejb3ModuleMBean))
					{
						if (DeployerUtil.getRemoteInterfaces(container.getBeanClass()).isEmpty())
							continue;

						EjbDescriptor ejbDescriptor = beanName2EjbDescriptors.get(container.getObjectName());
						if (ejbDescriptor == null)
							log.error("Found a Bean having a remote interface declared, but no ejbDescriptor is available for it!" +
									"Bean: "+container.getDeploymentQualifiedName());

						if (ejbDescriptor == null || ejbDescriptor.getOriginalBindings() == null)
							continue;

						container.getAnnotations().addClassAnnotation(RemoteBindings.class, ejbDescriptor.getOriginalBindings());
					}
				}
			}

			// create and bind unique Beans, i.e. their proxies
			if (!ejb3ModuleNamesRequiringReprocessing.isEmpty())
			{
				for (ObjectName ejb3ModuleNameRequiringReprocessing : ejb3ModuleNamesRequiringReprocessing)
				{
					final Map<Class<?>, Set<ProxyDeployer>> interface2Deployer =
						ejb3ModuleName2ejbInterface2ProxyDeployers.get(ejb3ModuleNameRequiringReprocessing);

					for (Set<ProxyDeployer> deployers : interface2Deployer.values())
					{
						for (ProxyDeployer deployer : deployers)
						{
							try
							{
								deployer.start();
							}
							catch (Exception e)
							{
								final EjbDescriptor ejbDesc = proxyDeployer2EjbDescriptor.get(deployer);
								log.error("Couldn't create the Http Proxies for " + ejbDesc.getEjbClass() + " deployed as "
										+ ejbDesc.getEjb3ModuleName() + "!", e);
							}
						}
					}
				}
			}
		}

		if (log.isDebugEnabled())
			log.debug("destroyRemoteProxies for remote http: Done for: " + ejb3ModuleName);
	}

}
