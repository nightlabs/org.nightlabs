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
import java.util.HashMap;
import java.util.Map;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintRequestAttributeSet;

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
	
	private PrintRequestAttributeSet printRequestAttributeSet;
	
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
	 * Returns the {@link PageFormat} associated
	 * for this configuratin or <code>null</code>
	 * to indicate that the default format of
	 * the associated print-service is to be used.
	 * 
	 * @return the pageFormat or null.
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
	 * Returns the name of the print-service to use
	 * with this configuratino or <code>null</code>
	 * to inidcate that the default print-service
	 * is to be used.
	 * 
	 * @return the printServiceName or null.
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
	 * Returns whether the user should be asked for the configuration
	 * on every print request.
	 * 
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

	/**
	 * Returns the set of {@link PrintRequestAttribute}s
	 * configured.
	 * @return The set of {@link PrintRequestAttribute}s
	 * configured.
	 */
	public PrintRequestAttributeSet getPrintRequestAttributeSet() {
		return printRequestAttributeSet;
	}
	
	/**
	 * 
	 * @param printRequestAttributeSet The printRequestAttributes to set
	 */
	public void setPrintRequestAttributeSet(
			PrintRequestAttributeSet printRequestAttributeSet) {
		this.printRequestAttributeSet = printRequestAttributeSet;
	}
	
	/**
	 * Returns a copy of this PrinterConfiguration
	 * with the same attributes. Note that the
	 * printRequestAttributeSet and the attributes Map
	 * of the clone will reference the same instances
	 * as their entries as this object does.
	 * 
	 * @return A copy of this PrinterConfiguration
	 */
	@Override
	public Object clone() {
		PrinterConfiguration clone = new PrinterConfiguration();
		clone.setAlwaysAsk(isAlwaysAsk());
		clone.setPrintServiceName(getPrintServiceName());
		if (getPageFormat() != null)
			clone.setPageFormat((PageFormat)getPageFormat().clone());
		if (printRequestAttributeSet != null)
			clone.setPrintRequestAttributeSet(new HashPrintRequestAttributeSet(printRequestAttributeSet));
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			clone.getAttributes().put(entry.getKey(), entry.getValue());
		}
		return clone;
	}
	
	@Override
	public String toString() {
		return "Printer configuration (Printer: "+printServiceName+", alwaysAsk "+alwaysAsk+")";
	}
}
