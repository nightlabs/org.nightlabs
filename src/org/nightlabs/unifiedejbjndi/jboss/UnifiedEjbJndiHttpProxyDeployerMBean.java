package org.nightlabs.unifiedejbjndi.jboss;

import org.jboss.system.ServiceMBean;

/**
 * JMX interface for the UnifiedEjbJndiHttpProxyDeployer which doesn't expose any functionality, hence its empty.
 *
 * @author Marius Heinzmann - marius[at]nightlabs[dot]de
 * @see UnifiedEjbJndiHttpProxyDeployer
 */
public interface UnifiedEjbJndiHttpProxyDeployerMBean
	extends ServiceMBean
{
	public static final String JNDI_PREFIX_EJB_BY_REMOTE_INTERFACE = UnifiedEjbJndiDeployerMBean.JNDI_PREFIX_EJB_BY_REMOTE_INTERFACE;
	public static final String JNDI_HTTP_EJB_PREFIX = JNDI_PREFIX_EJB_BY_REMOTE_INTERFACE + "http/";
	public static final String JNDI_HTTPS_EJB_PREFIX = JNDI_PREFIX_EJB_BY_REMOTE_INTERFACE + "https/";
	/* The following two subcontexts are not yet in use, but will be later when clustering is enabled. */
	public static final String JNDI_HTTPHA_EJB_PREFIX = JNDI_PREFIX_EJB_BY_REMOTE_INTERFACE + "httpha/";
	public static final String JNDI_HTTPSHA_EJB_PREFIX = JNDI_PREFIX_EJB_BY_REMOTE_INTERFACE + "httpsha/";
}
