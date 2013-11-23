package org.nightlabs.test.selenium;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * @author Marc Klinger - mklinger[at]nightlabs[dot]de
 */
public class SeleniumTestRunner extends BlockJUnit4ClassRunner {

	private ApplicationUnderTestLauncher applicationUnderTest;
	private WebDriver webDriver;
	private SeleniumSettings seleniumSettings;

	/** Constructor. */
	public SeleniumTestRunner(final Class<?> klass) throws InitializationError {
		super(klass);
	}

	private SeleniumSettings getSeleniumSettings() {
		if (seleniumSettings == null) {
			try {
				seleniumSettings = createSeleniumSettings();
			} catch (final IOException e) {
				throw new RuntimeException("Error creating selenium settings");
			}
		}
		return seleniumSettings;
	}

	private class StartUpClass extends Statement {
		private final Statement next;

		public StartUpClass(final Statement next) {
			this.next = next;
		}

		@Override
		public void evaluate() throws Throwable {
			beforeClass();
			next.evaluate();
		}
	}

	private void beforeClass() {
		if (!getSeleniumSettings().isApplicationPerTest()) {
			createApplicationUnderTest();
		}
	}

	private void createApplicationUnderTest() {
		try {
			applicationUnderTest = getSeleniumSettings().getApplicationUnderTest().newInstance();
		} catch (final Exception e) {
			throw new RuntimeException("Error creating ApplicationUnderTest instance");
		}
		applicationUnderTest.setSeleniumSettings(getSeleniumSettings());
		applicationUnderTest.start(getSeleniumSettings().getApplicationStartTimeoutMillis());
	}

	private class TearDownClass extends Statement {
		private final Statement before;

		public TearDownClass(final Statement before) {
			this.before = before;
		}

		@Override
		public void evaluate() throws Throwable {
			before.evaluate();
			afterClass();
		}
	}

	private void afterClass() {
		if (!getSeleniumSettings().isDriverPerTest()) {
			closeWebDriver();
		}
		if (!getSeleniumSettings().isApplicationPerTest()) {
			closeApplicationUnderTest();
		}
	}

	private void closeWebDriver() {
		try {
			if (webDriver != null) {
				webDriver.quit();
			}
		} catch (final Exception e) {
			// TODO log?
			e.printStackTrace();
		}
	}

	private void closeApplicationUnderTest() {
		try {
			if (applicationUnderTest != null) {
				applicationUnderTest.stop(getSeleniumSettings().getApplicationStopTimeoutMillis());
			}
		} catch (final Exception e) {
			// TODO log?
			e.printStackTrace();
		}
	}

	@Override
	protected Statement withBeforeClasses(final Statement statement) {
		return new StartUpClass(super.withBeforeClasses(statement));
	}

	@Override
	protected Statement withAfterClasses(final Statement statement) {
		return new TearDownClass(super.withAfterClasses(statement));
	}

	private SeleniumSettings createSeleniumSettings() throws IOException {
		final Properties defaultSettings = new Properties();
		final String defaultGlobalPropertiesName = "selenium-settings.properties";
		System.out.println("Looking for global default properties: " + defaultGlobalPropertiesName);
		final InputStream inGlobal = getTestClass().getJavaClass().getClassLoader().getResourceAsStream(defaultGlobalPropertiesName);
		loadProperties(defaultSettings, inGlobal);
		final String defaultPropertiesName = "selenium-settings-" + getTestClass().getJavaClass().getSimpleName() + ".properties";
		System.out.println("Looking for test class relative default properties: " + defaultPropertiesName);
		final InputStream inTest = getTestClass().getJavaClass().getResourceAsStream(defaultPropertiesName);
		loadProperties(defaultSettings, inTest);

		final ApplicationUnderTest applicationUnserTest = getTestClass().getJavaClass().getAnnotation(ApplicationUnderTest.class);
		if (applicationUnserTest != null) {
			// Annotation value overwrites the default properties but is overwritten by system properties
			defaultSettings.setProperty("selenium.applicationUnderTest", applicationUnserTest.value().getName());
		}
		return new SeleniumSettings(defaultSettings);
	}

	private void loadProperties(final Properties defaultSettings, final InputStream in) throws IOException {
		if (in != null) {
			try {
				System.out.println("Using default properties");
				defaultSettings.load(in);
			} finally {
				in.close();
			}
		} else {
			System.out.println("Not using default properties");
		}
	}

	@Override
	protected List<FrameworkMethod> computeTestMethods() {
		final List<FrameworkMethod> testMethods = super.computeTestMethods();
		final List<Class<WebDriver>> drivers = getSeleniumSettings().getDrivers();
		final List<FrameworkMethod> wrappedTestMethods = new ArrayList<FrameworkMethod>(testMethods.size() * drivers.size());
		for (final Class<WebDriver> driverClass : drivers) {
			for (final FrameworkMethod frameworkMethod : testMethods) {
				wrappedTestMethods.add(new DriverBoundFrameworkMethod(frameworkMethod.getMethod(), driverClass));
			}
		}
		return wrappedTestMethods;
	}

	@Override
	protected String testName(final FrameworkMethod method) {
		if (method instanceof DriverBoundFrameworkMethod) {
			final DriverBoundFrameworkMethod driverBoundFrameworkMethod = (DriverBoundFrameworkMethod) method;
			return method.getName() + "[" + driverBoundFrameworkMethod.getDriverClass().getSimpleName() + "]";
		}
		return super.testName(method);
	}

	@Override
	protected Statement methodInvoker(final FrameworkMethod method, final Object test) {
		return new InvokeMethod(method, test);
	}

	private class InvokeMethod extends Statement {
		private final FrameworkMethod testMethod;
		private final Object target;

		public InvokeMethod(final FrameworkMethod testMethod, final Object target) {
			this.testMethod = testMethod;
			this.target = target;
		}

		@Override
		public void evaluate() throws Throwable {
			if (getSeleniumSettings().isApplicationPerTest()) {
				createApplicationUnderTest();
			}
			DriverBoundFrameworkMethod driverBoundFrameworkMethod = null;
			if (testMethod instanceof DriverBoundFrameworkMethod) {
				driverBoundFrameworkMethod = (DriverBoundFrameworkMethod) testMethod;
			}
			if (driverBoundFrameworkMethod != null) {
				final Class<WebDriver> driverClass = driverBoundFrameworkMethod.getDriverClass();
				if (getSeleniumSettings().isDriverPerTest() || webDriver == null || driverClass != webDriver.getClass()) {
					closeWebDriver();
					// Workaround for IE to avoid security issues
					if ("org.openqa.selenium.ie.InternetExplorerDriver".equals(driverClass.getName())) {
						final DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
						ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
						webDriver = new InternetExplorerDriver(ieCapabilities);
					} else {
						webDriver = driverClass.newInstance();
					}
				}
			}
			if (webDriver instanceof HtmlUnitDriver) {
				boolean htmlUnitDriverJavascriptEnabled = Boolean.valueOf(getSeleniumSettings().getProperty(HtmlUnitDriver.class.getName() + ".javascriptEnabled", false));
				((HtmlUnitDriver) webDriver).setJavascriptEnabled(htmlUnitDriverJavascriptEnabled);
			}
			injectResources(target);
			try {
				testMethod.invokeExplosively(target);
			} catch (final Throwable e) {
				if (webDriver != null && webDriver instanceof TakesScreenshot) {
					final File scrFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
					final String name = SeleniumTestRunner.this.testName(testMethod) + "-" + scrFile.getName();
					final File targetFile = new File(name);
					FileUtils.copyFile(scrFile, targetFile);
					System.out.println("Created screenshot: " + targetFile.getAbsolutePath());
				}
				throw e;
			} finally {
				clearResources(target);
				if (getSeleniumSettings().isDriverPerTest()) {
					closeWebDriver();
				}
				if (getSeleniumSettings().isApplicationPerTest()) {
					closeApplicationUnderTest();
				}
			}
		}
	}

	// TODO consider using @Rule
	private void injectResources(final Object target) {
		final List<FrameworkField> annotatedFields = getTestClass().getAnnotatedFields(Resource.class);
		for (final FrameworkField frameworkField : annotatedFields) {
			final Field field = frameworkField.getField();
			try {
				field.setAccessible(true);
				injectResource(target, field);
			} catch (final Exception e) {
				throw new RuntimeException("Error injecting object of type " + field.getType().getName() + " into field " + field.getName());
			}
		}
	}

	private void injectResource(final Object target, final Field field) throws IllegalAccessException {
		final Class<?> fieldType = field.getType();
		if (fieldType.isAssignableFrom(applicationUnderTest.getClass())) {
			field.set(target, applicationUnderTest);
		} else if (fieldType.isAssignableFrom(getSeleniumSettings().getClass())) {
			field.set(target, getSeleniumSettings());
		} else if (fieldType.isAssignableFrom(webDriver.getClass())) {
			field.set(target, webDriver);
		}
	}

	private void clearResources(final Object target) {
		final List<FrameworkField> annotatedFields = getTestClass().getAnnotatedFields(Resource.class);
		for (final FrameworkField frameworkField : annotatedFields) {
			final Field field = frameworkField.getField();
			field.setAccessible(true);
			try {
				field.set(target, null);
			} catch (final Exception e) {
				// TODO log
				e.printStackTrace();
			}
		}
	}
}
