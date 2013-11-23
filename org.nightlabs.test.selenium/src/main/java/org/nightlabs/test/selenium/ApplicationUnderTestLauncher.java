package org.nightlabs.test.selenium;



/**
 * Application under test launcher interface.
 * @author Marc Klinger - mklinger[at]nightlabs[dot]de
 */
public interface ApplicationUnderTestLauncher {
	/** Set the settings. */
	void setSeleniumSettings(final SeleniumSettings seleniumSettings);

	/** Start the application. */
	void start(final long timeout);

	/** Shut down the application. */
	void stop(final long timeout);

	/** Get the application base url. */
	String getBaseUrl();
}
