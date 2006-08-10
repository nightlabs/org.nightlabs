package org.nightlabs.base.print;

import java.awt.print.PageFormat;
import java.util.HashMap;
import java.util.Map;

public class PrinterConfiguration {

	private String printServiceName;
	
	private PageFormat pageFormat;
	
	private Map<String, Object> attributes = new HashMap<String, Object>();

	public PrinterConfiguration() {	}
	
	/**
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
	
	
}
