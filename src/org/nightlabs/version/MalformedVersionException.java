/* ********************************************************************
 * NightLabsBase - Utilities by NightLabs                             *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.version;


/**
 * @author marco
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @deprecated Moved to separate artifact "org.nightlabs.version". The package "org.nightlabs.version" should
 * be removed from artifact "org.nightlabs.base" and a dependency onto artifact "org.nightlabs.version" should
 * be introduced instead. Or even better we should check if we can migrate to the version-handling-classes from OSGI
 * (e.g. org.osgi.framework.Version and org.eclipse.osgi.service.resolver.VersionRange). Marco :-)
 */
@Deprecated
public class MalformedVersionException extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new exception with <code>null</code> as its detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 */
	public MalformedVersionException() {
		super();
	}
	/**
	 * Constructs a new exception with the specified detail message.  The
	 * cause is not initialized, and may subsequently be initialized by
	 * a call to {@link #initCause}.
	 * @param   message the detail message. The detail message is saved for
	 *          later retrieval by the {@link #getMessage()} method.
	 */
	public MalformedVersionException(String message) {
		super(message);
	}
	/**
	 * Constructs a new exception with the specified cause and a detail
	 * message of <tt>(cause==null ? null : cause.toString())</tt> (which
	 * typically contains the class and detail message of <tt>cause</tt>).
	 * This constructor is useful for exceptions that are little more than
	 * wrappers for other throwables (for example, {@link
	 * java.security.PrivilegedActionException}).
	 * @param  cause the cause (which is saved for later retrieval by the
	 *         {@link #getCause()} method).  (A <tt>null</tt> value is
	 *         permitted, and indicates that the cause is nonexistent or
	 *         unknown.)
	 */
	public MalformedVersionException(Throwable cause) {
		super(cause);
	}
	/**
	 * Constructs a new exception with the specified detail message and
	 * cause.  <p>Note that the detail message associated with
	 * <code>cause</code> is <i>not</i> automatically incorporated in
	 * this exception's detail message.
	 * @param  message the detail message (which is saved for later retrieval
	 *         by the {@link #getMessage()} method).
	 * @param  cause the cause (which is saved for later retrieval by the
	 *         {@link #getCause()} method).  (A <tt>null</tt> value is
	 *         permitted, and indicates that the cause is nonexistent or
	 *         unknown.)
	 */
	public MalformedVersionException(String message, Throwable cause) {
		super(message, cause);
	}
}
