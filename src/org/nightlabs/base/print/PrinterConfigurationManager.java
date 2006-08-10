/**
 * 
 */
package org.nightlabs.base.print;

import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PrinterConfigurationManager {

	/**
	 * Returns a {@link PrinterJob} associated to the PrintService configured
	 * for the given printerUseCase. Also the settings configured (PageFormat and other??)
	 * will be set for the returned PrinterJob.
	 * 
	 * If no configuration was found, the user will be asked to configure one ( 
	 * see {@link #editPrinterConfiguration(String)}).
	 * 
	 * If configuration somehow fails for this method it will return null.
	 * 
	 * @param printerUseCase The printerUseCase a PrinterJob should be created for.
	 * @return A preconfigured {@link PrinterJob} or null if something fails.
	 */
	public PrinterJob getConfiguredPrinterJob(String printerUseCase) {
		// TODO: Implement
		return null;
	}

	/**
	 * Returns a {@link PrinterJob} associated to the system default printer.
	 * The settings (PageFormat and other?? attributes) of the default printer will 
	 * not be changed for this job.
	 * 
	 * @return A {@link PrinterJob} associated to the system default printer.
	 */
	public PrinterJob getSytemDefaultPrinterJob() {
		PrinterJob printerJob = PrinterJob.getPrinterJob();
		if (printerJob.getPrintService() == null)
			return null;
		return printerJob;
	}
	
	public PrinterJob editPrinterConfiguration(String printerUseCase) {
		// TODO: Implement
		return null;
	}
	
	public PrinterJob getUserSelectedPrinterJob() {
		// TODO: Implement
		return null;
	}
}
