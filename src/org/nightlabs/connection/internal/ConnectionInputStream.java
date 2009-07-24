package org.nightlabs.connection.internal;

import java.io.IOException;
import java.io.InputStream;

public class ConnectionInputStream extends InputStream
{
	private InputStream delegate;

	public ConnectionInputStream() { }

	public ConnectionInputStream(InputStream delegate) {
		setDelegate(delegate);
	}

	public void setDelegate(InputStream delegate) {
		this.delegate = delegate;
	}

	public InputStream getDelegate() {
		return delegate;
	}

	@Override
	public int read() throws IOException {
		assertHasDelegate();

		return delegate.read();
	}

	private void assertHasDelegate() {
		if (delegate == null)
			throw new IllegalStateException("This ConnectionInputStream doesn't have a delegate assigned!");
	}

	@Override
	public int available() throws IOException {
		assertHasDelegate();

		return delegate.available();
	}

	@Override
	public void close() throws IOException {
		if (delegate != null)
			delegate.close();

		delegate = null;
	}

	@Override
	public void mark(int readlimit) {
		assertHasDelegate();

		delegate.mark(readlimit);
	}

	@Override
	public boolean markSupported() {
		assertHasDelegate();

		return delegate.markSupported();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		assertHasDelegate();

		return delegate.read(b, off, len);
	}

	@Override
	public int read(byte[] b) throws IOException {
		assertHasDelegate();

		return delegate.read(b);
	}

	@Override
	public void reset() throws IOException {
		assertHasDelegate();

		delegate.reset();
	}

	@Override
	public long skip(long n) throws IOException {
		assertHasDelegate();

		return delegate.skip(n);
	}
}
