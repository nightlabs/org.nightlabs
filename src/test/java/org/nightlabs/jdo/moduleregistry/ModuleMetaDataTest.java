package org.nightlabs.jdo.moduleregistry;

import java.lang.reflect.Type;

import org.nightlabs.test.jdo.PersistenceCapableTestBase;

public class ModuleMetaDataTest extends PersistenceCapableTestBase<ModuleMetaData> {
	public ModuleMetaDataTest() {
		super(ModuleMetaData.class);
	}

	@Override
	protected Object createValue(Type type, String propertyName) {
		if (type == String.class && "schemaVersion".equals(propertyName)) {
			return createValue(Integer.class, propertyName + "-1") + "." +
				createValue(Integer.class, propertyName + "-2") + "." +
				createValue(Integer.class, propertyName + "-3") + "." +
				createValue(Integer.class, propertyName + "-4");
		}
		return super.createValue(type, propertyName);
	}
}
