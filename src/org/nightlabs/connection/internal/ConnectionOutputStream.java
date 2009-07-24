package org.nightlabs.connection.internal;

import java.io.IOException;
import java.io.OutputStream;

public class ConnectionOutputStream extends OutputStream
{
	private OutputStream delegate;

	public ConnectionOutputStream() { }

	public ConnectionOutputStream(OutputStream delegate) {
		setDelegate(delegate);
	}

	public void setDelegate(OutputStream delegate) {
		this.delegate = delegate;
	}

	public OutputStream getDelegate() {
		return delegate;
	}

	@Override
	public void write(int b) throws IOException {
		assertHasDelegate();

		delegate.write(b);
	}

	@Override
	public void close() throws IOException {
		if (delegate != null)
			delegate.close();

		delegate = null;
	}

	@Override
	public void flush() throws IOException {
		assertHasDelegate();

		delegate.flush();
	}

	private void assertHasDelegate() {
		if (delegate == null)
			throw new IllegalStateException("This ConnectionOutputStream doesn't have a delegate assigned!");
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		assertHasDelegate();

		delegate.write(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException {
		assertHasDelegate();

		delegate.write(b);
	}
}
