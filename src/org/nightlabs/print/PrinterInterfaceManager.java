/* ********************************************************************
 * NightLabsBase - Utilities by NightLabs                             *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.print;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.log4j.Logger;

/**
 * Helper class that can be used to lookup
 * {@link PrinterConfiguration}s and obtain {@link PrinterInterface}s
 * pre-configured with these configurations.
 * 
 * To access different Type of interfaces, most methods take a
 * {@link PrinterInterfaceFactory} as parameter that will allow them
 * to handle uknown interface types. See the INTERFACE_FACTORY_* constants
 * in this class for possible values when using the manager.
 * 
 * This class is abstract as it does not implement user interaction to
 * edit {@link PrinterConfiguration}s, see {@link #editPrinterConfiguration(String)}.
 * Sub-class the manager in your (GUI-)framework and implement the edit method.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public abstract class PrinterInterfaceManager {
	
	/**
	 * Constant for the interface factory when using standard Java (Graphics) printing.
	 * Using this in the methods requiring an {@link PrinterInterfaceFactory} will cause
	 * them to return an {@link AWTPrinter}.
	 */
	public static PrinterInterfaceFactory INTERFACE_FACTORY_AWT = new AWTPrinter.Factory();
	/**
	 * Constant for the interface factory when printing documents.
	 * Using this in the methods requiring an {@link PrinterInterfaceFactory} will cause
	 * them to return an {@link DelegatingDocumentPrinter}.
	 */
	public static PrinterInterfaceFactory INTERFACE_FACTORY_DOCUMENT = new DelegatingDocumentPrinter.Factory();
	
	private static final Logger logger = Logger.getLogger(PrinterInterfaceManager.class);
	
	/**
	 * Returns a {@link PrinterInterface} associated to the PrintService configured
	 * for the given printerUseCase. Also the settings configured (PageFormat and other??)
	 * will be set for the returned PrinterInterface.
	 * 
	 * If no configuration was found, the user will be asked to configure one (
	 * see {@link #editPrinterConfiguration(String)}).
	 * 
	 * If configuration somehow fails for this method it will return null.
	 *
	 * @param interfaceFactory The factory to use when creating the PrinterInterface (prior to configuring it).
	 * @param printerUseCaseID The printerUseCase a PrinterJob should be created for.
	 * @return A preconfigured {@link PrinterInterface} or null if something fails.
	 */
	public PrinterInterface getConfiguredPrinterInterface(PrinterInterfaceFactory interfaceFactory, String printerUseCaseID)
	throws PrinterException
	{
		if (interfaceFactory == null)
			throw new IllegalArgumentException("Parameter interfaceFactory can't be null!");
		logger.debug("getConfiguredPrinterInterface(): Try lookup printer interface");
		
		PrinterInterface printerInterface = interfaceFactory.createPrinterInterface();
		if (logger.isDebugEnabled())
			logger.debug("getConfiguredPrinterInterface(): Created printerInterface "+printerInterface.getClass().getSimpleName());
		
		PrinterConfiguration configuration = getPrinterConfiguration(printerUseCaseID);
		
		if (configuration != null)
			printerInterface.configure(configuration);
//			configurator.assignPrinterConfigurationInterface(configuration, printerInterface);
		return printerInterface;
	}
	
	/**
	 * This method reads the {@link PrinterConfiguration} stored for the given
	 * printerUseCaseID from the {@link PrinterConfigurationCfMod}.
	 * <p>
	 * It will ask the user (by {@link #editPrinterConfiguration(String, boolean)})
	 * to edit the configuration when either no configuration was yet stored
	 * or the configuration has the 'alwaysAsk' flag set to true.  
	 * </p>
	 * @param printerUseCaseID The id of the printer use-case the {@link PrinterConfiguration} should be searched for.
	 * @return The PrinterConfiguration for the next print with the printerUseCaseID.
	 */
	public PrinterConfiguration getPrinterConfiguration(String printerUseCaseID) {
		long start = 0;
		if (logger.isDebugEnabled()) {
			start = 0;
			logger.debug("getPrinterConfiguration(): Try lookup printer configuration");
		}
		PrinterConfiguration configuration = PrinterConfigurationCfMod.getPrinterConfiguration(printerUseCaseID);
		if (configuration == null) {
			if (logger.isDebugEnabled()) 
				logger.debug("getPrinterConfiguration(): PrinterConfiguration with useCaseID " + printerUseCaseID + " was not found in ConfigModule");
			configuration = editPrinterConfiguration(printerUseCaseID, true);
		} else if (configuration != null && configuration.isAlwaysAsk()) {
			if (logger.isDebugEnabled())
				logger.debug("getConfiguredPrinterInterface(): Have alwaysAsk == true => ask for config.");
			configuration = editPrinterConfiguration(printerUseCaseID, false);
		}
		if (configuration == null) {
			if (logger.isDebugEnabled())
				logger.warn("PrinterConfiguration for useCaseID " + printerUseCaseID + " could not be created, null will be returned");
		}
		if (logger.isDebugEnabled())
			logger.debug("getPrinterConfiguration(): Reading/Getting PrinterConfiguration from user took: " + (System.currentTimeMillis() - start) + " ms.");
		return configuration;
	}
	
	/**
	 * Call this method to configure the given {@link PrinterJob} using the {@link PrinterConfiguration}.
	 * This will set the {@link PrintService} from the {@link PrinterConfiguration}. 
	 * 
	 * @param printerJob The {@link PrinterJob} to configure.
	 * @param printerConfiguration The {@link PrinterConfiguration} to read the configuration from.
	 * @throws PrinterException If an error occurs while configuring the {@link PrinterJob}.
	 */
	public void configurePrinterJob(PrinterJob printerJob, PrinterConfiguration printerConfiguration) 
	throws PrinterException {
		long start = System.currentTimeMillis();
		if (logger.isDebugEnabled())
			logger.debug("configurePrinterJob() creates with " + printerConfiguration);
		if (printerConfiguration != null) {			
			PrintService service = null;
			if (printerConfiguration.getPrintServiceName() != null) {
				start = System.currentTimeMillis();
				if (logger.isDebugEnabled())
					logger.debug("configurePrinterJob() looking up PrintService: " + printerConfiguration.getPrintServiceName() + ".");
				service = PrintUtil.lookupPrintService(printerConfiguration.getPrintServiceName());
				if (logger.isDebugEnabled())
					logger.debug("configurePrinterJob() looking up PrintService took " + (System.currentTimeMillis() - start) + " msec.");
				if (service != null) {
					printerJob.setPrintService(service);
				}
			}
//			if (printerConfiguration.getPageFormat() != null) {
//				// TODO: Find way to set PageFormat
//				// I've commented this for now as printerJob.defaultPage does not set, but queries the
//				// default page format from the service and this takes quite a while sometimes.
//				if (logger.isDebugEnabled())
//					logger.debug("configurePrinterJob() PageFormat of configuration is not null, setting it for the printerJob"); 
//				printerJob.defaultPage(printerConfiguration.getPageFormat());
//			}
		}		
	}
	
	/**
	 * Returns a {@link PrinterInterface} associated to the system default printer.
	 * The settings (PageFormat and other?? attributes) of the default printer will
	 * not be changed for this job.
	 * If no default print-service can be found, <code>null</code> will be returned.
	 * 
	 * @param interfaceFactory The factory to use when creating the PrinterInterface (prior to configuring it).
	 * @return A {@link PrinterInterface} associated to the system default printer.
	 */
	public PrinterInterface getSytemDefaultPrinterInterface(PrinterInterfaceFactory interfaceFactory)
	throws PrinterException
	{
		logger.debug("Looking up system default printer interface");
		PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
		if (printService == null)
			return null;
		PrinterInterface printerInterface = interfaceFactory.createPrinterInterface();
		PrinterConfiguration configuration = new PrinterConfiguration();
		configuration.setPrintServiceName(printService.getName());
		printerInterface.configure(configuration);
		logger.debug("Returning system default printer interface "+printerInterface.getConfiguration().getPrintServiceName());
		return printerInterface;
	}
	
	/**
	 * Responsible for somehow creating a or maybe changing a {@link PrinterConfiguration}
	 * for the given printerUseCaseID.
	 * Implementations might open a dialog to edit the configuration.
	 * 
	 * @param printerUseCaseID The id of the printerUseCase to edit the configuration for.
	 * @param preSelectionDoStore The pre selected value for the option whether to store the edited config or simply use it for the next run.
	 * @return The newly edited, or the old PrinterConfiguration (if dialog cancelled). Might return null also, when errors occur.
	 */
	public abstract PrinterConfiguration editPrinterConfiguration(final String printerUseCaseID, boolean preSelectionDoStore);
	
	/**
	 * Opens a (Java API) printer dialog and returns a PrinterInterface associated to the
	 * selected printer. No other settings will be made to the {@link PrinterConfiguration}
	 * passed to the interface. If the dialog is cancelled, null is returned.
	 * 
	 * @param interfaceFactory The factory to use when creating the PrinterInterface.
	 * @return A {@link PrinterInterface} associated to a user-selected printer, or null.
	 */
	public PrinterInterface getUserSelectedPrinterInterface(PrinterInterfaceFactory interfaceFactory)
	throws PrinterException
	{
		PrinterJob printerJob = PrinterJob.getPrinterJob();
		if (printerJob.printDialog()) {
			PrinterInterface printerInterface = interfaceFactory.createPrinterInterface();
			PrinterConfiguration configuration = new PrinterConfiguration();
			configuration.setPrintServiceName(printerJob.getPrintService().getName());
			printerInterface.configure(configuration);
			return printerInterface;
		}
		return null;
	}
	
}

