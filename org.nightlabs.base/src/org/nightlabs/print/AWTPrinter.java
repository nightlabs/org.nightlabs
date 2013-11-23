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

import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.DocPrintJob;
import javax.print.PrintService;

import org.apache.log4j.Logger;

/**
 * AWTPrinter is the implementation of PrinterInterface to be
 * used if you need to or can print with the Java print API (java.awt.print).
 * 
 * AWTPrinter holds one {@link PrinterJob} that will be configured
 * upon a call to {@link #configure(PrinterConfiguration)}. Use it
 * either to direcly print, by setting its {@link Printable} or {@link Pageable},
 * or use it to access the {@link PrintService} to even create
 * new jobs for instance an{@link DocPrintJob}.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class AWTPrinter implements PrinterInterface {
	
	public static class Factory implements PrinterInterfaceFactory {

		public PrinterInterface createPrinterInterface() {
			PrinterJob printerJob = PrinterJob.getPrinterJob();
			AWTPrinter awtPrinter = new AWTPrinter(printerJob);
			return awtPrinter;
		}
		
	}
	
	private static final Logger logger = Logger.getLogger(AWTPrinter.class);

	private PrinterJob printerJob;
	private PrinterConfiguration configuration;
	
	public AWTPrinter(PrinterJob printerJob) {
		this.printerJob = printerJob;
	}

	/**
	 * @return the printerJob
	 */
	public PrinterJob getPrinterJob() {
		return printerJob;
	}

	/**
	 * @param printerJob the printerJob to set
	 */
	public void setPrinterJob(PrinterJob printerJob) {
		this.printerJob = printerJob;
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.print.PrinterInterface#configure(org.nightlabs.print.PrinterConfiguration)
	 */
	public void configure(PrinterConfiguration printerConfiguration)
	throws PrinterException
	{
		logger.debug("Configuring AWTPrinter with "+printerConfiguration);
		if (printerConfiguration != null) {
			configuration = (PrinterConfiguration)printerConfiguration.clone();
			PrintService service = null;
			if (printerConfiguration.getPrintServiceName() != null)
				service = PrintUtil.lookupPrintService(printerConfiguration.getPrintServiceName());
			if (service != null)
				printerJob.setPrintService(service);
				
			if (printerConfiguration.getPageFormat() != null)
				// TODO: Find better way to set PageFormat
				printerJob.defaultPage(printerConfiguration.getPageFormat());
		}
	}

	public PrinterConfiguration getConfiguration() {
		return configuration;
	}

}
