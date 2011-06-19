package org.nightlabs.test.jdo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.apache.commons.io.FileUtils;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDOTestRunner extends BlockJUnit4ClassRunner {

	private static final Logger LOG = LoggerFactory.getLogger(JDOTestRunner.class);
	private boolean classLevelDefaultRollback;
	private PersistenceManager persistenceManager;
	private Map<Field, Object> resources;

	public JDOTestRunner(Class<?> klass) throws InitializationError {
		super(klass);
		TransactionConfiguration transactionConfiguration = klass.getAnnotation(TransactionConfiguration.class);
		if(transactionConfiguration != null) {
			classLevelDefaultRollback = transactionConfiguration.defaultRollback();
		}
		createResources();
	}

	@Override
	protected Object createTest() throws Exception {
		Object test = super.createTest();
		injectResources(test);
		return test;
	}

	private void createResources() {
		resources = new HashMap<Field, Object>();
		List<FrameworkField> annotatedFields = getTestClass().getAnnotatedFields(JDOTestResource.class);
		for (FrameworkField frameworkField : annotatedFields) {
			Field field = frameworkField.getField();
			field.setAccessible(true);
			Type type = field.getGenericType();
			if(type instanceof Class<?>) {
				Class<?> clazz = (Class<?>)type;
				if(clazz == PersistenceManager.class) {
					resources.put(field, getPersistenceManager());
				}
			}
		}
	}

	private PersistenceManager getPersistenceManager() {
		if(persistenceManager == null) {
			persistenceManager = createPersistenceManager();
		}
		return persistenceManager;
	}

	private PersistenceManager createPersistenceManager() {
		String derbyBaseUrl = getConnectionUrl();
		Map<Object, Object> props = getPersistenceConfiguration(derbyBaseUrl);
		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(props);
		return pmf.getPersistenceManager();
	}

	protected Map<Object, Object> getPersistenceConfiguration(String connectionUrl) {
		PersistenceConfiguration persistenceConfiguration = getTestClass().getJavaClass().getAnnotation(PersistenceConfiguration.class);
		Class<? extends PersistenceConfigurationProvider> provider;
		if (persistenceConfiguration != null) {
			provider = persistenceConfiguration.provider();
		} else {
			provider = DefaultPersistenceConfigurationProvider.class;
		}
		try {
			return provider.newInstance().getPersistenceConfiguration(connectionUrl);
		} catch (Exception e) {
			throw new RuntimeException("Error creating persistence configuration provider " + provider.getName(), e);
		}
	}

	protected boolean useInMemoryDB() {
		return true;
	}

	protected String getConnectionUrl() {
		File derbyDir = createTmpDir();
		System.setProperty("derby.system.home", derbyDir.getAbsolutePath());
		if (useInMemoryDB()) {
			return "jdbc:derby:memory:testDb;create=true";
		} else {
			File dbDir = new File(derbyDir, "testDb");
			LOG.info("Derby database directory: " + dbDir.getAbsolutePath());
			return "jdbc:derby:" + dbDir.getAbsolutePath() + ";create=true";
		}
	}

	private File createTmpDir() {
		File tmpDir;
		File systemTmpDir = new File(System.getProperty("java.io.tmpdir"));
		int count = 1;
		int maxTries = 1000;
		String rand = String.valueOf(Math.random()).substring(2);
		do {
			tmpDir = new File(systemTmpDir, "derby-test-" + rand + "-" + count);
			count++;
			if (count > maxTries) {
				throw new RuntimeException("Error creating tmp dir in " + systemTmpDir.getAbsolutePath());
			}
		} while(tmpDir.exists() || !tmpDir.mkdirs());
		final File toDelete = tmpDir;
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (toDelete.exists()) {
					try {
						FileUtils.deleteDirectory(toDelete);
					} catch (IOException e) {
						LOG.error("Error deleting directory: " + toDelete.getAbsolutePath(), e);
					}
				}
			}
		});
		return tmpDir;
	}

	private void injectResources(Object test) throws IllegalArgumentException, IllegalAccessException {
		for(Map.Entry<Field, Object> e : resources.entrySet()) {
			e.getKey().set(test, e.getValue());
		}
	}

	@Override
	protected Statement methodInvoker(FrameworkMethod method, Object test) {
		return new InvokeMethod(method, test);
	}

	private class InvokeMethod extends Statement {
		private final FrameworkMethod fTestMethod;
		private final Object fTarget;
		private boolean defaultRollback;

		public InvokeMethod(FrameworkMethod testMethod, Object target) {
			fTestMethod= testMethod;
			fTarget= target;
			TransactionConfiguration transactionConfiguration = testMethod.getAnnotation(TransactionConfiguration.class);
			if(transactionConfiguration != null) {
				defaultRollback = transactionConfiguration.defaultRollback();
			} else {
				defaultRollback = classLevelDefaultRollback;
			}
		}

		@Override
		public void evaluate() throws Throwable {
			LOG.info("Begin transaction.");
			persistenceManager.currentTransaction().begin();
			try {
				fTestMethod.invokeExplosively(fTarget);
				if(!defaultRollback) {
					LOG.info("Commit transaction.");
					persistenceManager.currentTransaction().commit();
				}
			} finally {
				if(persistenceManager.currentTransaction().isActive() || defaultRollback) {
					LOG.info("Rollback transaction.");
					persistenceManager.currentTransaction().rollback();
				}
			}
		}
	}
}
