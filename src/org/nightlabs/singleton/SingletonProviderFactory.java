package org.nightlabs.singleton;

import org.nightlabs.singleton.ISingletonProvider.ISingletonFactory;

/**
 * The {@link SingletonProviderFactory} is the central entry-point for the
 * singletons package. It is configured with a class that implements the
 * {@link ISingletonProvider} interface ({@link #setProviderClass(Class)}). This
 * class is then used (instantiated) to create instances of the configured
 * implementation of {@link ISingletonProvider} in the createProvider*-Methods.
 * <p>
 * Use these methods (createProvider*-methods) to create vm-wide singletons of
 * the providers which you then can use to create 'singletons' for your current
 * scope. The class set in {@link #setProviderClass(Class)} defines how scope is
 * determined and when a new 'singleton'-instance is to be created.
 * </p>
 * <p>
 * Here is an example:
 * <br>
 * Class SingletonAccessor holds a static reference to a SingletonProvider. 
 * It creates this reference using the {@link SingletonProviderFactory}: 
 * <pre>
 * public class SingletonAccessor {
 *   private static final ISingletonProvider<SingletonAccessor> singletonProvider = SingletonProviderFactory.createProviderForClass(SingletonAccessor.class);
 * }
 * </pre>
 * Additionally the singleton has an instance-method:
 * <pre>
 *   protected void doSomething() {
 *     System.out.println(this);
 *   }
 * </pre>
 * It then uses the provider to access the 'singleton'-instance in its scope:
 * <pre>
 *   public static void doSomethingWithASingleton() {
 *     singletonProvider.getInstance().doSomething();
 *   }
 * </pre>
 * </p>
 * 
 * @author Alexey Aristov
 * @author Alexander Bieber <!-- alex [AT] nightlabs [dOt] de -->
 * 
 */
public class SingletonProviderFactory {
	
	/** Class implementing {@link ISingletonProvider} this factory was configured with. */
	@SuppressWarnings("unchecked")
	private static Class<? extends ISingletonProvider> providerClass;

	/**
	 * Use this method to configure what type of {@link ISingletonProvider}s this factory will create in its createProvider-methods.
	 * 
	 * @param clazz The type of {@link ISingletonProvider} to create.
	 */
	@SuppressWarnings("unchecked")
	public static void setProviderClass(Class<? extends ISingletonProvider> clazz) {
		providerClass = clazz;
	}

	/**
	 * Creates a new {@link ISingletonProvider} by instantiating the class
	 * configured with {@link #setProviderClass(Class)}. If no class was set it
	 * will fall-back to {@link SimpleSingletonProvider}.
	 * <p>
	 * Note, that a {@link ISingletonProvider}s are likely to use its
	 * {@link ISingletonFactory} to actually create instances and the
	 * {@link ISingletonProvider} created here is likely to not have such a
	 * factory configured. This of course depends on the class this factory was
	 * configured with.
	 * </p>
	 * 
	 * @param <C> The type of singleton to create.
	 * @return A newly created {@link ISingletonProvider}.
	 */
	@SuppressWarnings("unchecked")
	public static <C> ISingletonProvider<C> createProvider() {
		try {
			ISingletonProvider<C> provider;

			if (providerClass == null)
				provider = new SimpleSingletonProvider<C>();
			else {
				provider = (ISingletonProvider<C>) providerClass.newInstance();
			}

			return provider;
		} catch (Exception aEx) {
			throw new RuntimeException(aEx);
		}
	}

	/**
	 * Creates a new {@link ISingletonProvider} by instantiating the class this
	 * factory was configured with in {@link #setProviderClass(Class)}. The
	 * newly created provider will then be configured with the
	 * {@link ISingletonFactory} provided as parameter.
	 * 
	 * @param <C> The type of singleton to create.
	 * @param factory The factory that should be set to the newly created {@link ISingletonProvider}.
	 * @return An {@link ISingletonProvider} configured with the given {@link ISingletonFactory}.
	 */
	public static <C> ISingletonProvider<C> createProviderForFactory(ISingletonFactory<C> factory) {
		ISingletonProvider<C> provider = createProvider();
		provider.setFactory(factory);
		return provider;
	}

	/**
	 * Creates a new {@link ISingletonProvider} by instantiating the class this
	 * factory was configured with in {@link #setProviderClass(Class)}. The
	 * newly created provider will then be configured with a
	 * {@link ISingletonFactory} that will always return the instance provided
	 * as parameter. This means the created {@link ISingletonProvider} will
	 * serve the same instance (the one provided here as parameter) for every
	 * scope it is asked for an instance.
	 * 
	 * @param <C> The type of singleton to created
	 * @param instance The instance the newly created {@link ISingletonProvider} should serve.
	 * @return A newly created {@link ISingletonProvider} that will serve the given instance.
	 */
	public static <C> ISingletonProvider<C> createProviderForInstance(final C instance) {
		return createProviderForFactory(new ISingletonFactory<C>() {
			@Override
			public C makeInstance() {
				return instance;
			}
		});
	}
	
	/**
	 * Creates a new {@link ISingletonProvider} by instantiating the class this
	 * factory was configured with in {@link #setProviderClass(Class)}. The
	 * newly created provider will then be configured with a
	 * {@link ISingletonFactory} that will create new instances of the class
	 * provided as parameter. This means the created {@link ISingletonProvider}
	 * will serve a new instance of the provided class for each scope it is
	 * asked for an instance.
	 * 
	 * @param <C> The type of singletons to create.
	 * @param clazz The type of singletons to create. 
	 * @return A newly created {@link ISingletonProvider} that will serve instances of the given class.
	 */
	public static <C> ISingletonProvider<C> createProviderForClass(final Class<? extends C> clazz) {
		return createProviderForFactory(new ISingletonFactory<C>() {
			@Override
			public C makeInstance() {
				try {
					return clazz.newInstance();
				} catch (Exception e) {
					throw new RuntimeException("Cannot instantiate singleton", e);
				}
			}
		});
	}
}
