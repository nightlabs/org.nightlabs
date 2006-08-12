/**
 * 
 */
package org.nightlabs.base.print;

import java.awt.print.PrinterJob;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.composite.XComposite;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PrinterConfiguratorDefault implements PrinterConfigurator {

	private static Logger logger = Logger.getLogger(PrinterConfiguratorDefault.class); 
	
	private PrinterConfiguration initialConfiguration;
	private PrinterConfiguratorComposite configuratorComposite;
	
	/**
	 * 
	 */
	public PrinterConfiguratorDefault() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.print.PrinterConfigurator#discardComposite()
	 */
	public void discardComposite() {
		configuratorComposite = null;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.print.PrinterConfigurator#init(org.nightlabs.base.print.PrinterConfiguration)
	 */
	public void init(PrinterConfiguration printerConfiguration) {
		if (printerConfiguration != null)
			this.initialConfiguration = (PrinterConfiguration)printerConfiguration;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.print.PrinterConfigurator#readPrinterConfiguration()
	 */
	public PrinterConfiguration readPrinterConfiguration() {
		if (configuratorComposite != null && !configuratorComposite.isDisposed())
			return configuratorComposite.readPrinterConfiguration();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.print.PrinterConfigurator#showComposite(org.eclipse.swt.widgets.Composite)
	 */
	public Composite showComposite(Composite parent) {
		configuratorComposite = new PrinterConfiguratorComposite(
				parent, SWT.NONE, XComposite.LayoutMode.ORDINARY_WRAPPER
			);
		configuratorComposite.init(initialConfiguration);
		return configuratorComposite;
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.base.print.PrinterConfigurator#assignPrinterConfigurationInterface(org.nightlabs.base.print.PrinterConfiguration, org.nightlabs.base.print.PrinterInterface)
	 */
	public void assignPrinterConfigurationInterface(
			PrinterConfiguration printerConfiguration, PrinterInterface printerInterface
		) 
	{
		// TODO Auto-generated method stub
	}

}
