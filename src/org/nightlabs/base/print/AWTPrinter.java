/**
 * 
 */
package org.nightlabs.base.print;

import java.awt.print.PrinterJob;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class AWTPrinter implements PrinterInterface {
	
	public static class Factory implements PrinterInterfaceFactory {

		public PrinterInterface createPrinterInterface() {
			PrinterJob printerJob = PrinterJob.getPrinterJob();
			AWTPrinter awtPrinter = new AWTPrinter();
			awtPrinter.setPrinterJob(printerJob);
			return awtPrinter;
		}
		
	}

	private PrinterJob printerJob;
	
	/**
	 * 
	 */
	public AWTPrinter() {
	}
	
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

	public void configure(PrinterConfiguration printerConfiguration) {
		// TODO Auto-generated method stub		
	}

}
