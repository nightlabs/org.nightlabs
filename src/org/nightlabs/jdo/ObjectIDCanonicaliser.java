package org.nightlabs.jdo;

import java.util.HashSet;
import java.util.Set;

import org.nightlabs.util.Canonicaliser;

public class ObjectIDCanonicaliser
extends Canonicaliser
{
	private static ObjectIDCanonicaliser sharedInstance = new ObjectIDCanonicaliser();
	static {
		Set<Class<?>> replaceableTypes = new HashSet<Class<?>>(sharedInstance.getReplaceableTypes());
		replaceableTypes.add(ObjectID.class);
		sharedInstance.setReplaceableTypes(replaceableTypes);
//		sharedInstance.freezeConfiguration();
	}

	/**
	 * Get the JVM-wide shared instance. This class makes only sense in a JVM-wide context.
	 * @return the JVM-wide shared instance.
	 */
	public static ObjectIDCanonicaliser sharedInstance() { return sharedInstance; }

}
