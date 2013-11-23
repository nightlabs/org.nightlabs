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



/**
 * This is the base class for all KeyReaders that use the serial or
 * the parallel port. Note, that the parallel support has never been tested
 * so far.
 *
 * @author marco
 * @deprecated Connection is now cleanly separated from KeyReader
 */
@Deprecated
public abstract class KeyReaderComm extends KeyReader
{
//	private static final Logger logger = Logger.getLogger(KeyReaderComm.class);
//
//	/**
//	 * This method is executed by openPort(...) if the connection is serial (RS232).
//	 * Override this method and set the properties, your key reader needs. You
//	 * do not need to set baud rate, databits, stopbits and parity. This is read
//	 * from a config module and set automatically.
//	 *
//	 * @see org.nightlabs.keyreader.KeyReader#openPort(boolean forceReOpen)
//	 */
//	protected void setSerialPortProperties(SerialPort serialPort)
//	{
//	}
//
//	/**
//	 * This method is executed by openPort(...) if the connection is parallel (IEEE 1284).
//	 * Override this method and set the properties, your key reader needs.
//	 *
//	 * @see org.nightlabs.keyreader.KeyReader#openPort(boolean forceReOpen)
//	 */
//	protected void setParallelPortProperties(ParallelPort parallelPort)
//	{
//	}
//
//	protected CommPort commPort = null;
//
//	/**
//	 * @see org.nightlabs.keyreader.KeyReader#_openPort()
//	 */
//	protected void _openPort()
//	throws KeyReaderException
//	{
//		try {
//			ConnectionCf portCf = keyReaderCf.getConnectionCf();
//			if (portCf == null)
//				throw new NullPointerException("keyReaderCf.getPortCf() returned null!");
//
//			if (logger.isDebugEnabled())
//				logger.debug("available ports:");
//			for (Enumeration e = CommPortIdentifier.getPortIdentifiers(); e.hasMoreElements(); ) {
//				CommPortIdentifier cpi = (CommPortIdentifier)e.nextElement();
//				if (logger.isDebugEnabled())
//					logger.debug("	"+cpi.getName());
//			}
//
//			if (logger.isDebugEnabled())
//				logger.debug("Trying to open port \""+portCf.getAddress()+"\"...");
//			CommPortIdentifier commPortIdentifier = CommPortIdentifier.getPortIdentifier(
//				portCf.getAddress()
//			);
//			logger.info("Port \""+portCf.getAddress()+"\" is open.");
//
////			_isPortOpen = true;
//
//			commPort = commPortIdentifier.open(this.getClass().getName()+"("+getKeyReaderID()+")", 10000);
//			if (commPort == null)
//				throw new IllegalStateException("commPortIdentifier.open(...) returned null!");
//
//			if (commPort instanceof SerialPort) {
//				SerialPort serPort = (SerialPort)commPort;
//
//				if (!(portCf instanceof SerialConnectionCf))
//					throw new IllegalStateException("keyReaderCf.getPortCf() did not return an instance of SerialConnectionCf, but of "+portCf.getClass().getName());
//
//				SerialConnectionCf serialConnectionCf = (SerialConnectionCf)portCf;
//
//				int serPortParity = 0;
//				switch (serialConnectionCf.getParity()) {
//					case 'N':
//						serPortParity = SerialPort.PARITY_NONE;
//					break;
//					case 'O':
//						serPortParity = SerialPort.PARITY_ODD;
//					break;
//					case 'E':
//						serPortParity = SerialPort.PARITY_EVEN;
//					break;
//					case 'M':
//						serPortParity = SerialPort.PARITY_MARK;
//					break;
//					case 'S':
//						serPortParity = SerialPort.PARITY_SPACE;
//					break;
//					default:
//						throw new IllegalArgumentException("keyReaderCfMod.portCf.getParity() returned invalid value!");
//				} // switch (serialPortCf.getParity()) {
//
//				serPort.setSerialPortParams(
//					serialConnectionCf.getBaudRate(),
//					serialConnectionCf.getDataBits(),
//					serialConnectionCf.getStopBits(),
//					serPortParity
//				);
//
//				serPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
//
//				serPort.enableReceiveTimeout(500);
//				if (!serPort.isReceiveTimeoutEnabled())
//					logger.warn("Unable to set the serial receive timeout, because your OS seems not to support this feature! This may cause this application to hang up!");
//
//				setSerialPortProperties(serPort);
//			}
//			else if (commPort instanceof ParallelPort) {
//				ParallelPort parPort = (ParallelPort)commPort;
//				setParallelPortProperties(parPort);
//			}
//			else
//				throw new UnsupportedOperationException("KeyReaderComm does not support this comm port: "+commPort.getClass().getName());
//
//			keyReaderInputStream = commPort.getInputStream();
//			keyReaderOutputStream = commPort.getOutputStream();
//
////		} catch (KeyReaderException x) {
////			throw x;
//		} catch (RuntimeException x) {
//			throw x;
//		} catch (Throwable t) {
//			throw new KeyReaderException(t);
//		}
//	}
//
//	/**
//	 * @see org.nightlabs.keyreader.KeyReader#_closePort()
//	 */
//	protected void _closePort()
//	throws KeyReaderException
//	{
//		if (commPort != null) {
//			try {
//				commPort.close();
//			} catch (Throwable t) {
//				logger.error("commPort.close() failed!", t);
//			}
//			logger.info("Port \""+commPort.getName()+"\" is closed.");
//			commPort = null;
//		}
//
//		keyReaderInputStream = null;
//		keyReaderOutputStream = null;
//	}
//
}
