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
package org.nightlabs.l10n;

import java.text.ParseException;

/**
 * Because a {@link java.util.Date} is parsed by the
 * {@link org.nightlabs.l10n.DateFormatter} multiple times with
 * different patterns,
 * a normal <tt>ParseException</tt> cannot be used. This one
 * bundles all <tt>ParseException</tt>s.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class DateParseException extends ParseException
{
	/**
	 * The serial version of this class.
	 */
	private static final long serialVersionUID = 1L;
	
	private long[] flags;
	private ParseException[] parseExceptions;

	/**
	 * @param s
	 */
	public DateParseException(String s, ParseException[] parseExceptions)
	{
		super(s, 0);
	}

	/**
	 * The flags correspond to the {@link ParseException}s returned by
	 * {@link #getParseExceptions()}.
	 * <p>
	 * They are one of the constants starting with "FLAGS_" in the class
	 * {@link DateFormatter}.
	 *
	 * @return Returns the flags.
	 */
	public long[] getFlags()
	{
		return flags;
	}

	/**
	 * @return Returns the parseExceptions.
	 * @see #getFlags()
	 */
	public ParseException[] getParseExceptions()
	{
		return parseExceptions;
	}
}
