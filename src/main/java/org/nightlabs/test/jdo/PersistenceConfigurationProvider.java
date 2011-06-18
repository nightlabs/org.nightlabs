package org.nightlabs.test.jdo;

import java.util.Map;

public interface PersistenceConfigurationProvider {
	Map<Object, Object> getPersistenceConfiguration(String connectionUrl);
}
