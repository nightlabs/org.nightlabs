package org.nightlabs.test.jdo;

import java.util.HashMap;
import java.util.Map;

public class DefaultPersistenceConfigurationProvider implements PersistenceConfigurationProvider {
	@Override
	public Map<Object, Object> getPersistenceConfiguration(String connectionUrl) {
		Map<Object, Object> props = new HashMap<Object, Object>();

		//props.put("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.jdo.JDOPersistenceManagerFactory");
		props.put("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");

		props.put("javax.jdo.option.ConnectionURL", connectionUrl);
		props.put("javax.jdo.option.ConnectionDriverName", "org.apache.derby.jdbc.EmbeddedDriver");
		props.put("datanucleus.autoCreateSchema", "true");
		//props.put("javax.jdo.option.NontransactionalRead", "true");
		props.put("datanucleus.rdbms.stringDefaultLength", "255");

		//        <!--
		//    	*** NEW DN 2.1 ***
		//    	See: http://www.jpox.org/servlet/forum/viewthread_thread,6052
		//    	This is ignored by older DataNucleus versions before 2.1 (and not necessary at least for 1.1.3).
		//    -->
		props.put("datanucleus.query.checkUnusedParameters", "true");

		//    <!--
		//    	*** NEW DN 2.1 ***
		//    	To use the SchemaTable was default in older DataNucleus versions but has to be specified since DN 2.1.
		//    -->
		//props.put("datanucleus.autoStartMechanism", "SchemaTable");

		//	<!--
		//    	*** NEW DN 2.1 ***
		//    	See: http://www.jpox.org/servlet/jira/browse/NUCCORE-501
		//    -->
		//props.put("datanucleus.store.allowReferencesWithNoImplementations", "true");

		//    <!--
		//    	*** NEW DN 2.1 ***
		//    	See: http://www.datanucleus.org/products/accessplatform_2_0/migration.html
		//    	By default, DataNucleus 2.0 and newer uses the factory "datanucleus2", which uses less underscores (e.g. "authorityid" instead of "authority_id").
		//    	In order to reduce migration work, we continue using the old factory - at least for now. Marco.
		//    -->
		//props.put("datanucleus.identifierFactory", "datanucleus1");

		return props;
	}
}
