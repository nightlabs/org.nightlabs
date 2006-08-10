package org.nightlabs.base.print;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.standard.Copies;

/**
 * Utils for accessing printers.
 *  
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PrintUtil {

	/**
	 * Assigns the given printerJob to a PrintService. If the default printer is to be used
	 * this method tries to lookup the default print service and assign it. If no default is 
	 * 
	 * @param printerJob
	 * @param useDefault
	 * @return
	 */
	public boolean assignPrinterJobToService(PrinterJob printerJob, boolean useDefault) {
		if (useDefault) {
			PrintService defService = PrintServiceLookup.lookupDefaultPrintService();
			if (defService != null) {
//				Printer
//				printerJob.
//				defService.getA
				try {
//					printerJob.printDialog(attributes)
					printerJob.setPrintService(defService);
				} catch (PrinterException e) {
					return false;
				}
				return true;
			} else
				return printerJob.printDialog();
		}
		else {
			return printerJob.printDialog();
		}
	}
	
	/**
	 * Tries to lookup the printService with the given name. 
	 * Will return null if no such service is available.
	 *  
	 * @param name The name of the {@link PrintService} to lookpu
	 * @return A {@link PrintService} or null.
	 */
	public PrintService lookupPrintService(String name) {
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		for (int i = 0; i < services.length; i++) {
			if (services[i].getName().equals(name))
				return services[i];
		}
		return null;
	}
}
