package org.nightlabs.singleton;

/**
 * The {@link SimpleSingletonProvider} will use the {@link ISingletonFactory} to
 * create exactly one instance of C. This means one instance of this provider
 * will correlate to one instance of C.
 * <p>
 * Note, that this provider will enhance {@link IServiceContextAware} instances
 * (C implements {@link IServiceContextAware}) with a ServiceContext created by
 * {@link #createServiceContext()}.
 * </p>
 * 
 * @author Alexey Aristov
 * @author Alexander Bieber <!-- alex [AT] nightlabs [dOt] de -->
 * 
 * @param <C>
 *            The type of singleton to create.
 */
public class SimpleSingletonProvider<C> extends AbstractSingletonProvider<C>
		implements ISingletonProvider<C> {

	/** The one instance that will be (or was) created by this provider */
	private C instance;

	@Override
	public C getInstance() {
		if (instance == null) {
			instance = factory.makeInstance();

			if (instance instanceof IServiceContextAware) {
				((IServiceContextAware) instance)
						.setServiceContext(createServiceContext());
			}
		}

		return instance;
	}

	/**
	 * Creates the {@link IServiceContext} an instance will be enhanced with.
	 * <p>
	 * This method might be overwritten in order to change the ServiceContext
	 * passed to {@link IServiceContextAware} singletons.
	 * </p>
	 * 
	 * @return The {@link IServiceContext} an instance will be enhanced with.
	 */
	protected IServiceContext createServiceContext() {
		return new IServiceContext() {
			@Override
			public void associateThread(Thread thread) {
			}

			@Override
			public void disposeThread(Thread thread) {
			}

			@Override
			public void associateThread() {
			}

			@Override
			public void disposeThread() {
			}
		};
	}
}
