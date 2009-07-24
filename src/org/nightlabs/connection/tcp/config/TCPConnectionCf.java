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

package org.nightlabs.connection.tcp.config;

import org.nightlabs.connection.Connection;
import org.nightlabs.connection.config.ConnectionCf;
import org.nightlabs.connection.tcp.TCPConnection;

public class TCPConnectionCf
extends ConnectionCf
{
	private static final long serialVersionUID = 1L;

	@Override
	public Class<? extends Connection> getConnectionClass()
	{
		return TCPConnection.class;
	}

	@Override
	public void init()
	{
		super.init();

		if (getAddress() == null)
			setAddress("localhost:1234");

		if (soTimeout < 0)
			setSoTimeout(90000);
	}

	private boolean useSSL;
	private boolean verifyCert;

	public boolean isUseSSL()
	{
		return useSSL;
	}
	public void setUseSSL(boolean useSSL)
	{
		this.useSSL = useSSL;
	}
	public boolean isVerifyCert()
	{
		return verifyCert;
	}
	public void setVerifyCert(boolean verifyCert)
	{
		this.verifyCert = verifyCert;
	}

	private void parseAddress(String address)
	{
		this.host = null;
		this.port = 0;
		if (address != null) {
			int colonIdx = address.indexOf(':');
			if (colonIdx < 0)
				throw new IllegalArgumentException("address does not contain a port! Format must be \"host:port\"!");

			if (address.lastIndexOf(':') != colonIdx)
				throw new IllegalArgumentException("address contains more than one colon (':')! Format must be \"host:port\"!");

			String portStr = address.substring(colonIdx + 1);
			int port;
			try {
				port = Integer.parseInt(portStr);
			} catch (NumberFormatException x) {
				throw new IllegalArgumentException("port \""+portStr+"\" cannot be parsed as number! Format must be \"host:port\" and port must be a valid TCP port number!");
			}

			if (port < 1 || port > 65535)
				throw new IllegalArgumentException("port \""+port+"\" is out of range (1...65535)! Format must be \"host:port\" and port must be a valid TCP port number!");

			this.host = address.substring(0, colonIdx);
			this.port = port;
		}
	}

	@Override
	public void setAddress(String address)
	{
		parseAddress(address);
		super.setAddress(address);
	}

	private transient String host = null;
	private transient int port = -1;

	public String getHost()
	{
		if (host == null)
			parseAddress(getAddress());

		return host;
	}

	public int getPort()
	{
		if (port < 1)
			parseAddress(getAddress());

		return port;
	}

	private int soTimeout = -1;

	public int getSoTimeout()
	{
		return soTimeout;
	}
	public void setSoTimeout(int soTimeout)
	{
		this.soTimeout = soTimeout;
	}
}
