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

import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

/**
 * Utils for accessing printers.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class PrintUtil {

//	public static PrinterInterface getAWTPrinterInterface(String printerUseCaseID) {
//		return PrinterInterfaceManager.sharedInstance().getConfiguredPrinterInterface(
//				PrinterInterfaceManager.INTERFACE_FACTORY_AWT,
//				printerUseCaseID
//			);
//	}
	
	public static PrinterInterface getDocumentPrinterInterface(String printerUseCaseID) {
		// TODO: Implement platform dependent PrinterInterface for document printing.
		return null;
	}
	
	/**
	 * Assigns the given printerJob to a PrintService. If the default printer is to be used
	 * this method tries to lookup the default print service and assign it. If no default is
	 * 
	 * @param printerJob
	 * @param useDefault
	 * @return <code>true</code> if the user does not cancel the print dialog;
	 * 		<code>false</code> otherwise.
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
	public static PrintService lookupPrintService(String name) {
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		for (PrintService element : services) {
			if (element.getName().equals(name))
				return element;
		}
		return null;
	}
	
	/**
	 * Returns a {@link List} of all {@link PrintService}s found.
	 * 
	 * @return A {@link List} of all {@link PrintService}s found.
	 */
	public static List<PrintService> lookupPrintServices() {
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		ArrayList<PrintService> result = new ArrayList<PrintService>();
		for (PrintService element : services) {
			result.add(element);
		}
		return result;
	}
	
	/**
	 * Returns the {@link PrintService} configured to the settings of the given printerConfiguration.
	 * 
	 * @param printerConfiguration The printerConfiguration to apply.
	 * @param throwExceptionIfNotFound Whether to throw an exception if the printservice specified in the given printConfiguration could not be found and also no default is assigned.
	 * @return The {@link PrintService} configured to the settings of the given printerConfiguration.
	 * @throws PrinterException Thrown depending on the throwExceptionIfNotFound parameter.
	 */
	public static PrintService getConfiguredPrintService(PrinterConfiguration printerConfiguration, boolean throwExceptionIfNotFound)
	throws PrinterException
	{
		PrintService printService = null;
		if (printerConfiguration != null && printerConfiguration.getPrintServiceName() != null)
			printService = lookupPrintService(printerConfiguration.getPrintServiceName());
		if (printService == null)
			printService = PrintServiceLookup.lookupDefaultPrintService();
		if (printService == null && throwExceptionIfNotFound)
			throw new PrinterException("Could not find a configured or default print service");
		// TODO: Still have to configure the print-service
		return printService;
	}
	
	/**
	 * Retuns the default(current) {@link PageFormat} for the given {@link PrintService}.
	 * 
	 * @param printService The {@link PrintService} to get the {@link PageFormat} of.
	 * @return The default(current) {@link PageFormat} for the given {@link PrintService}.
	 */
	public static PageFormat getDefaultPageFormat(PrintService printService)
	throws PrinterException
	{
		PrinterJob printerJob = PrinterJob.getPrinterJob();
		printerJob.setPrintService(printService);
		return printerJob.defaultPage();
	}
	
	/**
	 * Returns whether the given {@link PageFormat}s equal regarding their paper.
	 * It will size, imageable position and imageable size of the given formats.
	 * 
	 * @param pageFormat1 The first {@link PageFormat} to compare.
	 * @param pageFormat2 The second {@link PageFormat} to compare.
	 */
	public static boolean pageFormatEquals(PageFormat pageFormat1, PageFormat pageFormat2) {
		if (pageFormat1 == null && pageFormat2 == null)
			return true;
		if (pageFormat1 == null && pageFormat2 != null)
			return false;
		if (pageFormat2 == null && pageFormat1 != null)
			return false;
		boolean result = false;
		result = pageFormat1.getHeight() == pageFormat2.getHeight();
		if (!result)
			return result;
		result = pageFormat1.getWidth() == pageFormat2.getWidth();
		if (!result)
			return result;
		result = pageFormat1.getImageableX() == pageFormat2.getImageableX();
		if (!result)
			return result;
		result = pageFormat1.getImageableY() == pageFormat2.getImageableY();
		if (!result)
			return result;
		result = pageFormat1.getImageableHeight() == pageFormat2.getImageableHeight();
		if (!result)
			return result;
		result = pageFormat1.getImageableWidth() == pageFormat2.getImageableWidth();
		return result;
	}

	/**
	 * Sets the given {@link Printable} to the given {@link PrinterJob}. It will
	 * check whether the {@link PageFormat} assigned to the job equals the given pageFormat
	 * and set the printable with the pageFormat only if they differ. This is likely to save some time.
	 * 
	 * @param printJob The {@link PrinterJob} to set the printable.
	 * @param printable The {@link Printable} to set.
	 * @param pageFormat The {@link PageFormat} to set to the job.
	 */
	public static void setPrinterJobPrintable(PrinterJob printJob, Printable printable, PageFormat pageFormat) {
		if (pageFormat == null)
			printJob.setPrintable(printable);
		else {
			PageFormat jobFormat = printJob.defaultPage();
			if (pageFormatEquals(pageFormat, jobFormat))
				printJob.setPrintable(printable);
			else
				printJob.setPrintable(printable, pageFormat);
		}
			
	}
}
