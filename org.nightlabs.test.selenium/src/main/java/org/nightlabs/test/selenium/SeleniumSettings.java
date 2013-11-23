package org.nightlabs.test.selenium;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;


/**
 * Selenium settings from system properties or default properties.
 * @author Marc Klinger - mklinger[at]nightlabs[dot]de
 */
public class SeleniumSettings {
	private static final String PROPERTY_PREFIX = "selenium.";
	private static final String APPLICATION_UNDER_TEST_PROPERTY_INFIX = "applicationUnderTest.properties.";
	private final Properties defaultSettings;
	private final List<Class<WebDriver>> drivers;
	private final boolean driverPerTest;
	private final boolean applicationPerTest;
	private final Class<ApplicationUnderTestLauncher> applicationUnderTest;
	private final long applicationStartTimeoutMillis = 30000;
	private final long applicationStopTimeoutMillis = 5000;

	/** Default constructor. */
	public SeleniumSettings() {
		this(null);
	}

	/** Create settings with the given default settings. */
	public SeleniumSettings(final Properties defaultSettings) {
		this.defaultSettings = defaultSettings;
		applicationUnderTest = getClass(getProperty("applicationUnderTest", true));
		List<String> driverNames = getListProperty("drivers", false);
		if (driverNames == null) {
			driverNames = Collections.singletonList(HtmlUnitDriver.class.getName());
		}
		drivers = getDriversList(driverNames);
		driverPerTest = getBooleanProperty("driverPerTest", false);
		applicationPerTest = getBooleanProperty("applicationPerTest", false);
	}

	String getProperty(final String name, final boolean mandatory) {
		final String qualifiedName = PROPERTY_PREFIX + name;
		String value = System.getProperty(qualifiedName);
		if (value == null || value.isEmpty() && defaultSettings != null) {
			value = defaultSettings.getProperty(qualifiedName);
		}
		if (mandatory && (value == null || value.isEmpty())) {
			throw new IllegalStateException("Missing mandatory property " + qualifiedName);
		}
		return value;
	}

	/** Get an application property value. */
	public String getApplicationProperty(final String name, final boolean mandatory) {
		return getProperty(APPLICATION_UNDER_TEST_PROPERTY_INFIX + name, mandatory);
	}

	private List<String> getListProperty(final String name, final boolean mandatory) {
		final String value = getProperty(name, mandatory);
		if (value == null || value.isEmpty()) {
			return null;
		}
		final List<String> list = new ArrayList<String>();
		final StringTokenizer st = new StringTokenizer(value, ",");
		while (st.hasMoreTokens()) {
			list.add(st.nextToken().trim());
		}
		return list;
	}

	private boolean getBooleanProperty(final String name, final boolean mandatory) {
		final String value = getProperty(name, mandatory);
		if (value == null) {
			return false;
		}
		return Boolean.valueOf(value);
	}

	private List<Class<WebDriver>> getDriversList(final List<String> driverClassNames) {
		if (driverClassNames == null || driverClassNames.isEmpty()) {
			return null;
		}
		final List<Class<WebDriver>> result = new ArrayList<Class<WebDriver>>();
		for (final String className : driverClassNames) {
			final Class<WebDriver> clazz = getClass(className);
			result.add(clazz);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> getClass(final String className) {
		try {
			return (Class<T>) SeleniumSettings.class.getClassLoader().loadClass(className);
		} catch (final ClassNotFoundException e) {
			throw new RuntimeException("Invalid driver class: " + className, e);
		}
	}

	/**
	 * Get the drivers.
	 * @return the drivers
	 */
	public List<Class<WebDriver>> getDrivers() {
		return drivers;
	}

	/**
	 * Get the driverPerTest.
	 * @return the driverPerTest
	 */
	public boolean isDriverPerTest() {
		return driverPerTest;
	}

	/**
	 * Get the applicationPerTest.
	 * @return the applicationPerTest
	 */
	public boolean isApplicationPerTest() {
		return applicationPerTest;
	}

	/**
	 * Get the applicationUnderTest.
	 * @return the applicationUnderTest
	 */
	public Class<ApplicationUnderTestLauncher> getApplicationUnderTest() {
		return applicationUnderTest;
	}

	/** Get the application startup timeout in millis. */
	public long getApplicationStartTimeoutMillis() {
		return applicationStartTimeoutMillis;
	}

	/** Get the application shutdown timeout in millis. */
	public long getApplicationStopTimeoutMillis() {
		return applicationStopTimeoutMillis;
	}
}
