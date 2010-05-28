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

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

/**
 * @author Marco Schulze
 * @version 1.0
 */
public class KeyReaderListenerThread extends Thread
{
	public static final Logger logger = Logger.getLogger(KeyReaderListenerThread.class);

	protected KeyReader keyReader;

	public KeyReaderListenerThread(KeyReader barcodeReader)
	{
		if (barcodeReader == null)
			throw new NullPointerException("keyReader must not be null!"); //$NON-NLS-1$

		this.keyReader = barcodeReader;

		start();
	}

	/**
	 * This controls the size of the read buffer. In other words, it defines, how
	 * many bytes are read at one step before executing KeyReader.dataReceived(...).
	 * Because a barcode reader sends normally only a few bytes, we set this to 512 bytes, only.
	 */
	protected static final int readBufSize = 512; // bytes

	@Override
	public void run()
	{
		try { // catch
			try { // finally
				logger.debug("KeyReaderListenerThread starting."); //$NON-NLS-1$

				InputStream in = keyReader.getInputStream();
				OutputStream out = keyReader.getOutputStream();

				if (in == null)
					logger.error("keyReader.getInputStream() returned null!"); //$NON-NLS-1$

				if (out == null)
					logger.warn("keyReader.getOutputStream() returned null!"); // only a warning, because we don't need it //$NON-NLS-1$

				byte[] buf = new byte[readBufSize];

				while (!isInterrupted()) {
					try {

						int bytesRead;
						synchronized (keyReader.getIOLock()) {
							bytesRead = 0;
							try {
								sleep(300);
							} catch (InterruptedException x) {
								// ignore!
							}

							int avl = in.available();
							if (avl > 0) {
								bytesRead += in.read(buf, 0, avl);
							} // if (avl > 0) {
						} // synchronized (keyReader.getIOLock()) {

						if (bytesRead > 0) {
							byte[] data = new byte[bytesRead];
							for (int i = 0; i < bytesRead; ++i)
								data[i] = buf[i];

							keyReader.dataReceived(data);
						} // if (bytesRead > 0) {

					} catch (Throwable t) {
						logger.error("Read failed.", t); //$NON-NLS-1$
						keyReader.errorOccured(t);
						try { sleep(5000); } catch (InterruptedException x) { logger.warn("interrupted", x); } //$NON-NLS-1$

						// and now, we reopen the connection to the reader
						// we call the _xyz() methods directly, because we don't
						// want reference counting here - it must REALLY reopen
						keyReader._closePort();
						keyReader._openPort();
						in = keyReader.getInputStream();
						out = keyReader.getOutputStream();
					}
				} // while (!isInterrupted()) {

			} finally {
				keyReader.listenerThreadHasFinished();
			}
		} catch (Throwable t) {
			logger.fatal("Read failure caused thread death! This should never happen!", t); //$NON-NLS-1$
		}
	}

	private boolean finishIt = false;

	@Override
	public void interrupt()
	{
		finishIt = true;
		super.interrupt();
	}
	@Override
	public boolean isInterrupted()
	{
		return finishIt || super.isInterrupted();
	}

}
