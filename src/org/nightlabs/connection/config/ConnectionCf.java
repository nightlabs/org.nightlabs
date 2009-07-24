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

package org.nightlabs.connection.config;

import java.io.Serializable;

import org.nightlabs.config.Initializable;
import org.nightlabs.connection.Connection;

/**
 * This is the base class for configuring the hardware port settings
 * to access a device.
 * Usually you inherit it and define the appropriate options needed
 * for your device.

 *
 * @author Marco Schulze
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @version 1.0
 * 
 * @see org.nightlabs.keyreader.config.SerialConnectionCf
 */
public abstract class ConnectionCf
implements Initializable, Serializable
{
	private static final long serialVersionUID = 1L;
	private String address;
	private boolean fakeConnection = false;

	public ConnectionCf()
	{
	}

	public abstract Class<? extends Connection> getConnectionClass();

	public void init()
	{
	}
	
	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public boolean isFakeConnection()
	{
		return fakeConnection;
	}

	public void setFakeConnection(boolean fakeConnection)
	{
		this.fakeConnection = fakeConnection;
	}
}