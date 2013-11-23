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

package org.nightlabs.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nightlabs.connection.config.ConnectionCf;
import org.nightlabs.connection.internal.ConnectionInputStream;
import org.nightlabs.connection.internal.ConnectionOutputStream;
import org.nightlabs.i18n.I18nUtil;
import org.nightlabs.util.IOUtil;

public abstract class Connection
{
	private static final Logger logger = Logger.getLogger(Connection.class);

	public static Connection createConnection(ConnectionCf connectionCf)
	throws InstantiationException, IllegalAccessException
	{
		// now, we create a connection instance
		Connection connection = connectionCf.getConnectionClass().newInstance();
		// initialize connection with config
		connection.init(connectionCf);
		return connection;
	}

	private static List<ConnectionImplementation> connectionImplementations = null;

	@SuppressWarnings("unchecked")
	public static List<ConnectionImplementation> getConnectionImplementations()
	throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
	{
		if (connectionImplementations == null) {
			synchronized(Connection.class) {
				if (connectionImplementations == null) {
					ArrayList<ConnectionImplementation> res = new ArrayList<ConnectionImplementation>();
					InputStream in = Connection.class.getResourceAsStream("resource/ConnectionImplementation.conf");
					if (in == null)
						throw new IllegalStateException("resource/ConnectionImplementation.conf not found!");

					String implementationStr;
					try {
						implementationStr = IOUtil.readTextFile(in);
					} finally {
						in.close();
					}

					String[] implementationArray = implementationStr.split("\n");
					for (String className : implementationArray) {
						className = className.replace(" ", "").replace("\r", "");

						if ("".equals(className))
							continue;

						if (className.startsWith("#"))
							continue;

						try {
							Class<? extends Connection> clazz = (Class<? extends Connection>) Class.forName(className);
							Connection connection = clazz.newInstance();
							res.add(new ConnectionImplementation(connection));
						} catch (Throwable t) {
							logger.error("Loading/instantiating Connection class failed! className=" + className, t);
						}
					}
					connectionImplementations = res;
				}
			} // synchronized(this) {
		}
		return connectionImplementations;
	}

	public abstract Map<Locale, String> getNames();
	public abstract Map<Locale, String> getDescriptions();

	/**
	 * @return The connection config class extending {@link ConnectionCf} which is used to store
	 *	the configuration of this <code>Connection</code> implementation.
	 */
	public abstract Class<? extends ConnectionCf> getConnectionCfClass();

	public String getName(Locale locale)
	{
		return I18nUtil.getClosestL10n(getNames(), locale);
	}

	public String getDescription(Locale locale)
	{
		return I18nUtil.getClosestL10n(getDescriptions(), locale);
	}

	private ConnectionInputStream inputStream;
	private ConnectionOutputStream outputStream;
	private boolean isOpen;
	@SuppressWarnings("unused")
	private boolean isClosing;
	protected ConnectionCf connectionCf;

	public Connection()
	{
		isOpen = false;
	}

	/**
	 * Override this method to implement opening the connection.
	 * This method is called by <code>open()</code>.
	 * @return The connection streams
	 * @throws IOException
	 */
	protected abstract StreamPair _open()	throws IOException;

	/**
	 * Override this method to implement closing the connection.
	 * This method is called by {@link #close()}.
	 * @throws IOException
	 */
	protected abstract void _close() throws IOException;

	/**
	 *	@param connectionCf The configuration for the connection
	 */
	protected void init(ConnectionCf connectionCf)
	{
		if (!getConnectionCfClass().isInstance(connectionCf))
			throw new IllegalArgumentException(this.getClass().getName() + " does not support ConnectionCf of type " + (connectionCf == null ? null : connectionCf.getClass().getName()) + " but requires " + this.getConnectionCfClass().getName());

		this.connectionCf = connectionCf;
	}

	public void assertOpen()
	{
		if (!isOpen)
			throw new IllegalStateException("Connection is not open");
	}

	public boolean isOpen()
	{
		return isOpen;
	}

	/**
	 * This method opens the port - which type
	 * of port depends on the descendant of <code>Connection</code>.
	 * <p>
	 * Do NOT override/extend this method! Implement
	 * {@link #_open()} instead!
	 * <p>
	 * As a user of this class, you MUST NOT forget
	 * to call {@link #close()} at the end.
	 */
	public final void open()
	throws IOException
	{
		if (isOpen)
			throw new IllegalStateException("Connection is not closed! Call close() before!");

		boolean close = true;
		try {
			fireConnectionEvent(ConnectionEventType.preOpen, false, false);

			isOpen = true;
			if(connectionCf == null || connectionCf.isFakeConnection()) {
				this.inputStream = new ConnectionInputStream(System.in);
				this.outputStream = new ConnectionOutputStream(System.err);
			} else {
				StreamPair sp = _open();
				this.inputStream = new ConnectionInputStream(sp.getInputStream());
				this.outputStream = new ConnectionOutputStream(sp.getOutputStream());
//				this.inputStream = new BufferedInputStream(sp.getInputStream());
//				this.outputStream = new BufferedOutputStream(sp.getOutputStream());
			}
			fireConnectionEvent(ConnectionEventType.postOpen, false, false);

			close = false;
		} finally {
			if (close)
				close(true);
		}
	}

	/**
	 * This method closes the port - which type
	 * of port depends on the descendant of <code>Connection</code>.
	 * <p>
	 * Do NOT override/extend this method! Implement
	 * {@link #_close()} instead!
	 * @throws IOException
	 */
	public final void close()
	throws IOException
	{
		close(false);
	}

	protected final void close(boolean closeOnError)
	throws IOException
	{
		if (logger.isDebugEnabled())
			logger.debug("close: enter");

//		try {
		if (!isOpen) {
			if (logger.isDebugEnabled())
				logger.debug("close: already closed! Returning immediately.");

			return;
		}

		fireConnectionEvent(ConnectionEventType.preClose, false, closeOnError);

//		if (!isClosing)
		isClosing = true;

		if(connectionCf != null && !connectionCf.isFakeConnection()) {
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
			if (outputStream != null) {
				outputStream.close();
				outputStream = null;
			}
			_close();
		}
		isOpen = false;
		isClosing = false;

		if (logger.isDebugEnabled())
			logger.debug("close: successfully closed");

		fireConnectionEvent(ConnectionEventType.postClose, false, closeOnError);
//		} catch (RuntimeException e) {
//			throw e;
//		} catch (IOException e) {
//			throw e;
//		} catch (Exception e) {
//			IOException x = new IOException();
//			x.initCause(e);
//			throw x;
//		}
	}

	/**
	 * Reopens an existing connection. If no connection is currently open,
	 * there's no difference to calling {@link #open()}. If a connection currently
	 * exists, the connection is closed and re-opened. The streams (see
	 * {@link #getInputStream()} and {@link #getOutputStream()}) stay the same
	 * (no need to update your references) because they wrap the real streams which
	 * are replaced.
	 *
	 * @throws IOException if closing or opening fails.
	 */
	public final void reOpen()
	throws IOException
	{
		if (!isOpen) {
			open();
			return;
		}

		if (inputStream == null)
			throw new IllegalStateException("This Connection is open, but there is no inputStream!");

		if (outputStream == null)
			throw new IllegalStateException("This Connection is open, but there is no outputStream!");

		fireConnectionEvent(ConnectionEventType.preClose, true, false);

		isClosing = true;

		if(connectionCf != null && !connectionCf.isFakeConnection()) {
			inputStream.close();
			inputStream.setDelegate(null);
			outputStream.close();
			outputStream.setDelegate(null);

			_close();
		}
		else {
			inputStream.setDelegate(null);
			outputStream.setDelegate(null);
		}
		isOpen = false;
		isClosing = false;

		fireConnectionEvent(ConnectionEventType.postClose, true, false);


		boolean close = true;
		try {
			fireConnectionEvent(ConnectionEventType.preOpen, true, false);

			isOpen = true;
			if(connectionCf == null || connectionCf.isFakeConnection()) {
				this.inputStream.setDelegate(System.in);
				this.outputStream.setDelegate(System.err);
			} else {
				StreamPair sp = _open();
				this.inputStream.setDelegate(sp.getInputStream());
				this.outputStream.setDelegate(sp.getOutputStream());
			}
			fireConnectionEvent(ConnectionEventType.postOpen, true, false);

			close = false;
		} finally {
			if (close) {
				fireConnectionEvent(ConnectionEventType.preClose, true, true);

				isClosing = true;

				if(connectionCf != null && !connectionCf.isFakeConnection()) {
					inputStream.close();
					inputStream.setDelegate(null);
					outputStream.close();
					outputStream.setDelegate(null);
					_close();
				}
				else {
					inputStream.setDelegate(null);
					outputStream.setDelegate(null);
				}

				isOpen = false;
				isClosing = false;

				fireConnectionEvent(ConnectionEventType.postClose, true, true);
			}
		}
	}

	public InputStream getInputStream()
	{
		assertOpen();
		return inputStream;
	}

	public OutputStream getOutputStream()
	{
		assertOpen();
		return outputStream;
	}

	public void setConnectionCf(ConnectionCf cf)
	{
		connectionCf = cf;
	}

	public ConnectionCf getConnectionCf()
	{
		return connectionCf;
	}

	private List<ConnectionListener> connectionListeners = new ArrayList<ConnectionListener>();
	private List<ConnectionListener> _connectionListeners = null;

	public void addConnectionListener(ConnectionListener listener)
	{
		synchronized (connectionListeners) {
			connectionListeners.add(listener);
			_connectionListeners = null;
		}
	}

	public void removeConnectionListener(ConnectionListener listener)
	{
		synchronized (connectionListeners) {
			connectionListeners.remove(listener);
			_connectionListeners = null;
		}
	}

	protected void fireConnectionEvent(ConnectionEventType connectionEventType, boolean reOpen, boolean closeOnError) {
		List<ConnectionListener> listeners;
		synchronized (connectionListeners) {
			if (_connectionListeners == null)
				_connectionListeners = Collections.unmodifiableList(new ArrayList<ConnectionListener>(connectionListeners));

			listeners = _connectionListeners;
		}
		if (listeners.isEmpty())
			return;

		ConnectionEvent connectionEvent = new ConnectionEvent(this, connectionEventType, reOpen, closeOnError);
		for (ConnectionListener connectionListener : listeners) {
			connectionListener.onConnectionEvent(connectionEvent);
		}
	}
}
