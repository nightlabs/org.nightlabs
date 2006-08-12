package org.nightlabs.base.print;

import java.awt.print.PageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to store configurations for printers for different
 * use cases. 
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PrinterConfiguration {

	private boolean alwaysAsk;
	
	private String printServiceName;
	
	private PageFormat pageFormat;
	
	private Map<String, Object> attributes = new HashMap<String, Object>();

	public PrinterConfiguration() {	}
	
	/**
	 * Returns the Map of custom named attributes
	 * of this configuration. This is intended to
	 * store attributes not covered by the AWT
	 * print API or 
	 * 
	 * @return the attributes
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the pageFormat
	 */
	public PageFormat getPageFormat() {
		return pageFormat;
	}

	/**
	 * @param pageFormat the pageFormat to set
	 */
	public void setPageFormat(PageFormat pageFormat) {
		this.pageFormat = pageFormat;
	}

	/**
	 * @return the printServiceName
	 */
	public String getPrintServiceName() {
		return printServiceName;
	}

	/**
	 * @param printServiceName the printServiceName to set
	 */
	public void setPrintServiceName(String printServiceName) {
		this.printServiceName = printServiceName;
	}

	/**
	 * @return the alwaysAsk
	 */
	public boolean isAlwaysAsk() {
		return alwaysAsk;
	}

	/**
	 * @param alwaysAsk the alwaysAsk to set
	 */
	public void setAlwaysAsk(boolean alwaysAsk) {
		this.alwaysAsk = alwaysAsk;
	}
	
	

}
