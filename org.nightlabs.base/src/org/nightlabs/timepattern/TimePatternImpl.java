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
package org.nightlabs.timepattern;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class TimePatternImpl extends TimePattern
{
	/**
	 * The serial version of this class.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param _timePatternSet
	 */
	public TimePatternImpl(TimePatternSet _timePatternSet) {
		super(_timePatternSet);
		year = "*";
		month = "*";
		day = "*";
		dayOfWeek = "*";
		hour = "*";
		minute = "*";
	}

	/**
	 * @param _timePatternSet
	 * @param _year
	 * @param _month
	 * @param _day
	 * @param _dayOfWeek
	 * @param _hour
	 * @param _minute
	 * @throws TimePatternFormatException
	 */
	public TimePatternImpl(TimePatternSet _timePatternSet, String _year,
			String _month, String _day, String _dayOfWeek, String _hour,
			String _minute) throws TimePatternFormatException {
		super(_timePatternSet, _year, _month, _day, _dayOfWeek, _hour, _minute);
	}

	private String year;

	/**
	 * @see org.nightlabs.timepattern.TimePattern#getYear()
	 */
	@Override
	public String getYear() {
		return year;
	}

	/**
	 * @see org.nightlabs.timepattern.TimePattern#_setYear(java.lang.String)
	 */
	@Override
	protected void _setYear(String _year) {
		this.year = _year;
	}

	private String month;
	
	/**
	 * @see org.nightlabs.timepattern.TimePattern#getMonth()
	 */
	@Override
	public String getMonth() {
		return month;
	}

	/**
	 * @see org.nightlabs.timepattern.TimePattern#_setMonth(java.lang.String)
	 */
	@Override
	protected void _setMonth(String _month) {
		this.month = _month;
	}

	private String day;

	/**
	 * @see org.nightlabs.timepattern.TimePattern#getDay()
	 */
	@Override
	public String getDay() {
		return day;
	}

	/**
	 * @see org.nightlabs.timepattern.TimePattern#_setDay(java.lang.String)
	 */
	@Override
	protected void _setDay(String _day) {
		this.day = _day;
	}

	private String dayOfWeek;

	/**
	 * @see org.nightlabs.timepattern.TimePattern#getDayOfWeek()
	 */
	@Override
	public String getDayOfWeek() {
		return dayOfWeek;
	}

	/**
	 * @see org.nightlabs.timepattern.TimePattern#_setDayOfWeek(java.lang.String)
	 */
	@Override
	protected void _setDayOfWeek(String _dayOfWeek) {
		this.dayOfWeek = _dayOfWeek;
	}

	private String hour;

	/**
	 * @see org.nightlabs.timepattern.TimePattern#getHour()
	 */
	@Override
	public String getHour() {
		return hour;
	}

	/**
	 * @see org.nightlabs.timepattern.TimePattern#_setHour(java.lang.String)
	 */
	@Override
	protected void _setHour(String _hour) {
		this.hour = _hour;
	}

	private String minute;

	/**
	 * @see org.nightlabs.timepattern.TimePattern#getMinute()
	 */
	@Override
	public String getMinute() {
		return minute;
	}

	/**
	 * @see org.nightlabs.timepattern.TimePattern#_setMinute(java.lang.String)
	 */
	@Override
	protected void _setMinute(String _minute) {
		this.minute = _minute;
	}

	private TimePatternSet timePatternSet;

	/**
	 * @see org.nightlabs.timepattern.TimePattern#getTimePatternSet()
	 */
	@Override
	public TimePatternSet getTimePatternSet() {
		return timePatternSet;
	}

	/**
	 * @see org.nightlabs.timepattern.TimePattern#setTimePatternSet(org.nightlabs.timepattern.TimePatternSet)
	 */
	@Override
	protected void setTimePatternSet(TimePatternSet set) {
		this.timePatternSet = set;
	}
}
