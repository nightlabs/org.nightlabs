/* *****************************************************************************
 * NightLabsConnection - Provides a unified & extensible connection API        *
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
 ******************************************************************************/

package org.nightlabs.connection.rxtx.config;

import gnu.io.CommPortIdentifier;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class serves mainly as a workaround for <i>CommPortIdentifier.getPortIdentifiers()</i> as
 * this method doesn't list ports anymore that are currently open. Of course, we need all ports
 * in the listing. Hence, ports we knew earlier are added to the result in {@link #getPortIdentifiers()}.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class PortMan
{
	private static Map<String, CommPortIdentifier> portIdentifiers = new HashMap<String, CommPortIdentifier>();

	@SuppressWarnings("unchecked")
	public synchronized static List<CommPortIdentifier> getPortIdentifiers()
	{
		Set<String> portNames = new HashSet<String>(portIdentifiers.keySet());
		ArrayList<CommPortIdentifier> res = new ArrayList<CommPortIdentifier>();
		for (Enumeration e = CommPortIdentifier.getPortIdentifiers(); e.hasMoreElements(); ) {
			CommPortIdentifier cpi = (CommPortIdentifier)e.nextElement();
			portNames.remove(cpi.getName());
			res.add(cpi);
			portIdentifiers.put(cpi.getName(), cpi);
		}

		for (String portName : portNames)
			res.add(portIdentifiers.get(portName));

		return res;
	}
}
