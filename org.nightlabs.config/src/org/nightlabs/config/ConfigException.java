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
package org.nightlabs.config;

/**
 * An exception thrown in cases of configuration errors.
 * @version $Revision: 12386 $ - $Date: 2008-10-10 14:42:28 +0200 (Fr, 10 Okt 2008) $
 */
public class ConfigException extends RuntimeException
{
	/**
	 * The serial version of this class.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Create a new ConfigException.
	 */
	public ConfigException()
	{
		super();
	}

	/**
	 * Create a new ConfigException.
	 * @param  message the detail message (which is saved for later retrieval
	 *         by the {@link #getMessage()} method).
	 */
	public ConfigException(String message)
	{
		super(message);
	}

	/**
	 * Create a new ConfigException.
	 * @param  message the detail message (which is saved for later retrieval
	 *         by the {@link #getMessage()} method).
	 * @param  cause the cause (which is saved for later retrieval by the
	 *         {@link #getCause()} method).  (A <tt>null</tt> value is
	 *         permitted, and indicates that the cause is nonexistent or
	 *         unknown.)
	 */
	public ConfigException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Create a new ConfigException.
	 * @param  cause the cause (which is saved for later retrieval by the
	 *         {@link #getCause()} method).  (A <tt>null</tt> value is
	 *         permitted, and indicates that the cause is nonexistent or
	 *         unknown.)
	 */
	public ConfigException(Throwable cause)
	{
		super(cause);
	}
}
