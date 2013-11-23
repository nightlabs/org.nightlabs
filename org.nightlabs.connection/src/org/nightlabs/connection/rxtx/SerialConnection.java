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
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nightlabs.connection.Connection;
import org.nightlabs.connection.StreamPair;
import org.nightlabs.connection.config.ConnectionCf;
import org.nightlabs.connection.rxtx.config.PortMan;
import org.nightlabs.connection.rxtx.config.SerialConnectionCf;

public class SerialConnection extends Connection
{
	private static  Logger logger = Logger.getLogger(SerialConnection.class);

	public SerialConnection()
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
		res.put(Locale.ENGLISH, "Serial connection via RXTX");
		res.put(Locale.GERMAN, "Serielle Verbindung via RXTX");
		return res;
	}

	@Override
	public Map<Locale, String> getDescriptions()
	{
		Map<Locale, String> res = new HashMap<Locale, String>();
		res.put(Locale.ENGLISH, "This driver allows connections via serial (RS-232) ports " +
				"using the RXTX library.");
		res.put(Locale.GERMAN, "Dieser Treiber ermöglicht Verbindungen via Seriell-Schnittstelle (RS-232) " +
				"über die RXTX Bibliothek.");
		return res;
	}

	@Override
	public Class<? extends ConnectionCf> getConnectionCfClass()
	{
		return SerialConnectionCf.class;
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

		SerialConnectionCf serialConnectionCf = (SerialConnectionCf) connectionCf;

		try {
			logger.debug("Port address: "+address);
			CommPortIdentifier cpi = CommPortIdentifier.getPortIdentifier(address);
			logger.debug("CommPortIdentifier: "+cpi.toString());
			commPort = cpi.open(connectionCf.getClass().getName(), 1000); // , 1000000);
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

		if (!(commPort instanceof SerialPort))
			throw new IllegalStateException("Port " + address + " is an instance of " + commPort.getClass().getName() + " but should be a SerialPort!");

		logger.info("SerialPort opened. address=" + address);

		// seems the receive timeout doesn't have any effect
//		try {
//			commPort.enableReceiveTimeout(10);
//		} catch (UnsupportedCommOperationException e1) {
//			logger.error(e1.getMessage(), e1);
//		}

		SerialPort serialPort = (SerialPort)commPort;

		int dataBits;
		switch (serialConnectionCf.getDataBits()) {
			case 5: dataBits = SerialPort.DATABITS_5; break;
			case 6: dataBits = SerialPort.DATABITS_6; break;
			case 7: dataBits = SerialPort.DATABITS_7; break;
			case 8: dataBits = SerialPort.DATABITS_8; break;
			default:
				throw new IllegalArgumentException("dataBits invalid!");
		}

		int stopBits;
		switch (serialConnectionCf.getStopBits()) {
			case 1: stopBits = SerialPort.STOPBITS_1; break;
			case 2: stopBits = SerialPort.STOPBITS_2; break;
			default:
				throw new IllegalArgumentException("stopBits invalid!");
		}

		int serPortParity;
		switch (serialConnectionCf.getParity()) {
			case 'N':
				serPortParity = SerialPort.PARITY_NONE;
				break;
			case 'O':
				serPortParity = SerialPort.PARITY_ODD;
				break;
			case 'E':
				serPortParity = SerialPort.PARITY_EVEN;
				break;
			case 'M':
				serPortParity = SerialPort.PARITY_MARK;
				break;
			case 'S':
				serPortParity = SerialPort.PARITY_SPACE;
				break;
			default:
				throw new IllegalArgumentException("parity invalid!");
		}
		logger.info("Setting params: baudRate=" + serialConnectionCf.getBaudRate() + ", dataBits=" + serialConnectionCf.getDataBits() + ", stopBits=" + serialConnectionCf.getStopBits() + ", parity=" + serialConnectionCf.getParity() + ", dtr="+serialConnectionCf.isDtrOn()+", rts="+serialConnectionCf.isRtsOn());
		try {
			int tryCounter = 0;
			boolean successful = false;
			do {
				tryCounter++;
				try {
					serialPort.setDTR(serialConnectionCf.isDtrOn());
					serialPort.setRTS(serialConnectionCf.isRtsOn());

					serialPort.setSerialPortParams(
							serialConnectionCf.getBaudRate(),
							dataBits,
							stopBits,
							serPortParity);

					successful = true;
				} catch (Exception x) {
					logger.warn("Setting serial params failed. Try #" + tryCounter);
					if (tryCounter > 10)
						throw x;

					try { Thread.sleep(500); } catch (InterruptedException y) { }
				}
			} while (!successful);

			return new StreamPair(commPort.getInputStream(), commPort.getOutputStream());
		} catch(Exception e) {
			IOException x = new IOException("Setting params (baudRate=" + serialConnectionCf.getBaudRate() + ", dataBits=" + serialConnectionCf.getDataBits() + ", stopBits=" + serialConnectionCf.getStopBits() + ", parity=" + serialConnectionCf.getParity() + ") for SerialPort with adress " + address + " failed");
			x.initCause(e);
			throw x;
		}
	}

	@Override
	protected void _close()
	throws IOException
	{
		if(commPort != null) {
			commPort.close();
			logger.info("_close: SerialPort closed. address=" + commPort.getName());
		}
	}

}
