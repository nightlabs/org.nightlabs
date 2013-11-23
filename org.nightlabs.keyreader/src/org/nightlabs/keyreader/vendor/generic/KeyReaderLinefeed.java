/* *****************************************************************************
 * KeyReader - Framework library for reading keys from arbitrary reader-devices*
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.keyreader.vendor.generic;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nightlabs.keyreader.KeyReader;
import org.nightlabs.keyreader.KeyReaderException;


/**
 * <p>
 * <code>KeyReaderLinefeed</code> is a driver supporting all key readers that
 * send a key simply as sequence of characters (A-Z, a-z, 0-9) terminated by
 * ASCII-10 (linefeed). Hence, no special commands or control characters
 * (except the linefeed) are used.
 * </p>
 * <p>
 * Some barcode readers send ASCII-13 + ASCII-10 (Windows CRLF), hence
 * this class silently ignores all ASCII-13 (carriage return).
 * </p>
 * <p>
 * <b>Important:</b> Many barcode readers do <b>not</b> send line-feeds by default
 * and you have to change their configuration. Usually, a configuration change is
 * extremely easy: You only have to scan a special barcode which encodes the
 * configuration command. You'll usually find these configuration-barcodes in
 * the scanner's documentation.
 * </p>
 * <p>
 * This KeyReader class has been successfully used with the following barcode readers:
 * <ul>
 *	<li>DataLogic
 *		<ul>
 *			<li>Gryphon</li>
 *			<li>DLC7070</li>
 *		</ul>
 *	</li>
 *	<li>ades
 *		<ul>
 *			<li>SCANBAR S6600</li>
 *		</ul>
 *	</li>
 * </ul>
 * </p>
 *
 * @author Marco Schulze
 * @version 1.0
 */
public class KeyReaderLinefeed extends KeyReader
{
	private static final Logger logger = Logger.getLogger(KeyReaderLinefeed.class);

	private Map<Locale, String> names = new HashMap<Locale, String>();
	private Map<Locale, String> descriptions = new HashMap<Locale, String>();

	@Override
	public Map<Locale, String> getNames()
	{
		if (names.isEmpty()) {
			names.put(Locale.ENGLISH, "Generic reader using linefeed as separator"); //$NON-NLS-1$
			names.put(Locale.GERMANY, "Generischer Leser mit Zeilenumbruch als Trennzeichen"); //$NON-NLS-1$
		}
		return names;
	}

	@Override
	public Map<Locale, String> getDescriptions()
	{
		if (descriptions.isEmpty()) {
			descriptions.put(Locale.ENGLISH,
					"KeyReaderLinefeed is a generic driver supporting all key readers that " + //$NON-NLS-1$
					"send a key simply as sequence of " + //$NON-NLS-1$
					"characters (A-Z, a-z, 0-9) terminated by ASCII-10 (linefeed). Hence, no special " + //$NON-NLS-1$
					"commands or control characters (except the linefeed) are used."); //$NON-NLS-1$
			descriptions.put(Locale.GERMAN, "KeyReaderLinefeed ist ein generischer Treiber, der alle Lesegeräte " + //$NON-NLS-1$
					"unterstützt, die " + //$NON-NLS-1$
					"gelesene Schlüssel als einfache Zeichenketten (A-Z, a-z, 0-9) gefolgt von einem " + //$NON-NLS-1$
					"ASCII-10 (Zeilenumbruch) senden. Daher verwendet dieser Treiber keine speziellen " + //$NON-NLS-1$
					"Kommandos oder Kontrollzeichen (außer dem Zeilenumbruch)."); //$NON-NLS-1$
		}
		return descriptions;
	}

	private StringBuffer currKey = new StringBuffer();

	@Override
	public void dataReceived(byte[] data)
	throws Throwable
	{
		super.dataReceived(data); // this method only performs some logging if debugging is enabled.

		for (int i = 0; i < data.length; ++i) {
			byte b = data[i];
			char c = (char)b;

			if ((c >= 'A' && c <='Z') || (c >= 'a' && c <='z') || (c >= '0' && c <='9'))
				currKey.append(c);
			else {
				if (b == 13) {
					// ignore
				}
				else if (b == 10) {
					// barcode complete!

					if (currKey.length() > 0) {
						logger.debug("read a complete key: "+currKey.toString()); //$NON-NLS-1$

						fireKeyReadEvent(
							null,
							currKey.toString()
						);

						currKey.setLength(0);
					} // if (currKey.length() > 0) {
				}
				else {
					logger.error("received illegal data from barcode-reader: ASCII-value: "+b); //$NON-NLS-1$
					errorOccured(new KeyReaderException("received illegal data from barcode-reader: ASCII-value: "+b)); //$NON-NLS-1$
				}
			}

		} // for (int i = 0; i < data.length; ++i) {

	}
}
