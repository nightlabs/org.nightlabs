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
package org.nightlabs.util;

import java.util.Calendar;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public abstract class CalendarUtil {

	/**
	 * Sets the given {@link Calendar} instance to
	 * reflect the very last moment of its current day.
	 * This means it sets the time component to 23:59:59.999.
	 * 
	 * @param calendar The Calendar to change
	 */
	public static void setToMaxTimeOfDay(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
	}
	
	/**
	 * Sets the given {@link Calendar} instance to
	 * reflect the very first moment of its current day.
	 * This means it sets the time component to 00:00:00.000.
	 * 
	 * @param calendar The Calendar to change
	 */
	public static void setToMinTimeOfDay(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
	}
	
	private static long oneYearInMillis = 100 * 60 * 60 * 24 * 365;
	/**
	 * returns one year in milliseconds
	 * @return one year in milliseconds
	 */
	public static long getOneYearInMillis() {
		return oneYearInMillis;
	}
	
	private static long oneDayInMillis = 100 * 60 * 60 * 24;
	/**
	 * returns one day in milliseconds
	 * @return one day in milliseconds
	 */
	public static long getOneDayInMillis() {
		return oneDayInMillis;
	}

	private static long oneHourInMillis = 100 * 60 * 60;
	/**
	 * returns one hour in milliseconds
	 * @return one hour in milliseconds
	 */
	public static long getOneHourInMillis() {
		return oneHourInMillis;
	}
	
}
