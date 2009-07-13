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

import java.text.DateFormat;

import org.nightlabs.config.Config;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Marc Klinger - marc at nightlabs dot de (API documentation fixes)
 */
public interface DateFormatProvider
{
	/**
	 * This method is called once directly after the DateFormatProvider
	 * has been instantiated.
	 *
	 * @param config The configuration
	 * @param isoLanguage The language to use
	 * @param isoCountry The country to use
	 */
	public void init(Config config, String isoLanguage, String isoCountry);

	public static final long DATE         = 0x000001L;
	public static final long DATE_LONG    = 0x000002L | DATE;
	public static final long DATE_SHORT   = 0x000004L | DATE;
	public static final long DATE_WEEKDAY = 0x000008L | DATE;

	public static final long TIME         = 0x100000L;
	public static final long TIME_SEC     = 0x200000L | TIME;
	public static final long TIME_MSEC    = 0x400000L | TIME_SEC;

	/**
	 * Warning! The DateFormatProvider can cache the DateFormat instances
	 * and therefore you should NEVER change the returned object. The
	 * effects on other parts of the software are unpredictable.
	 *
	 * @param flags The flags define the behaviour of the DateFormat.
	 * @return Returns an instance of DateFormat according to the given flags.
	 */
	public DateFormat getDateFormat(long flags);
}
