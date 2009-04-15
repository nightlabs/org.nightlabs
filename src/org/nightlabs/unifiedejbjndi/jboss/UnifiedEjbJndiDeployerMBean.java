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
	public static final String JNDI_PREFIX_BY_REMOTE_INTERFACE = "ejb/byRemoteInterface/";
}
