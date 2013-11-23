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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.nightlabs.connection.Connection;
import org.nightlabs.i18n.I18nUtil;
import org.nightlabs.keyreader.config.KeyReaderCf;

/**
 * This is the base class of all KeyReaders. To implement your own driver, you need
 * to extend this class and override the following methods:
 * <ul>
 *	<li>dataReceived(byte[])</li>
 *	<li>_openPort()</li>
 *	<li>_closePort()</li>
 *	<li>getInputStream() [optional]</li>
 *	<li>getOutputStream() [optional]</li>
 * </ul>
 * Note, that there exist inheritents of this class managing certain types of hardware
 * interfaces (e.g. serial or parallel ports).
 *
 * @author Marco Schulze
 * @version 1.0
 */
public abstract class KeyReader
{
	private static final Logger logger = Logger.getLogger(KeyReader.class);

	private KeyReaderMan keyReaderMan;
	public KeyReaderMan getKeyReaderMan() { return keyReaderMan; }

	protected KeyReaderCf keyReaderCf;

	private String keyReaderID;
	public String getKeyReaderID() { return keyReaderID; }

	/**
	 * <p>
	 * This method should return a {@link Map} with at least one entry. The
	 * name is used to describe this driver in short. The {@link #getDescriptions()}
	 * method, in contrast, should describe it in long.
	 * </p>
	 * <p>
	 * Note, that this method can be called even if {@link #init(KeyReaderMan, String, KeyReaderCf)}
	 * has not been called, before.
	 * </p>
	 *
	 * @return A {@link Map} that maps the language (in form of a {@link Locale}) to a name of type {@link String}.
	 */
	public abstract Map<Locale, String> getNames();

	/**
	 * <p>
	 * This method should return a {@link Map} with at least one entry. The
	 * description is used to describe this driver in long. The {@link #getNames()}
	 * method, in contrast, should describe it in short.
	 * </p>
	 * <p>
	 * Note, that this method can be called even if {@link #init(KeyReaderMan, String, KeyReaderCf)}
	 * has not been called, before.
	 * </p>
	 *
	 * @return A {@link Map} that maps the language (in form of a {@link Locale}) to a name of type {@link String}.
	 */
	public abstract Map<Locale, String> getDescriptions();

	public String getName(Locale locale)
	{
		return I18nUtil.getClosestL10n(getNames(), locale);
	}

	public String getDescription(Locale locale)
	{
		return I18nUtil.getClosestL10n(getDescriptions(), locale);
	}

	/**
	 * @return Returns the slot of this KeyReader.
	 * @see org.nightlabs.keyreader.config.KeyReaderCf
	 * @see org.nightlabs.keyreader.KeyReader#fireKeyReadEvent(String slot, String key)
	 */
	public String getSlot()
	{
		assertNotClosed();
		return keyReaderCf.getSlot();
	}

	/**
	 * You should not instanciate a KeyReader directly! Use KeyReaderMan.createKeyReader(...)!
	 * If you inherit this class, please do not define a constructor at all or make it public!
	 * You should do all your initialization in the init(...) method instead of the constructor.
	 *
	 * @see KeyReaderMan#createKeyReader(String keyReaderID)
	 * @see #init(KeyReaderMan _keyReaderMan, String _keyReaderID, KeyReaderCf _keyReaderCf)
	 */
	public KeyReader() { }

	/**
	 * This method is called directly after creation of the instance and
	 * allows KeyReader to have a no-argument constructor.
	 *
	 * @param _keyReaderMan The owner of this keyReader. Note, that there should only exist one shared instance of KeyReaderMan.
	 * @param _keyReaderID The name of this reader.
	 * @param _keyReaderCf The settings for this reader.
	 */
	protected void init(KeyReaderMan _keyReaderMan, String _keyReaderID, KeyReaderCf _keyReaderCf)
	{
		this.keyReaderMan = _keyReaderMan;
		this.keyReaderID = _keyReaderID;
		this.keyReaderCf = _keyReaderCf;
	}

	private boolean _isPortOpen = false;
	public boolean isPortOpen()
	{
		synchronized (portLock) {
			return _isPortOpen;
		}
	}

	public boolean isKeyReaderThreadRunning()
	{
		return keyReaderListenerThread != null;
	}

//	/**
//	 * This method is executed by openPort(), after the connection to the scanner
//	 * is opened.<br/><br/>
//	 * Override this method in your child of KeyReader.
//	 *
//	 * @see #openPort(boolean forceReOpen)
//	 */
//	protected void initReader()
//	throws KeyReaderException
//	{
//	}

//	/**
//	 * This method is executed by closePort(), before the connection to the scanner
//	 * gets closed.<br/><br/>
//	 * Override this method in your child of KeyReader.
//	 *
//	 * @see #openPort(boolean forceReOpen)
//	 */
//	protected void uninitReader()
//	throws KeyReaderException
//	{
//	}

	/**
	 * Call this method to get an input stream to communicate with the barcode reader
	 * device.
	 * <br/><br/>
	 * <b>Note, that you must synchronize on ioLock!</b>
	 *
	 * @return An InputStream connected to the barcode reader device.
	 *
	 * @see #getIOLock()
	 */
	public InputStream getInputStream()
	{
		assertNotClosed();

		if (!_isPortOpen)
			throw new IllegalStateException("This method cannot be used, if the port is not open!"); //$NON-NLS-1$

		return connection.getInputStream();
	}


	private Object ioLock = new Object();

	/**
	 * The returned object is used to synchronize on for read/write operations over
	 * the input/output stream.
	 *
	 * @see #getInputStream()
	 * @see #getOutputStream()
	 */
	public Object getIOLock()
	{ return ioLock; }

	/**
	 * Call this method to get an output stream to communicate with the key reader
	 * device.
	 * <br/><br/>
	 * <b>Note, that you must synchronize on ioLock!</b>
	 *
	 * @return An OutputStream connected to the key reader device.
	 *
	 * @see #getIOLock()
	 */
	public OutputStream getOutputStream()
	{
		assertNotClosed();

		if (!_isPortOpen)
			throw new IllegalStateException("This method cannot be used, if the port is not open!"); //$NON-NLS-1$

		return connection.getOutputStream();
	}

	/**
	 * This object is used to synchronize on, whenever we open or close the port
	 * or set its parameters.
	 */
	protected Object portLock = new Object();

	/**
	 * Override this method to manage your port. Never override the
	 * public openPort(...) method!
	 * <br/><br/>
	 * This method must do the following things:
	 * <ul>
	 * <li>Open a connection to the key reader device.</li>
	 * <li>Set the two members {@link #keyReaderInputStream} and
	 *	{@link #keyReaderOutputStream} to allow communication.
	 * </li>
	 * </ul>
	 */
	protected void _openPort()
	throws KeyReaderException
	{
		try {
			getConnection().open();
		} catch (IOException e) {
			throw new KeyReaderException(e);
		}
	}

	protected void open()
	{
		if (sharingKeyReaders_existing_register(this))
			_open();
	}

	protected void _open()
	{
	}

	/**
	 * This method opens the port to the barcode reader. If the connection is already
	 * open, nothing is done and the method returns immediately.
	 * <br/><br/>
	 * This method initializes the scanner by calling initScanner() after calling
	 * _openPort().
	 * <br/><br/>
	 * Never override this method! If you want to manage the connection to your
	 * reader yourself, please override _openPort()!
	 *
	 * @param forceReOpen Shall an existing connection be closed and reopened?
	 *
	 * @see #_openPort()
	 * @see #initReader()
	 */
	public void openPort()
	throws KeyReaderException
	{
		sharingKeyReaders_openPort(this);
	}

	/**
	 * Do not override this method! Override {@link #_openPort()} instead!
	 */
	protected void sharingKeyReaders_openPort(KeyReader keyReader)
	throws KeyReaderException
	{
		assertNotClosed();

		sharingKeyReaders_openPort_register(keyReader);

		synchronized (portLock) {
			if (_isPortOpen)
				return;

			boolean closePort = true;
			try {
				_openPort();
//				initReader();
				_start();

				_isPortOpen = true;
				closePort = false;
			} finally {
				if (closePort) {
					sharingKeyReaders_openPort_unregister(keyReader);
					_closePort();
				}
			}

		} // synchronized (portLock) {
	}

	private Connection connection;

	/**
	 * Get the ticket printer connection. Create it lazily.
	 * @return Returns a new Connection or a previously created one (a subsequent
	 *		call to this method does NOT re-create).
	 */
	protected Connection getConnection()
	throws KeyReaderException
	{
		if (connection==null) {
			try {
				connection = Connection.createConnection(keyReaderCf.getConnectionCf());
			} catch (Exception x) {
				throw new KeyReaderException(x);
			}
		}
		return connection;
	}

	/**
	 * Override this method to do your own port handling. NEVER override
	 * closePort()!
	 */
	protected void _closePort()
	{
		if (logger.isDebugEnabled())
			logger.debug("_closePort: enter"); //$NON-NLS-1$

		try {
			getConnection().close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * This method closes the port. If there's still a listener thread
	 * existing, it is automatically stopped.
	 * <br/><br/>
	 * Note: You should never override this method. If you want to manage your
	 * port yourself, please override _closePort()!
	 *
	 * @see #_closePort()
	 */
	public boolean closePort()
	{
		return sharingKeyReaders_closePort(this);
	}
	protected boolean sharingKeyReaders_closePort(KeyReader keyReader)
	{
		if (logger.isDebugEnabled())
			logger.debug("sharingKeyReaders_closePort: keyReader.keyReaderID = " + keyReader.getKeyReaderID()); //$NON-NLS-1$

		synchronized (sharingKeyReadersMutex) {
			sharingKeyReaders_openPort_unregister(keyReader);
			if (!sharingKeyReaders_openPort.isEmpty())
				return false;
		}
		_stop();
		return true;
	}

	/**
	 * called by {@link #listenerThreadHasFinished()}
	 */
	protected void internalClosePort()
	{
		synchronized (portLock) {
			if (!_isPortOpen)
				return;

//			try {
//				uninitReader();
//			} catch (Throwable t) {
//				logger.error("uninitReader() failed! keyReaderID=\""+keyReaderID+"\"", t);
//			}

			try {
				_closePort();
			} catch (Throwable t) {
				logger.error("_closePort() failed! keyReaderID=\""+keyReaderID+"\"", t); //$NON-NLS-1$ //$NON-NLS-2$
			}
			_isPortOpen = false;
		} // synchronized (portLock) {
	}

	private boolean closed = false;
	public boolean isClosed()
	{
		return closed;
	}
	public void assertNotClosed()
	{
		if (closed)
			throw new IllegalStateException("This instance of KeyReader is already closed! " + this); //$NON-NLS-1$
	}

	private Set<KeyReader> sharingKeyReaders_existing = new HashSet<KeyReader>();
	private Set<KeyReader> sharingKeyReaders_openPort = new HashSet<KeyReader>();

	protected boolean sharingKeyReaders_existing_register(KeyReader keyReader)
	{
		assertNotClosed();

		synchronized (sharingKeyReadersMutex) {
			if (sharingKeyReaders_existing.contains(keyReader))
				return false;

			sharingKeyReaders_existing.add(keyReader);
		}
		return true;
	}
	protected void sharingKeyReaders_existing_unregister(KeyReader keyReader)
	{
		synchronized (sharingKeyReadersMutex) {
			if (!sharingKeyReaders_existing.remove(keyReader))
				throw new IllegalStateException("keyReader not registered!"); //$NON-NLS-1$
		}
	}

	protected void sharingKeyReaders_openPort_register(KeyReader keyReader)
	{
		assertNotClosed();

		synchronized (sharingKeyReadersMutex) {
			if (sharingKeyReaders_openPort.contains(keyReader))
				return;

			sharingKeyReaders_openPort.add(keyReader);
		}
	}
	protected void sharingKeyReaders_openPort_unregister(KeyReader keyReader)
	{
		synchronized (sharingKeyReadersMutex) {
			sharingKeyReaders_openPort.remove(keyReader);
		}
	}

	private Object sharingKeyReadersMutex = new Object();

	/**
	 * <p>
	 * This method cleans up this <code>KeyReader</code> instance by releasing all resources
	 * ({@link KeyReaderListenerThread} & connection) and removes it from the {@link KeyReaderMan}.
	 * If you need the same key reader again, you have to recreate it by calling
	 * {@link KeyReaderMan#createKeyReader(String)}.
	 * </p>
	 * <p>
	 * This method can be called directly or the method {@link KeyReaderMan#dropKeyReader(String, boolean)}
	 * can be called instead - for example if only the <code>keyReaderID</code> is known and not the
	 * <code>KeyReader</code> instance.
	 * </p>
	 * @return <code>true</code> if the keyReader is really closed. <code>false</code>, if it is
	 *		still in use (by a {@link KeyReaderSharingDevice}) and could not be closed. It will be
	 *		closed as soon as all {@link KeyReaderSharingDevice}s are closed.
	 *
	 * @throws KeyReaderException
	 */
	public boolean close(boolean wait)
	{
		return sharingKeyReaders_close(this, wait);
	}

	protected boolean sharingKeyReaders_close(KeyReader keyReader, boolean wait)
	{
		if (closed)
			return true;

		boolean res;
		synchronized (sharingKeyReadersMutex) {
			if (!sharingKeyReaders_existing.remove(keyReader))
				return false;

			res = sharingKeyReaders_existing.isEmpty();
		}
		if (!res)
			return res;

//		synchronized (keyReaderListenerThreadLock) {
//			closed = true;
//			if (keyReaderListenerThread != null)
//				stop(wait, true);
//			else if (_isPortOpen)
//				closePort();
//		}
		closePort();
		keyReaderMan.onKeyReaderClosed(this);

		return res;
	}

	/**
	 * Always synchronize access to this member on keyReaderListenerThreadLock!
	 *
	 * @see #keyReaderListenerThreadLock
	 */
	protected KeyReaderListenerThread keyReaderListenerThread = null;

	/**
	 * This member is used as a mutex to synchronize access on the keyReaderListenerThread member.
	 *
	 * @see #keyReaderListenerThread
	 */
	protected Object keyReaderListenerThreadLock = new Object();

//	/**
//	 * This method starts a thread that waits for data from the barcode reader. If the
//	 * device sends a code, this thread fires a KeyReadEvent.
//	 * If an error occurs, the listener thread fires a KeyReaderErrorEvent.
//	 * Do not override this method!!! Override {@link #_start()} instead!
//	 */
//	public void start()
//	{
//		sharingKeyReaders_start(this);
//	}
//
//	protected void sharingKeyReaders_start(KeyReader keyReader)
//	{
//		sharingKeyReaders_started_register(keyReader);
//		_start();
//	}

	protected void _start()
	{
		synchronized (keyReaderListenerThreadLock) {
			assertNotClosed();
			synchronized (portLock) {
				if (keyReaderListenerThread != null) {
					if (logger.isDebugEnabled())
						logger.debug("start() called, but already running!", new Exception()); //$NON-NLS-1$

					return;
				}

				keyReaderListenerThread = new KeyReaderListenerThread(this);
			} // synchronized (portLock) {
		} //	synchronized (keyReaderListenerThreadLock) {
	}

	/**
	 * This method is executed <b>asynchronously</b> by the KeyReaderListenerThread,
	 * whenever data is read from the device (after the listener has been started).
	 * <br/><br/>
	 * Note that you should not rely on the comleteness of this data. Maybe there
	 * come several packages of data that form one complete "sentence". Thus, you
	 * must use an own data buffer and find out, when a "sentence" from your device
	 * is complete, yourself.
	 */
	public void dataReceived(byte[] data)
	throws Throwable
	{
		if (!logger.isDebugEnabled())
			return;

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < data.length; ++i) {
			byte b = data[i];
			char c = (char)b;
			if ((c >= 'A' && c <='Z') || (c >= 'a' && c <='z') || (c >= '0' && c <='9'))
				sb.append(c);
			else {
				sb.append("<#"); //$NON-NLS-1$
				sb.append(b);
				sb.append(">"); //$NON-NLS-1$
			}
		} // for (int i = 0; i < data.length; ++i) {
		logger.debug("received data: "+sb.toString()); //$NON-NLS-1$
	}

//	private ReferenceList keyReaderErrorListeners = new ReferenceList();
	private Set<KeyReaderErrorListener> keyReaderErrorListeners = new HashSet<KeyReaderErrorListener>();
	private ArrayList<KeyReaderErrorListener> _keyReaderErrorListeners = null;

	/**
	 * This method adds a KeyReaderErrorListener. This listener gets
	 * triggered <b>asynchronously</b>, whenever an error occurs. If you want
	 * the event being handled on a certain thread (e.g. the GUI-thread), you have
	 * to take care about this, yourself!
	 * <br/><br/>
	 * To get the event onto the GUI thread, you might consider using SwingUtils.invokeLater(...).
	 */
	public void addKeyReaderErrorListener(KeyReaderErrorListener l)
	{
		assertNotClosed();
//		keyReaderErrorListeners.add(new WeakReference(l));
		synchronized (keyReaderErrorListeners) {
			keyReaderErrorListeners.add(l);
			_keyReaderErrorListeners = null;
		}
	}

	/**
	 * This method removes a KeyReaderErrorListener.
	 *
	 * @see #addKeyReaderErrorListener(KeyReaderErrorListener l)
	 */
	public boolean removeKeyReaderErrorListener(KeyReaderErrorListener l)
	{
		assertNotClosed();
		synchronized (keyReaderErrorListeners) {
			boolean res = keyReaderErrorListeners.remove(l);
			_keyReaderErrorListeners = null;
			return res;
		}
	}

	/**
	 * You should execute this method, if an error occurs in your implementation of
	 * KeyReader.
	 * <br/><br/>
	 * This method is executed <b>asynchronously</b> by the KeyReaderListenerThread,
	 * whenever an error occured. After calling this method, the
	 * KeyReaderListenerThread will wait 10 seconds before continuing work to
	 * avoid putting heavy load on the machine because of a permanent problem.
	 * <br/><br/>
	 * You should not overwrite this method or if you do, you should call super.errorOccured(...)!
	 */
	public void errorOccured(Throwable error)
	{
		ArrayList<KeyReaderErrorListener> listeners = _keyReaderErrorListeners;
		if (listeners == null) {
			synchronized (keyReaderErrorListeners) {
				_keyReaderErrorListeners = new ArrayList<KeyReaderErrorListener>(keyReaderErrorListeners);
				listeners = _keyReaderErrorListeners;
			}
		}

		if (listeners.isEmpty())
			return;

		KeyReaderErrorEvent e = new KeyReaderErrorEvent(this, error);
		for (KeyReaderErrorListener l : listeners)
			l.errorOccured(e);

//		for (int i = keyReaderErrorListeners.size() - 1; i >= 0; --i) {
//			WeakReference ref = (WeakReference)keyReaderErrorListeners.get(i);
//			if (ref.get() == null) {
//				logger.debug("Removing WeakReference, whose value became null.");
//				keyReaderErrorListeners.remove(i); // cleanup
//			}
//			else
//				((KeyReaderErrorListener)ref.get()).errorOccured(e);
//		} // for (int i = keyReaderErrorListeners.size() - 1; i >= 0; --i) {
	}

//	/**
//	 * This method stops the listener.
//	 *
//	 * @param wait Defines whether this method should wait until the listener thread
//	 *	 has finished.
//	 *
//	 * @see #start()
//	 */
//	public boolean stop(boolean wait, boolean closePortAfterStop)
//	{
//		return sharingKeyReaders_stop(this, wait, closePortAfterStop);
//	}
//	protected boolean sharingKeyReaders_stop(KeyReader keyReader, boolean wait, boolean closePortAfterStop)
//	{
//		synchronized (sharingKeyReadersMutex) {
//			sharingKeyReaders_started_unregister(this);
//			if (!sharingKeyReaders_started.isEmpty())
//				return false;
//		}
//		_stop(wait, closePortAfterStop);
//		return true;
//	}
	protected void _stop()
	{
		if (logger.isDebugEnabled())
			logger.debug("_stop: enter"); //$NON-NLS-1$

		synchronized (keyReaderListenerThreadLock) {
			if (keyReaderListenerThread == null) {
				logger.warn("stop() called, but not running!", new Exception()); //$NON-NLS-1$
				return;
			}

			keyReaderListenerThread.interrupt();

			if (logger.isDebugEnabled())
				logger.debug("_stop: keyReaderListenerThread.interrupt() called."); //$NON-NLS-1$

//			if (wait) {
				long waitStartDT = System.currentTimeMillis();

				while (keyReaderListenerThread != null) {

					if (System.currentTimeMillis() - waitStartDT > waitForListenerThreadTimeout)
						throw new IllegalThreadStateException("KeyReaderListenerThread did not finish within timeout!"); //$NON-NLS-1$

					try {
						keyReaderListenerThreadLock.wait(5000);
					} catch (InterruptedException x) { logger.warn("interrupted", x); } //$NON-NLS-1$
				}
				logger.debug("KeyReaderListenerThread is finished."); //$NON-NLS-1$

//				if (this.closePortAfterStop) {
					while(isPortOpen()) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException x) { logger.warn("interrupted", x); } //$NON-NLS-1$
					}
					logger.debug("Port is closed."); //$NON-NLS-1$
//				}

//			} // if (wait) {

		} // synchronized (keyReaderListenerThreadLock) {
	}

//	private boolean closePortAfterStop = false;

	/**
	 * The timeout in milliseconds. Should be one minute.
	 */
	protected static final int waitForListenerThreadTimeout = 60000;

	/**
	 * This method is executed by the listener thread, when it has finished.
	 */
	void listenerThreadHasFinished()
	throws KeyReaderException
	{
		synchronized (keyReaderListenerThreadLock) {
			keyReaderListenerThread = null;
			keyReaderListenerThreadLock.notifyAll();

			internalClosePort();
//			if (closePortAfterStop)
//				closePort();
		}
	}


	/**
	 * Call this method, when your implementation of KeyReader has
	 * read a complete key.
	 *
	 * @param slot This param may be null. If it is null, the event is
	 * dispatched to all keyReaders sharing the same device. If it is not null,
	 * the event will only be sent to KeyReaders which are registered on the
	 * given slot. This means, even the listeners in this instance (your inheritent
	 * of KeyReader) may not be triggered.
	 * <br/><br/>
	 * The slot support is necessary for combi devices.
	 * <br/><br/>
	 * Example: Imagine, you have a device with two readers and one door. If the device
	 * can only be accessed by one driver, the second needs to be configured as
	 * using the "shareDeviceWith" setting. Now, the incoming events need to be split
	 * again between the two different logic KeyReaders. Therefore, two slots will be
	 * defined by the driver and the logic KeyReaders need to use each one of them.
	 *
	 * @param key The code that has been read.
	 */
	protected void fireKeyReadEvent(String slot, String key)
	{
		String thisSlot = getSlot();
		if (slot == null || slot.equals(thisSlot))
			internalFireKeyReadEvent(key);

		for(KeyReader keyReader : sharingKeyReaders_existing) {
			if (keyReader != this)
				keyReader.fireKeyReadEvent(slot, key);
		}

//		if (otherReadersSharingDevice != null) {
//			for (int i = otherReadersSharingDevice.size()-1; i >= 0; --i) {
//				WeakReference ref = (WeakReference)otherReadersSharingDevice.get(i);
//				if (ref.get() == null) {
//					logger.debug("Removing WeakReference to a KeyReaderSharingDevice, which became null.");
//					otherReadersSharingDevice.remove(i);
//				}
//				else {
//					KeyReader otherReader = (KeyReader)ref.get();
//					otherReader.fireKeyReadEvent(slot, key);
//				} // if (ref.get() != null)
//
//			} // for (int i = otherReadersSharingDevice.size()-1; i >= 0; --i) {
//		} // if (otherReadersSharingDevice != null) {
	}

	private Set<KeyReadListener> keyReadListeners = new HashSet<KeyReadListener>();
	private ArrayList<KeyReadListener> _keyReadListeners = null;
//	private ReferenceList keyReadListeners = new ReferenceList();

	/**
	 * This method adds a KeyReadListener. This listener gets triggered <b>asynchronously</b>,
	 * whenever a complete barcode is read. If you want the event being handled on
	 * a certain thread (e.g. the GUI-thread), you have to take care about this, yourself!
	 * <br/><br/>
	 * To get the event onto the GUI thread, you might consider using SwingUtilities.invokeLater(...).
	 *
	 * @see org.nightlabs.keyreader.KeyReaderSharingDevice
	 */
	public void addKeyReadListener(KeyReadListener l)
	{
//		keyReadListeners.add(new WeakReference(l));
		synchronized (keyReadListeners) {
			keyReadListeners.add(l);
			_keyReadListeners = null;
		}
	}

	/**
	 * This method removes a KeyReadListener.
	 *
	 * @see #addKeyReadListener(KeyReadListener l)
	 */
	public boolean removeKeyReadListener(KeyReadListener l)
	{
		synchronized (keyReadListeners) {
			boolean res = keyReadListeners.remove(l);
			_keyReadListeners = null;
			return res;
		}
	}

	/**
	 * Avoid calling this method! It is executed internally by fireKeyReadEvent(...)
	 * and does the dispatching of the event.
	 */
	protected void internalFireKeyReadEvent(String key)
	{
		ArrayList<KeyReadListener> listeners = _keyReadListeners;
		if (listeners == null) {
			synchronized (keyReadListeners) {
				_keyReadListeners = new ArrayList<KeyReadListener>(keyReadListeners);
				listeners = _keyReadListeners;
			}
		}

		if (listeners.isEmpty())
			return;

		KeyReadEvent e = new KeyReadEvent(this, key);
		for (KeyReadListener l : listeners)
			l.keyRead(e);

//		for (int i = keyReadListeners.size() - 1; i >= 0; --i) {
//			WeakReference ref = (WeakReference)keyReadListeners.get(i);
//			if (ref.get() == null) {
//				logger.debug("Removing WeakReference, whose value became null.");
//				keyReadListeners.remove(i); // cleanup
//			}
//			else
//				((KeyReadListener)ref.get()).keyRead(e);
//		} // for (int i = keyReadListeners.size() - 1; i >= 0; --i) {
	}


//	/**
//	 * Contains instances of WeakReference pointing to instances of
//	 * KeyReaderSharingDevice.
//	 */
//	protected List otherReadersSharingDevice = null;

//	public void registerKeyReaderSharingDevice(KeyReaderSharingDevice _keyReaderSharingDevice)
//	{
//		if (otherReadersSharingDevice == null)
//			otherReadersSharingDevice = new ArrayList();
//
//		otherReadersSharingDevice.add(
//			new WeakReference(_keyReaderSharingDevice)
//		);
//	}
//
//	public void unregisterKeyReaderSharingDevice(KeyReaderSharingDevice _keyReaderSharingDevice)
//	{
//		if (otherReadersSharingDevice == null)
//			return;
//
//		for (Iterator it = otherReadersSharingDevice.iterator(); it.hasNext(); ) {
//			Reference ref = (Reference)it.next();
//			if (ref.get() == null)
//				it.remove();
//			else {
//				if (ref.get().equals(_keyReaderSharingDevice)) {
//					it.remove();
//					return;
//				}
//			}
//		}
//	}

	/**
	 * @return Returns the keyReaderCf.
	 */
	public KeyReaderCf getKeyReaderCf() {
		return keyReaderCf;
	}
}
