/**
 * 
 */
package org.nightlabs.base.print;

import java.awt.print.PrinterJob;
import java.util.Map;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.eclipse.swt.widgets.Display;

/**
 * Helper class to access {@link PrinterInterface}s configured for different useCases within
 * a RCP application. Currently the system relies on the AWT printing API
 * and will return interfaces for this exclusively (and there is no system to change this behaviour yet).
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PrinterInterfaceManager {
	
	public static PrinterInterfaceFactory INTERFACE_FACTORY_AWT = new AWTPrinter.Factory();
//	public static PrinterInterfaceFactory INTERFACE_FACTORY_DOCUMENT = DocumentPrinter.class;
	
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
	 * @param printerUseCaseID The printerUseCase a PrinterJob should be created for.
	 * @return A preconfigured {@link PrinterInterface} or null if something fails.
	 */
	public PrinterInterface getConfiguredPrinterInterface(PrinterInterfaceFactory interfaceFactory, String printerUseCaseID) {
		if (interfaceFactory == null)
			throw new IllegalArgumentException("Parameter interfaceFactory can't be null!");
		PrinterUseCase useCase = PrinterConfigurationRegistry.sharedInstance().getPrinterUseCase(printerUseCaseID);
		if (useCase == null)
			throw new IllegalArgumentException("No printerUseCase: '"+printerUseCaseID+"' was registered.");
		PrinterConfiguratorFactory factory = useCase.getDefaultConfiguratorFactory();
		if (factory == null)
			factory = PrinterConfigurationRegistry.sharedInstance().getPrinterConfiguratorFactory(PrinterConfiguratorFactoryDefault.class.getName());
		if (factory == null)
			throw new IllegalStateException("Unable to find a PrinterConfigurationFactory for the printerUseCase: '"+printerUseCaseID+"'");

		PrinterConfigurator configurator = factory.createPrinterConfigurator();
		
		PrinterConfiguration configuration = PrinterConfigurationCfMod.getPrinterConfiguration(printerUseCaseID);		
		if (configuration == null)
			configuration = editPrinterConfiguration(printerUseCaseID);
		
		PrinterInterface printerInterface = interfaceFactory.createPrinterInterface();
		
		if (configuration != null)
			printerInterface.configure(configuration);
//			configurator.assignPrinterConfigurationInterface(configuration, printerInterface);
		return printerInterface;
	}
	
	/**
	 * Returns a {@link PrinterInterface} associated to the system default printer.
	 * The settings (PageFormat and other?? attributes) of the default printer will 
	 * not be changed for this job.
	 * 
	 * @return A {@link PrinterInterface} associated to the system default printer.
	 */
	public PrinterInterface getSytemDefaultPrinterInterface(PrinterInterfaceFactory interfaceFactory) {
		PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
		if (printService == null)
			return null;
		PrinterInterface printerInterface = interfaceFactory.createPrinterInterface();
		PrinterConfiguration configuration = new PrinterConfiguration();
		configuration.setPrintServiceName(printService.getName());
		return printerInterface;
	}
	
	/**
	 * Opens a dialog to edit the configuration of the given printerUseCase.
	 * 
	 * @param printerUseCaseID The id of the printerUseCase to edit the configuration for.
	 * @return The newly edited, or the old PrinterConfiguration (if dialog cancelled). Might return null also.
	 */
	public PrinterConfiguration editPrinterConfiguration(final String printerUseCaseID) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				EditPrinterConfigurationDialog.openDialog(printerUseCaseID);
			}
		});
		return PrinterConfigurationCfMod.getPrinterConfiguration(printerUseCaseID);
	}
	
	/**
	 * Opens a printer dialog and returns a PrinterInterface associated to the
	 * selected printer. If cancelled, null is returned.
	 *   
	 * @return A PrinterJob associated to a user-selected printer, or null.
	 */
	public PrinterInterface getUserSelectedPrinterInterface(PrinterInterfaceFactory interfaceFactory) {
		PrinterJob printerJob = PrinterJob.getPrinterJob();
		if (printerJob.printDialog()) {
			PrinterInterface printerInterface = interfaceFactory.createPrinterInterface();
			PrinterConfiguration configuration = new PrinterConfiguration();
			configuration.setPrintServiceName(printerJob.getPrintService().getName());
			return printerInterface;
		}
		return null;
	}
	
	private static PrinterInterfaceManager sharedInstance;
	
	public static PrinterInterfaceManager sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new PrinterInterfaceManager();
		return sharedInstance;
	}
	
}

