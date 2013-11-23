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
import java.util.List;

import org.nightlabs.connection.Connection;
import org.nightlabs.connection.config.ConnectionCf;
import org.nightlabs.connection.rxtx.ParallelConnection;

public class ParallelConnectionCf
extends ConnectionCf
{
	private static final long serialVersionUID = 1L;

	public static List<CommPortIdentifier> getParallelPortIdentifiers()
	{
		List<CommPortIdentifier> identifiers = PortMan.getPortIdentifiers();
		ArrayList<CommPortIdentifier> res = new ArrayList<CommPortIdentifier>(identifiers.size());
		for (CommPortIdentifier cpi : identifiers) {
			if (CommPortIdentifier.PORT_PARALLEL == cpi.getPortType())
				res.add(cpi);
		}
		return res;
	}

	public ParallelConnectionCf() { }

	@Override
	public Class<? extends Connection> getConnectionClass()
	{
		return ParallelConnection.class;
	}

	@Override
	public void init()
	{
		super.init();

		if (getAddress() == null)
			setAddress("/dev/lp0");
	}
}
