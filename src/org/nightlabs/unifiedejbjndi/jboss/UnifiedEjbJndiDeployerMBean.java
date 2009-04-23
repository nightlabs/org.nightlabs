package org.nightlabs.unifiedejbjndi.jboss;

import org.jboss.system.ServiceMBean;

/**
 * The MBean service interface for the {@link UnifiedEjbJndiDeployer}.
 * Since it does not provide any services via API but registers aliases
 * in JNDI, this interface is pretty empty.
 *
 * @author marco schulze - marco at nightlabs dot de
 * @see UnifiedEjbJndiDeployer
 */
public interface UnifiedEjbJndiDeployerMBean extends ServiceMBean
{
	public static final String JNDI_PREFIX_EJB_BY_REMOTE_INTERFACE = "ejb/byRemoteInterface/";
	public static final String JNDI_PREFIX_EJB_BY_LOCAL_INTERFACE = "ejb/byLocalInterface/";
	public static final String JNDI_PREFIX_EJB_META_DATA_BY_REMOTE_INTERFACE = "ejb/meta/byRemoteInterface/";
	public static final String JNDI_PREFIX_EJB_META_DATA_BY_LOCAL_INTERFACE = "ejb/meta/byLocalInterface/";
}
