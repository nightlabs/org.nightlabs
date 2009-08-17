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
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.nightlabs.config.Config;
import org.nightlabs.i18n.NLLocale;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Marc Klinger - marc at nightlabs dot de (API documentation fixes)
 */
public class DefaultDateFormatProvider implements DateFormatProvider
{
	protected Config config;
	protected DefaultDateFormatCfMod defaultDateFormatCfMod;
	protected String isoLanguage;
	protected String isoCountry;

	public DefaultDateFormatProvider()
	{
	}

	/**
	 * When parsing a date without time, it is necessary to assume that the entered
	 * date is UTC. Otherwise, the date will "jump" due to the translation from/to UTC in combination
	 * with truncating of hours/minutes. Thus, we force the DateFormat instances which are used
	 * for date-only (no time) to be UTC rather than local.
	 */
	private static final Map<Long, Boolean> flags2utc;
	static {
		Map<Long, Boolean> m = new HashMap<Long, Boolean>();

		m.put(DateFormatter.FLAGS_DATE_SHORT, Boolean.TRUE);
		m.put(DateFormatter.FLAGS_DATE_SHORT_WEEKDAY, Boolean.TRUE);
		m.put(DateFormatter.FLAGS_DATE_LONG, Boolean.TRUE);
		m.put(DateFormatter.FLAGS_DATE_LONG_WEEKDAY, Boolean.TRUE);

		flags2utc = Collections.unmodifiableMap(m);
	}

	/**
	 * @see org.nightlabs.l10n.DateFormatProvider#init(Config, String, String)
	 */
	public void init(Config config, String isoLanguage, String isoCountry)
	{
		try {
			this.config = config;
			this.isoLanguage = isoLanguage;
			this.isoCountry = isoCountry;
			this.defaultDateFormatCfMod = ConfigUtil.createConfigModule(config, DefaultDateFormatCfMod.class, isoLanguage, isoCountry);
		} catch (RuntimeException x) {
			throw x;
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	/**
	 * {@link DateFormat} is not thread-safe! Therefore, we're now managing a ThreadLocal of what it was previously.
	 * <p>
	 * key: Long flags<br/>
	 * value: SimpleDateFormat dateFormat
	 * </p>
	 */
	protected ThreadLocal<Map<Long, SimpleDateFormat>> dateFormatsByFlags = new ThreadLocal<Map<Long,SimpleDateFormat>>() {
		@Override
		protected Map<Long, SimpleDateFormat> initialValue() {
			return new HashMap<Long, SimpleDateFormat>();
		}
	};

	/**
	 * @see org.nightlabs.l10n.DateFormatProvider#getDateFormat(long)
	 */
	public DateFormat getDateFormat(long flags)
	{
		Long flagsL = new Long(flags);
		SimpleDateFormat sdf;
//		synchronized (dateFormatsByFlags) {
			sdf = dateFormatsByFlags.get().get(flagsL);
//		}
		if (sdf == null) {
			String datePattern = null;
			String timePattern = null;

			if ((flags & DATE_LONG) == DATE_LONG &&  (flags & DATE_WEEKDAY) == DATE_WEEKDAY)
				datePattern = defaultDateFormatCfMod.getDateLongWeekday();
			else if ((flags & DATE_LONG) == DATE_LONG)
				datePattern = defaultDateFormatCfMod.getDateLong();
			else if ((flags & DATE_SHORT) == DATE_SHORT &&  (flags & DATE_WEEKDAY) == DATE_WEEKDAY)
				datePattern = defaultDateFormatCfMod.getDateShortWeekday();
			else if ((flags & DATE_SHORT) == DATE_SHORT)
				datePattern = defaultDateFormatCfMod.getDateShort();

			if ((flags & TIME_MSEC) == TIME_MSEC)
				timePattern = defaultDateFormatCfMod.getTimeHMSms();
			else if ((flags & TIME_SEC) == TIME_SEC)
				timePattern = defaultDateFormatCfMod.getTimeHMS();
			else if ((flags & TIME) == TIME)
				timePattern = defaultDateFormatCfMod.getTimeHM();

			String pattern;
			if (datePattern != null && timePattern != null)
				pattern = datePattern + ' ' + timePattern;
			else if (datePattern != null)
				pattern = datePattern;
			else
				pattern = timePattern;

			if (pattern == null)
				throw new IllegalArgumentException("It seems the flags were not producing a senseful pattern!");

			DateFormatSymbols dfs = new DateFormatSymbols(NLLocale.getDefault());
			dfs.setAmPmStrings(defaultDateFormatCfMod.getAmPmStrings());
			dfs.setEras(defaultDateFormatCfMod.getEras());
			dfs.setMonths(defaultDateFormatCfMod.getMonthsLong());
			dfs.setShortMonths(defaultDateFormatCfMod.getMonthsShort());
			dfs.setWeekdays(defaultDateFormatCfMod.getWeekDaysLong());
			dfs.setShortWeekdays(defaultDateFormatCfMod.getWeekDaysShort());
			sdf = new SimpleDateFormat(pattern, dfs);

			if (Boolean.TRUE.equals(flags2utc.get(flagsL)))
				sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

			// Maybe we should allow configuration of the time zone in a config-module. For now, it's ok to use the OS' time zone.
//			sdf.setTimeZone(TimeZone.getDefault());

//			synchronized (dateFormatsByFlags) {
				dateFormatsByFlags.get().put(flagsL, sdf);
//			}
		}
		return sdf;
	}

}
