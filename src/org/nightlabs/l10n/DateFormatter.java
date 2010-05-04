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
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;
import org.nightlabs.util.CollectionUtil;
import org.nightlabs.util.NLLocale;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class DateFormatter
{
	private static DateFormatter _sharedInstance = null;

	/**
	 * In case there is no shared instance existing yet, it will be
	 * created with the shared instance of Config. Hence, this method
	 * fails if there is no shared instance of Config.
	 *
	 * @return Returns the shared instance of DateFormatter.
	 */
	public static DateFormatter sharedInstance()
	{
		DateFormatterFactory dateFormatterFactory = getDateFormatterFactory();
		if (dateFormatterFactory != null)
			return dateFormatterFactory.sharedInstance();

		if (_sharedInstance == null)
			_sharedInstance = new DateFormatter(Config.sharedInstance(), NLLocale.getDefault());

		return _sharedInstance;
	}


	/**
	 * If there is a system property set with this name, the DateFormatter's static methods
	 * will delegate to an instance of the class specified by this system property.
	 */
	public static final String PROPERTY_KEY_DATE_FORMATTER_FACTORY = DateFormatterFactory.class.getName();

	private static DateFormatterFactory dateFormatterFactory = null;

	private synchronized static DateFormatterFactory getDateFormatterFactory()
	{
		if (dateFormatterFactory == null) {
			String dateFormatterFactoryClassName = System.getProperty(PROPERTY_KEY_DATE_FORMATTER_FACTORY);
			if (dateFormatterFactoryClassName == null)
				return null;

			Class<?> dateFormatterFactoryClass;
			try {
				dateFormatterFactoryClass = Class.forName(dateFormatterFactoryClassName);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("The system-property '" + PROPERTY_KEY_DATE_FORMATTER_FACTORY + "' was specified as '"+dateFormatterFactoryClassName+"' but this class cannot be found!", e);
			}

			try {
				dateFormatterFactory = (DateFormatterFactory) dateFormatterFactoryClass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Instantiating the class '"+dateFormatterFactoryClassName+"' specified by the system-property '" + PROPERTY_KEY_DATE_FORMATTER_FACTORY + "' was found, but it could not be instantiated!", e);
			}
		}
		return dateFormatterFactory;
	}

	private final Config config;
	private final Locale locale;

	/**
	 * @param config
	 */
	public DateFormatter(Config config, Locale locale)
	{
		this.config = config;
		this.locale = locale;
		try {
			config.createConfigModule(GlobalL10nSettings.class);
		} catch (ConfigException e) {
			throw new RuntimeException(e);
		}
	}

	public Locale getLocale() {
		return locale;
	}

	private DateFormatProvider dateFormatProvider = null;

	public DateFormatProvider getDateFormatProvider()
	{
		if (dateFormatProvider != null)
			return dateFormatProvider;

		L10nFormatCfMod l10nFormatCfMod;
		try {
			l10nFormatCfMod = ConfigUtil.createConfigModule(
					config, L10nFormatCfMod.class, locale.getLanguage(), locale.getCountry());

			String className = l10nFormatCfMod.getDateFormatProvider();

			Class<?> clazz = Class.forName(className);
			if (!DateFormatProvider.class.isAssignableFrom(clazz))
				throw new ClassCastException("class \""+className+"\" defined in config module \""+L10nFormatCfMod.class.getName()+"\" does not implement interface \""+DateFormatProvider.class.getName()+"\"!");

			DateFormatProvider dfp = (DateFormatProvider) clazz.newInstance();
			dfp.init(config, locale.getLanguage(), locale.getCountry());

			this.dateFormatProvider = dfp;
			return dateFormatProvider;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

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

	public static Date parseDate(String s)
	throws DateParseException
	{
		DateFormatProvider dfp = sharedInstance().getDateFormatProvider();
		Date res = null;
		List<ParseException> parseExceptions = null;
		for (long element : FLAGS) {
			DateFormat dateFormat = dfp.getDateFormat(element);
			try {
				res = dateFormat.parse(s);
			} catch (ParseException x) {
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

	public static String formatDate(Date dt, long flags)
	{
		if (dt == null)
			return null;

		DateFormat dateFormat = sharedInstance().getDateFormatProvider().getDateFormat(flags);
		return dateFormat.format(dt);
	}

	public static String formatDateShortTimeHM(Date dt, boolean weekDay)
	{
		return formatDate(dt,
						weekDay ? FLAGS_DATE_SHORT_TIME_HM_WEEKDAY : FLAGS_DATE_SHORT_TIME_HM);
	}

	public static String formatDateLongTimeHM(Date dt, boolean weekDay)
	{
		return formatDate(dt,
						weekDay ? FLAGS_DATE_LONG_TIME_HM_WEEKDAY : FLAGS_DATE_LONG_TIME_HM);
	}

	public static String formatDateShortTimeHMS(Date dt, boolean weekDay)
	{
		return formatDate(dt,
						weekDay ? FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY : FLAGS_DATE_SHORT_TIME_HMS);
	}

	public static String formatDateLongTimeHMS(Date dt, boolean weekDay)
	{
		return formatDate(dt,
				weekDay ? FLAGS_DATE_LONG_TIME_HMS_WEEKDAY : FLAGS_DATE_LONG_TIME_HMS);
	}

	/**
	 * Produces sth. like 2004-12-24 (means only digits and no
	 * week day).
	 */
	public static String formatDateShort(Date dt, boolean weekDay)
	{
		return formatDate(dt,
						weekDay ? FLAGS_DATE_SHORT_WEEKDAY : FLAGS_DATE_SHORT);
	}

	/**
	 * Produces sth. like 2004 December 24
	 */
	public static String formatDateLong(Date dt, boolean weekDay)
	{
		return formatDate(dt,
				weekDay ? FLAGS_DATE_LONG_WEEKDAY : FLAGS_DATE_LONG);
	}

	/**
	 * Produces sth. like 23:51 or 11:51 PM
	 */
	public static String formatTimeHM(Date dt)
	{
		return formatDate(dt, FLAGS_TIME_HM);
	}

	/**
	 * Produces sth. like 23:51:47
	 */
	public static String formatTimeHMS(Date dt)
	{
		return formatDate(dt, FLAGS_TIME_HMS);
	}

	/**
	 * Produces sth. like 23:51:47.095
	 */
	public static String formatTimeHMSmsec(Date dt)
	{
		return formatDate(dt, FLAGS_TIME_HMS_MSEC);
	}
}
