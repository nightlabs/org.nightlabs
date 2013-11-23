package org.nightlabs.l10n;

import java.util.Date;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public interface IDateFormatter {
	public static final long FLAGS_DATE_SHORT_TIME_HM =
		DateFormatProvider.DATE_SHORT |
		DateFormatProvider.TIME;

	public static final long FLAGS_DATE_SHORT_TIME_HM_WEEKDAY =
		DateFormatProvider.DATE_SHORT |
		DateFormatProvider.DATE_WEEKDAY |
		DateFormatProvider.TIME;

	public static final long FLAGS_DATE_LONG_TIME_HM =
		DateFormatProvider.DATE_LONG |
		DateFormatProvider.TIME;

	public static final long FLAGS_DATE_LONG_TIME_HM_WEEKDAY =
		DateFormatProvider.DATE_LONG |
		DateFormatProvider.DATE_WEEKDAY |
		DateFormatProvider.TIME;

	public static final long FLAGS_DATE_SHORT_TIME_HMS =
		DateFormatProvider.DATE_SHORT |
		DateFormatProvider.TIME_SEC;

	public static final long FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY =
		DateFormatProvider.DATE_SHORT |
		DateFormatProvider.DATE_WEEKDAY |
		DateFormatProvider.TIME_SEC;

	public static final long FLAGS_DATE_LONG_TIME_HMS =
		DateFormatProvider.DATE_LONG |
		DateFormatProvider.TIME_SEC;

	public static final long FLAGS_DATE_LONG_TIME_HMS_WEEKDAY =
		DateFormatProvider.DATE_LONG |
		DateFormatProvider.DATE_WEEKDAY |
		DateFormatProvider.TIME_SEC;

	public static final long FLAGS_DATE_SHORT = DateFormatProvider.DATE_SHORT;
	public static final long FLAGS_DATE_SHORT_WEEKDAY = DateFormatProvider.DATE_SHORT | DateFormatProvider.DATE_WEEKDAY;
	public static final long FLAGS_DATE_LONG = DateFormatProvider.DATE_LONG;
	public static final long FLAGS_DATE_LONG_WEEKDAY = DateFormatProvider.DATE_LONG | DateFormatProvider.DATE_WEEKDAY;
	public static final long FLAGS_TIME_HM = DateFormatProvider.TIME;
	public static final long FLAGS_TIME_HMS = DateFormatProvider.TIME_SEC;
	public static final long FLAGS_TIME_HMS_MSEC = DateFormatProvider.TIME_MSEC;

	/**
	 * In {@link #parseDate(String)} all the flags are tried in the here defined
	 * order. Order is important, because otherwise sec or msec might be lost.
	 *
	 * Notice: Be sure to update the array {@link #FLAG_NAMES} too, when changing this array.
	 */
	public static final long[] FLAGS = new long[] {
		FLAGS_DATE_SHORT_TIME_HMS,
		FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY,
		FLAGS_DATE_LONG_TIME_HMS,
		FLAGS_DATE_LONG_TIME_HMS_WEEKDAY,

		FLAGS_DATE_SHORT_TIME_HM,
		FLAGS_DATE_SHORT_TIME_HM_WEEKDAY,
		FLAGS_DATE_LONG_TIME_HM,
		FLAGS_DATE_LONG_TIME_HM_WEEKDAY,

		FLAGS_DATE_SHORT,
		FLAGS_DATE_SHORT_WEEKDAY,
		FLAGS_DATE_LONG,
		FLAGS_DATE_LONG_WEEKDAY,

		FLAGS_TIME_HMS_MSEC,
		FLAGS_TIME_HMS,
		FLAGS_TIME_HM
	};

	/**
	 * This array contains descriptive strings for all the flags in the array {@link #FLAGS}
	 * in the same order (important!!)
	 */
	public static final String[] FLAG_NAMES = new String[] {
		"FLAGS_DATE_SHORT_TIME_HMS",
		"FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY",
		"FLAGS_DATE_LONG_TIME_HMS",
		"FLAGS_DATE_LONG_TIME_HMS_WEEKDAY",

		"FLAGS_DATE_SHORT_TIME_HM",
		"FLAGS_DATE_SHORT_TIME_HM_WEEKDAY",
		"FLAGS_DATE_LONG_TIME_HM",
		"FLAGS_DATE_LONG_TIME_HM_WEEKDAY",

		"FLAGS_DATE_SHORT",
		"FLAGS_DATE_SHORT_WEEKDAY",
		"FLAGS_DATE_LONG",
		"FLAGS_DATE_LONG_WEEKDAY",

		"FLAGS_TIME_HMS_MSEC",
		"FLAGS_TIME_HMS",
		"FLAGS_TIME_HM"
	};

	DateFormatProvider getDateFormatProvider();
	Date parseDate(String s)throws DateParseException;
	String formatDate(Date dt, long flags);
}
