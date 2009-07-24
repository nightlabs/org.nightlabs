package org.nightlabs.connection;

import java.util.EventObject;

public class ConnectionEvent extends EventObject
{
	private ConnectionEventType connectionEventType;
	private boolean reOpen;
	private boolean closeOnError;

	public ConnectionEvent(Connection connection, ConnectionEventType connectionEventType, boolean reOpen, boolean closeOnError) {
		super(connection);

		if (connectionEventType == null)
			throw new IllegalArgumentException("connectionEventType == null");

		this.connectionEventType = connectionEventType;
		this.reOpen = reOpen;
		this.closeOnError = closeOnError;
	}

	private static final long serialVersionUID = 1L;

	/**
	 * Get the {@link Connection} that fired this event. It is the same as returned
	 * by the method {@link #getConnection()}.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public Object getSource() {
		return super.getSource();
	}

	/**
	 * Get the {@link Connection} that fired this event.
	 *
	 * @return the {@link Connection} firing this event.
	 * @see #getSource()
	 */
	public Connection getConnection()
	{
		return (Connection) getSource();
	}

	public ConnectionEventType getConnectionEventType() {
		return connectionEventType;
	}

	public boolean isReOpen() {
		return reOpen;
	}

	public boolean isCloseOnError() {
		return closeOnError;
	}
}
