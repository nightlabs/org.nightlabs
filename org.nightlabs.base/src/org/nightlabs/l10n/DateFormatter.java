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

import java.util.Date;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @deprecated Use {@link IDateFormatter} and {@link GlobalDateFormatter}.
 */
@Deprecated
public class DateFormatter
{
	/** @deprecated Use the constants defined in {@link IDateFormatter} */
	@Deprecated
	public static final long FLAGS_DATE_SHORT_TIME_HM = IDateFormatter.FLAGS_DATE_SHORT_TIME_HM;

	/** @deprecated Use the constants defined in {@link IDateFormatter} */
	@Deprecated
	public static final long FLAGS_DATE_SHORT_TIME_HM_WEEKDAY = IDateFormatter.FLAGS_DATE_SHORT_TIME_HM_WEEKDAY;

	/** @deprecated Use the constants defined in {@link IDateFormatter} */
	@Deprecated
	public static final long FLAGS_DATE_LONG_TIME_HM = IDateFormatter.FLAGS_DATE_LONG_TIME_HM;

	/** @deprecated Use the constants defined in {@link IDateFormatter} */
	@Deprecated
	public static final long FLAGS_DATE_LONG_TIME_HM_WEEKDAY = IDateFormatter.FLAGS_DATE_LONG_TIME_HM_WEEKDAY;

	/** @deprecated Use the constants defined in {@link IDateFormatter} */
	@Deprecated
	public static final long FLAGS_DATE_SHORT_TIME_HMS = IDateFormatter.FLAGS_DATE_SHORT_TIME_HMS;

	/** @deprecated Use the constants defined in {@link IDateFormatter} */
	@Deprecated
	public static final long FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY = IDateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY;

	/** @deprecated Use the constants defined in {@link IDateFormatter} */
	@Deprecated
	public static final long FLAGS_DATE_LONG_TIME_HMS = IDateFormatter.FLAGS_DATE_LONG_TIME_HMS;

	/** @deprecated Use the constants defined in {@link IDateFormatter} */
	@Deprecated
	public static final long FLAGS_DATE_LONG_TIME_HMS_WEEKDAY = IDateFormatter.FLAGS_DATE_LONG_TIME_HMS_WEEKDAY;

	/** @deprecated Use the constants defined in {@link IDateFormatter} */
	@Deprecated
	public static final long FLAGS_DATE_SHORT = IDateFormatter.FLAGS_DATE_SHORT;
	/** @deprecated Use the constants defined in {@link IDateFormatter} */
	@Deprecated
	public static final long FLAGS_DATE_SHORT_WEEKDAY = IDateFormatter.FLAGS_DATE_SHORT_WEEKDAY;
	/** @deprecated Use the constants defined in {@link IDateFormatter} */
	@Deprecated
	public static final long FLAGS_DATE_LONG = IDateFormatter.FLAGS_DATE_LONG;
	/** @deprecated Use the constants defined in {@link IDateFormatter} */
	@Deprecated
	public static final long FLAGS_DATE_LONG_WEEKDAY = IDateFormatter.FLAGS_DATE_LONG_WEEKDAY;
	/** @deprecated Use the constants defined in {@link IDateFormatter} */
	@Deprecated
	public static final long FLAGS_TIME_HM = IDateFormatter.FLAGS_TIME_HM;
	/** @deprecated Use the constants defined in {@link IDateFormatter} */
	@Deprecated
	public static final long FLAGS_TIME_HMS = IDateFormatter.FLAGS_TIME_HMS;
	/** @deprecated Use the constants defined in {@link IDateFormatter} */
	@Deprecated
	public static final long FLAGS_TIME_HMS_MSEC = IDateFormatter.FLAGS_TIME_HMS_MSEC;

	/**
	 * In {@link #parseDate(String)} all the flags are tried in the here defined
	 * order. Order is important, because otherwise sec or msec might be lost.
	 *
	 * Notice: Be sure to update the array {@link #FLAG_NAMES} too, when changing this array.
	 * @deprecated Use the constants defined in {@link IDateFormatter}
	 */
	@Deprecated
	public static final long[] FLAGS = IDateFormatter.FLAGS;

	/**
	 * This array contains descriptive strings for all the flags in the array {@link #FLAGS}
	 * in the same order (important!!)
	 * @deprecated Use the constants defined in {@link IDateFormatter}
	 */
	@Deprecated
	public static final String[] FLAG_NAMES = IDateFormatter.FLAG_NAMES;

	/** @deprecated Use the non-static methods in {@link IDateFormatter} via {@link GlobalDateFormatter}. */
	@Deprecated
	public static Date parseDate(final String s)
	throws DateParseException
	{
		return GlobalDateFormatter.sharedInstance().parseDate(s);
	}

	/** @deprecated Use the non-static methods in {@link IDateFormatter} via {@link GlobalDateFormatter}. */
	@Deprecated
	public static String formatDate(final Date dt, final long flags)
	{
		return GlobalDateFormatter.sharedInstance().formatDate(dt, flags);
	}

	/** @deprecated Use {@link IDateFormatter#formatDate(Date, long)} with the flags of your choice. */
	@Deprecated
	public static String formatDateShortTimeHM(final Date dt, final boolean weekDay)
	{
		return formatDate(dt,
				weekDay ? IDateFormatter.FLAGS_DATE_SHORT_TIME_HM_WEEKDAY : IDateFormatter.FLAGS_DATE_SHORT_TIME_HM);
	}

	/** @deprecated Use {@link IDateFormatter#formatDate(Date, long)} with the flags of your choice. */
	@Deprecated
	public static String formatDateLongTimeHM(final Date dt, final boolean weekDay)
	{
		return formatDate(dt,
				weekDay ? IDateFormatter.FLAGS_DATE_LONG_TIME_HM_WEEKDAY : IDateFormatter.FLAGS_DATE_LONG_TIME_HM);
	}

	/** @deprecated Use {@link IDateFormatter#formatDate(Date, long)} with the flags of your choice. */
	@Deprecated
	public static String formatDateShortTimeHMS(final Date dt, final boolean weekDay)
	{
		return formatDate(dt,
				weekDay ? IDateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY : IDateFormatter.FLAGS_DATE_SHORT_TIME_HMS);
	}

	/** @deprecated Use {@link IDateFormatter#formatDate(Date, long)} with the flags of your choice. */
	@Deprecated
	public static String formatDateLongTimeHMS(final Date dt, final boolean weekDay)
	{
		return formatDate(dt,
				weekDay ? IDateFormatter.FLAGS_DATE_LONG_TIME_HMS_WEEKDAY : IDateFormatter.FLAGS_DATE_LONG_TIME_HMS);
	}

	/**
	 * Produces sth. like 2004-12-24 (means only digits and no
	 * week day).
	 * @deprecated Use {@link IDateFormatter#formatDate(Date, long)} with the flags of your choice.
	 */
	@Deprecated
	public static String formatDateShort(final Date dt, final boolean weekDay)
	{
		return formatDate(dt,
				weekDay ? IDateFormatter.FLAGS_DATE_SHORT_WEEKDAY : IDateFormatter.FLAGS_DATE_SHORT);
	}

	/**
	 * Produces sth. like 2004 December 24
	 * @deprecated Use {@link IDateFormatter#formatDate(Date, long)} with the flags of your choice.
	 */
	@Deprecated
	public static String formatDateLong(final Date dt, final boolean weekDay)
	{
		return formatDate(dt,
				weekDay ? IDateFormatter.FLAGS_DATE_LONG_WEEKDAY : IDateFormatter.FLAGS_DATE_LONG);
	}

	/**
	 * Produces sth. like 23:51 or 11:51 PM
	 * @deprecated Use {@link IDateFormatter#formatDate(Date, long)} with the flags of your choice.
	 */
	@Deprecated
	public static String formatTimeHM(final Date dt)
	{
		return formatDate(dt, IDateFormatter.FLAGS_TIME_HM);
	}

	/**
	 * Produces sth. like 23:51:47
	 * @deprecated Use {@link IDateFormatter#formatDate(Date, long)} with the flags of your choice.
	 */
	@Deprecated
	public static String formatTimeHMS(final Date dt)
	{
		return formatDate(dt, IDateFormatter.FLAGS_TIME_HMS);
	}

	/**
	 * Produces sth. like 23:51:47.095
	 * @deprecated Use {@link IDateFormatter#formatDate(Date, long)} with the flags of your choice.
	 */
	@Deprecated
	public static String formatTimeHMSmsec(final Date dt)
	{
		return formatDate(dt, IDateFormatter.FLAGS_TIME_HMS_MSEC);
	}
}
