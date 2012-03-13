package org.nightlabs.test.selenium;

import java.lang.reflect.Method;

import org.junit.runners.model.FrameworkMethod;
import org.openqa.selenium.WebDriver;

/**
 * @author Marc Klinger - mklinger[at]nightlabs[dot]de
 */
public class DriverBoundFrameworkMethod extends FrameworkMethod {
	private final Class<WebDriver> driverClass;

	public DriverBoundFrameworkMethod(final Method method, final Class<WebDriver> driverClass) {
		super(method);
		this.driverClass = driverClass;
	}

	/**
	 * Get the driverClass.
	 * @return the driverClass
	 */
	public Class<WebDriver> getDriverClass() {
		return driverClass;
	}
}
