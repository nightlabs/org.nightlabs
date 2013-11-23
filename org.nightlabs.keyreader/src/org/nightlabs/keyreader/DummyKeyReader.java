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

package org.nightlabs.keyreader;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This pseudo driver will be instantiated by KeyReaderMan if no config
 * is existing for a given keyReaderID. You can use this mechanism
 * to get an initial configuration.
 *
 * @author Marco Schulze
 * @version 1.0
 */
public class DummyKeyReader extends KeyReader
{
	private static final Logger logger = Logger.getLogger(DummyKeyReader.class);

	protected DummyKeyReader() { }

	private Map<Locale, String> names = new HashMap<Locale, String>();
	private Map<Locale, String> descriptions = new HashMap<Locale, String>();

	@Override
	public Map<Locale, String> getNames()
	{
		if (names.isEmpty()) {
			names.put(Locale.ENGLISH, "Dummy key reader"); //$NON-NLS-1$
			names.put(Locale.GERMAN, "Dummy (Platzhalter)"); //$NON-NLS-1$
		}
		return names;
	}

	@Override
	public Map<Locale, String> getDescriptions()
	{
		if (descriptions.isEmpty()) {
			descriptions.put(Locale.ENGLISH,
					"This pseudo driver will be instantiated by KeyReaderMan if no config " + //$NON-NLS-1$
					"is existing for a given keyReaderID. It does NOT read any keys and should " + //$NON-NLS-1$
					"therefore only be used, if no device is connected."); //$NON-NLS-1$
			descriptions.put(Locale.GERMAN, "Dieser Pseudo-Treiber wird verwendet, wenn keine " + //$NON-NLS-1$
					"Konfiguration für eine gewünschte keyReaderID vorhanden ist. Er liest KEINE " + //$NON-NLS-1$
					"Schlüssel und sollte daher nur benutzt werden, wenn kein Gerät angeschlossen ist."); //$NON-NLS-1$
		}
		return descriptions;
	}

	@Override
	protected void _openPort()
	{
		logger.warn("DummyKeyReader._openPort() called. Dummy should not be used! Check config and use a real device driver!"); //$NON-NLS-1$
	}

	@Override
	protected void _start()
	{
		logger.warn("DummyKeyReader.start() called. Dummy should not be used! Check config and use a real device driver!"); //$NON-NLS-1$
	}

	@Override
	protected void _closePort()
	{
		// nothing
	}
}
