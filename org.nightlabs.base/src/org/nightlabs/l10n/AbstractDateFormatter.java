package org.nightlabs.l10n;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.nightlabs.util.CollectionUtil;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public abstract class AbstractDateFormatter implements IDateFormatter {

	/* (non-Javadoc)
	 * @see org.nightlabs.l10n.IDateFormatter#parseDate(java.lang.String)
	 */
	@Override
	public Date parseDate(final String s) throws DateParseException
	{
		final DateFormatProvider dfp = getDateFormatProvider();
		Date res = null;
		List<ParseException> parseExceptions = null;
		for (final long element : FLAGS) {
			final DateFormat dateFormat = dfp.getDateFormat(element);
			try {
				res = dateFormat.parse(s);
			} catch (final ParseException x) {
				if (parseExceptions == null)
					parseExceptions = new LinkedList<ParseException>();

				parseExceptions.add(x);
			}
			if (res != null)
				return res;
		}

		if (parseExceptions == null)
			throw new IllegalStateException("Have neither any ParseException nor a Date result - sth.'s really wrong!");

		throw new DateParseException(
				"Could not parse \""+s+"\" into a Date!",
				CollectionUtil.collection2TypedArray(
						parseExceptions, ParseException.class));
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.l10n.IDateFormatter#formatDate(java.util.Date, long)
	 */
	@Override
	public String formatDate(final Date dt, final long flags)
	{
		if (dt == null)
			return null;

		final DateFormat dateFormat = getDateFormatProvider().getDateFormat(flags);
		return dateFormat.format(dt);
	}

	public String formatDateShortTimeHM(final Date dt, final boolean weekDay)
	{
		return formatDate(dt,
				weekDay ? FLAGS_DATE_SHORT_TIME_HM_WEEKDAY : FLAGS_DATE_SHORT_TIME_HM);
	}

	public String formatDateLongTimeHM(final Date dt, final boolean weekDay)
	{
		return formatDate(dt,
				weekDay ? FLAGS_DATE_LONG_TIME_HM_WEEKDAY : FLAGS_DATE_LONG_TIME_HM);
	}

	public String formatDateShortTimeHMS(final Date dt, final boolean weekDay)
	{
		return formatDate(dt,
				weekDay ? FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY : FLAGS_DATE_SHORT_TIME_HMS);
	}

	public String formatDateLongTimeHMS(final Date dt, final boolean weekDay)
	{
		return formatDate(dt,
				weekDay ? FLAGS_DATE_LONG_TIME_HMS_WEEKDAY : FLAGS_DATE_LONG_TIME_HMS);
	}

	/**
	 * Produces sth. like 2004-12-24 (means only digits and no
	 * week day).
	 */
	public String formatDateShort(final Date dt, final boolean weekDay)
	{
		return formatDate(dt,
				weekDay ? FLAGS_DATE_SHORT_WEEKDAY : FLAGS_DATE_SHORT);
	}

	/**
	 * Produces sth. like 2004 December 24
	 */
	public String formatDateLong(final Date dt, final boolean weekDay)
	{
		return formatDate(dt,
				weekDay ? FLAGS_DATE_LONG_WEEKDAY : FLAGS_DATE_LONG);
	}

	/**
	 * Produces sth. like 23:51 or 11:51 PM
	 */
	public String formatTimeHM(final Date dt)
	{
		return formatDate(dt, FLAGS_TIME_HM);
	}

	/**
	 * Produces sth. like 23:51:47
	 */
	public String formatTimeHMS(final Date dt)
	{
		return formatDate(dt, FLAGS_TIME_HMS);
	}

	/**
	 * Produces sth. like 23:51:47.095
	 */
	public String formatTimeHMSmsec(final Date dt)
	{
		return formatDate(dt, FLAGS_TIME_HMS_MSEC);
	}
}
