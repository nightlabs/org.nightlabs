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

package org.nightlabs.connection.rxtx;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.ParallelPort;
import gnu.io.PortInUseException;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nightlabs.connection.Connection;
import org.nightlabs.connection.StreamPair;
import org.nightlabs.connection.config.ConnectionCf;
import org.nightlabs.connection.rxtx.config.ParallelConnectionCf;
import org.nightlabs.connection.rxtx.config.PortMan;

public class ParallelConnection extends Connection
{
	private static  Logger logger = Logger.getLogger(ParallelConnection.class);

	public ParallelConnection()
	{
		try {
			PortMan.getPortIdentifiers(); // workaround, because a port disappears (that's a bug!!!) if it is open
		} catch (Throwable x) {
			logger.warn("Reading existing ports from OS via 'PortMan.getPortIdentifiers()' failed.", x);
		}
	}

	@Override
	public Map<Locale, String> getNames()
	{
		Map<Locale, String> res = new HashMap<Locale, String>();
		res.put(Locale.ENGLISH, "Parallel connection via RXTX");
		res.put(Locale.GERMAN, "Parallele Verbindung via RXTX");
		return res;
	}

	@Override
	public Map<Locale, String> getDescriptions()
	{
		Map<Locale, String> res = new HashMap<Locale, String>();
		res.put(Locale.ENGLISH, "This driver allows connections via parallel (IEEE 1284) ports " +
				"using the RXTX library.");
		res.put(Locale.GERMAN, "Dieser Treiber ermöglicht Verbindungen via Parallel-Schnittstelle (IEEE 1284) " +
				"über die RXTX Bibliothek.");
		return res;
	}

	@Override
	public Class<? extends ConnectionCf> getConnectionCfClass()
	{
		return ParallelConnectionCf.class;
	}

	private CommPort commPort=null;

	@Override
	@SuppressWarnings("unchecked")
	protected final StreamPair _open()
	throws IOException
	{
		String address = connectionCf.getAddress();

		logger.info("Existing ports:");
		for (Enumeration e = CommPortIdentifier.getPortIdentifiers(); e.hasMoreElements(); ) {
			CommPortIdentifier cpi = (CommPortIdentifier)e.nextElement();
			logger.info("  " + cpi.getName() + (cpi.isCurrentlyOwned() ? (" (owned by " + cpi.getCurrentOwner() + ")") : " (available)"));
		}

		try {
			logger.debug("Port address: "+address);
			CommPortIdentifier cpi = CommPortIdentifier.getPortIdentifier(address);
			logger.debug("CommPortIdentifier: "+cpi.toString());
			commPort = cpi.open(connectionCf.getClass().getName(), 1000000);
		} catch (NoSuchPortException e) {
			IOException x = new IOException("port with adress " + address + " does not exist");
			x.initCause(e);
			throw x;
		} catch (PortInUseException e) {
			IOException x = new IOException("port with adress " + address + " is in use");
			x.initCause(e);
			throw x;
		}
		if (commPort == null)
			throw new NullPointerException("CommPortIdentifier.getPortIdentifier(...).open(...) returned null for address " + address);

		if (!(commPort instanceof ParallelPort))
			throw new IllegalStateException("Port " + address + " is an instance of " + commPort.getClass().getName() + " but should be a ParallelPort!");

		logger.info("ParallelPort openend! address=" + address);

		ParallelPort parallelPort = (ParallelPort)commPort;

		if (logger.isDebugEnabled())
			logger.debug("parallelPort.mode=" + parallelPort.getMode());
//		parallelPort.setMode(ParallelPort.LPT_MODE_ANY);

		// TODO we should configure the parallel port using the ParallelConnectionCf

		return new StreamPair(commPort.getInputStream(), commPort.getOutputStream());
	}

	@Override
	protected void _close()
	throws IOException
	{
		if(commPort != null) {
			commPort.close();
			logger.info("ParallelPort closed! address=" + commPort.getName());
		}
	}

}
