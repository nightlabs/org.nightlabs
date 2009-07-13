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


/**
 * Interface to access different print services. Instances of this interface are created
 * by the {@link PrinterInterfaceFactory}
 * for one print job and thrown away
 * afterwards.
 * <p>
 * The {@link PrinterInterfaceManager} is able to return interfaces
 * preconfigured for different use cases (represented by a simple String).
 * </p>
 * <p>
 * Currently there are three type of interfaces planned:
 * <ul>
 * <li>AWT: To use for Graphical operations. Users might operate on a Graphics object here. See {@link AWTPrinter}.</li>
 * <li>SWT: Later (when the API is ready) for the same purposes as AWT</li>
 * <li>Document: For printing documents. This should allow different (native) implementations for
 * lnx, win and other printing engines to be somehow registered as printing of some document-types is not always supported by default (by the printer or the platform).
 * See {@link DelegatingDocumentPrinter} for more information.</li>
 * </ul>
 * </p>
 * Common to all interface type: They can be configured using a {@link PrinterConfiguration}.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface PrinterInterface {

	/**
	 * Configure this interface to reflect the setting given in the passed printerConfiguration.
	 *
	 * @param printerConfiguration The configuration to adopt.
	 */
	public void configure(PrinterConfiguration printerConfiguration) throws PrinterException;

	/**
	 * Returns the {@link PrinterConfiguration} this interface was {@link #configure(PrinterConfiguration)}d
	 * with, or null when it was not yet configured.
	 *
	 * @return the {@link PrinterConfiguration} this interface was {@link #configure(PrinterConfiguration)}d
	 * with, or null when it was not yet configured.
	 */
	public PrinterConfiguration getConfiguration();
}
