/**
 * 
 */
package org.nightlabs.base.print;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PrinterConfiguratorFactoryDefault implements
		PrinterConfiguratorFactory {

	/**
	 * 
	 */
	public PrinterConfiguratorFactoryDefault() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.print.PrinterConfiguratorFactory#createPrinterConfigurator()
	 */
	public PrinterConfigurator createPrinterConfigurator() {
		return new PrinterConfiguratorDefault();
	}

}
