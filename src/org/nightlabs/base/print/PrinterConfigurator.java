/**
 * 
 */
package org.nightlabs.base.print;

import org.eclipse.swt.widgets.Composite;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface PrinterConfigurator {
	
	public void init(PrinterConfiguration printerConfiguration);
	
	public Composite showComposite(Composite parent);
	
	public PrinterConfiguration readPrinterConfiguration();
	
	public void discardComposite();
	
//	public void assignPrinterConfigurationInterface(PrinterConfiguration printerConfiguration, PrinterInterface printerInterface);
}
