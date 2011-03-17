package org.nightlabs.singleton;

/**
 * Abstract implementation of a {@link ISingletonProvider}. Use this as
 * base-class for your implementations.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [dOt] de -->
 * 
 * @param <C>
 */
public class AbstractSingletonProvider<C> {
	
	/** The factory set for this provider */
	protected ISingletonProvider.ISingletonFactory<C> factory;
	
	/**
	 * {@inheritDoc}
	 */
	public void setFactory(ISingletonProvider.ISingletonFactory<C> factory) {
		if (this.factory == null)
			this.factory = factory;
		else
			throw new IllegalStateException();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isFactorySet() {
		return factory != null;
	}
}
