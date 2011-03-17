package org.nightlabs.singleton;

/**
 * Interface that allows for the creation and serving of 'singletons'. This
 * interface is used when 'singletons' are needed not as singletons within one
 * java-vm only but as 'singletons' for a specific scop like for example a
 * session of a web-system.
 * 
 * @author Alexey Aristov
 * @author Alexander Bieber <!-- alex [AT] nightlabs [dOt] de -->
 * 
 * @param <C>
 *            The type of singleton created.
 */
public interface ISingletonProvider<C> {

	/**
	 * Interface for a factory that creates instances of type C.
	 * 
	 * @param <C>
	 *            The type of singleton created.
	 */
	public interface ISingletonFactory<C> {
		C makeInstance();
	}

	/**
	 * Set the singleton-factory used by this provider to create new instances.
	 * 
	 * @param factory
	 *            The factory to set.
	 */
	void setFactory(ISingletonFactory<C> factory);

	/**
	 * @return Whether or not the {@link ISingletonFactory} is set for this
	 *         provider.
	 */
	boolean isFactorySet();

	/**
	 * @return The instance of type C that serves as singleton for the current
	 *         scope.
	 */
	C getInstance();
}
