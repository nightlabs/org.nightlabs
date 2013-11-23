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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This pseudo driver is instanced, if you set the key "shareDeviceWith" in the
 * config. This is necessary, if you have a combi device which unifies two (or more)
 * readers in one machine (e.g. in a gate).
 * <br/><br/>
 * Note, that the order is important. You can only share a device with a key
 * reader that is already existing. As a general rule, we instantiate the drivers
 * in alphabetical order. This means, in case you have a reader "entry0" and you
 * want to use the same device in "exit0", this works, because "entry0" is instanced
 * before "exit0". But to define "exit0" and point from "entry0" to "exit0", does
 * not work.
 * <br/><br/>
 * To dispatch the events that source from only one device to the right instances
 * of KeyReader, there are slots existing.
 * <br/><br/>
 * Example: Your device consists out of two readers (one for entry & one for exit)
 * and is accessed by the serial port. Now imagine, your physical device sends a
 * special prefix before every key to specify whether it comes from the entry or from
 * the exit reader. The driver must know to which instance of KeyReader (the real one
 * or the KeyReaderSharingDevice) it needs to dispatch the event. Therefore, the driver
 * in our example might define two slots, named "entry" and "exit".
 *
 * @author Marco Schulze
 * @version 1.0
 * 
 * @see org.nightlabs.keyreader.config.KeyReaderCf#getShareDeviceWith()
 * @see org.nightlabs.keyreader.config.KeyReaderCf#getSlot()
 */
public class KeyReaderSharingDevice extends KeyReader
{
	protected KeyReader realKeyReader;

	protected KeyReaderSharingDevice() { }

	private Map<Locale, String> names = new HashMap<Locale, String>();
	private Map<Locale, String> descriptions = new HashMap<Locale, String>();

	@Override
	public Map<Locale, String> getNames()
	{
		if (names.isEmpty()) {
			names.put(Locale.ENGLISH, "Pseudo driver for sharing a device"); //$NON-NLS-1$
			names.put(Locale.GERMAN, "Pseudo-Treiber zur gemeinsamen Nutzung eines Gerätes"); //$NON-NLS-1$
		}
		return names;
	}

	@Override
	public Map<Locale, String> getDescriptions()
	{
		if (descriptions.isEmpty()) {
			descriptions.put(Locale.ENGLISH,
					"This pseudo driver is instantiated, if you set the key \"shareDeviceWith\" in the " + //$NON-NLS-1$
					"config. This is necessary, if you have a combi device which unifies two (or more) " + //$NON-NLS-1$
					"readers in one machine (e.g. in a gate)."); //$NON-NLS-1$
		}
		return descriptions;
	}

	public void setRealKeyReader(KeyReader _realKeyReader)
	{
		assertNotClosed();

		if (this.realKeyReader != null) {
			this.realKeyReader.sharingKeyReaders_existing_unregister(this);
//			this.realKeyReader.unregisterKeyReaderSharingDevice(this);
		}

		this.realKeyReader = _realKeyReader;
		this.realKeyReader.sharingKeyReaders_existing_register(this);
//		this.realKeyReader.registerKeyReaderSharingDevice(this);
	}

	@Override
	public InputStream getInputStream()
	{
		return realKeyReader.getInputStream();
	}

	@Override
	public OutputStream getOutputStream()
	{
		return realKeyReader.getOutputStream();
	}

	/**
	 * @see org.nightlabs.keyreader.KeyReader#getIOLock()
	 */
	@Override
	public Object getIOLock() {
		return realKeyReader.getIOLock();
	}

//	/**
//	 * This method opens the port with the real device by delegating
//	 * to the realKeyReader.
//	 * @throws KeyReaderException
//	 */
//	@Override
//	public void openPort()
//	throws KeyReaderException
//	{
//		super.openPort();
//		realKeyReader.sharingKeyReaders_openPort(this);
//	}

	@Override
	protected void sharingKeyReaders_openPort(KeyReader keyReader)
			throws KeyReaderException
	{
		super.sharingKeyReaders_openPort(keyReader);
		realKeyReader.sharingKeyReaders_openPort(this);
	}

//	/**
//	 * This method closes the port with the real device by delegating
//	 * to the realKeyReader. Note, that reference counting is used - i.e.
//	 * the port will be closed when all sharing readers have been closed
//	 * and the real one, too.
//	 */
//	@Override
//	public boolean closePort()
//	{
//		if (!super.closePort())
//			return false;
//
//		realKeyReader.sharingKeyReaders_closePort(this);
//		return true;
//	}

	@Override
	protected boolean sharingKeyReaders_closePort(KeyReader keyReader)
	{
		if (!super.sharingKeyReaders_closePort(keyReader))
			return false;

		realKeyReader.sharingKeyReaders_closePort(this);
		return true;
	}

//	/**
//	 * This method delegates to the real key reader and starts it
//	 * if it's not yet running. Reference counting is used.
//	 */
//	@Override
//	public void start()
//	{
//		super.start();
//	}
//	@Override
//	protected void sharingKeyReaders_start(KeyReader keyReader)
//	{
//		super.sharingKeyReaders_start(keyReader);
//		realKeyReader.sharingKeyReaders_start(this);
//	}
	@Override
	protected void _start()
	{
		// we don't start a thread here!
		started = true;
	}

	private boolean started = false;
	
//	/**
//	 * This method does nothing, because the main key reader instance is
//	 * responsible and this sharing driver should not interfere with the real one!
//	 */
//	@Override
//	public boolean stop(boolean wait, boolean closePortAfterStop)
//	{
//		if (!super.stop(wait, closePortAfterStop))
//			return;
//
//
//	}

//	@Override
//	protected boolean sharingKeyReaders_stop(KeyReader keyReader, boolean wait, boolean closePortAfterStop)
//	{
//		if (!super.sharingKeyReaders_stop(keyReader, wait, closePortAfterStop))
//			return false;
//
//		realKeyReader.sharingKeyReaders_stop(this, wait, closePortAfterStop);
//		return true;
//	}
//
	@Override
	protected void _stop()
	{
		// do nothing - the keyReader where we hooked into manages everything
		started = false;
	}

	@Override
	public boolean close(boolean wait)
	{
		if (!super.close(wait))
			return false;

		realKeyReader.sharingKeyReaders_close(this, wait);
		return true;
	}

	/**
	 * @see org.nightlabs.keyreader.KeyReader#isKeyReaderThreadRunning()
	 */
	@Override
	public boolean isKeyReaderThreadRunning() {
		return started;
	}

	/**
	 * @see org.nightlabs.keyreader.KeyReader#_closePort()
	 */
	@Override
	protected void _closePort() {
		// nothing
	}
	/**
	 * @see org.nightlabs.keyreader.KeyReader#_openPort()
	 */
	@Override
	protected void _openPort() throws KeyReaderException {
		// nothing
	}
}
