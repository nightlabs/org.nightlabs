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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * An instance of <code>TimePattern</code> holds periods of time for the following
 * fields: year, month, day, dayOfWeek, hour, minute.
 * <p>
 * It can be used to check whether a certain moment of time matches a pattern defined
 * by the time periods within these fields. This is useful for scheduled execution of a timer
 * job or for the definition of the validity of an admittance (or everything else that requires
 * complex time configurations).
 * </p>
 * <p>
 * The periods are 100%
 * <a href="http://www.littletechshoppe.com/servers/extensions/cron/crontab_5.html">linux cron compatible</a>
 * with one exception and two extensions:
 * <ul>
 * <li>We do <b>not</b> support month names. You must use numbers (1 = January, 12 = December).</li>
 * <li>We support an additional <i>year</i> field that's not supported by cron.</li>
 * <li>We support "/n" where <i>n</i> is a number and the expression means all values that
 * are dividable by <i>n</i> (this is different from "* /n" where the first possible value for the
 * field is non-zero or non-dividable by <i>n</i> - e.g. the month field).
 * </ul>
 * </p>
 * <p>
 * Each period can define lists of values by separating the values with
 * commas (",") or define ranges by defining the <i>from</i> and the <i>to</i> value separated
 * by a minus ("-"). Ranges and lists can be combined. You can use the "*" as wildcard for all possible
 * values (e.g. month: * = 1-12; hour: * = 0-23; minute: * = 0-59).
 * </p>
 * <p>
 * Additionally, you can define step values. For example "15-45/5" means
 * the same as "15,20,25,30,35,40,45" (or in other words every 5 between 15 and 45).
 * Note, that this is <b>not</b> the same as "14-45/5", because it always starts with the
 * first given value and then steps up by the given step size. Hence, "14-45/5" equals
 * "14,19,24,29,34,39,44". Instead of a range before the slash, you can use "*":
 * A minute field with the value "* /2" means every two minutes. Because months start with
 * 1, the expression "* /2" means all odd values, while the same in the hour field means all
 * even values.
 * </p>
 * <p>
 * In the <i>dayOfWeek</i> field, you can use both, numbers and names, where 1=Monday and 6=Saturday. Both, 0 and 7 mean
 * Sunday. You can use the following dayOfWeek names:
 * <ul>
 * <li>mon: Monday = 1</li>
 * <li>tue: Tuesday = 2</li>
 * <li>wed: Wednesday = 3</li>
 * <li>thu: Thursday = 4</li>
 * <li>fri: Friday = 5</li>
 * <li>sat: Saturday = 6</li>
 * <li>sun: Sunday = 0, 7</li>
 * </ul>
 * Note, that "sun" is automatically assumed to be the first day, if you write "sun-xxx" and the last day in "xxx-sun" (where
 * <i>xxx</i> is another day). As the first day is 0, the expression "* /2" equals "0,2,4,6" or "sun,tue,thu,sat" or simply "/2".
 * </p>
 * <p>
 * An empty field (i.e. the field is <code>null</code>) equals a field containing "*".
 * Thus, a new instance of this class (generated with the no-parameter-constructor) will
 * match all timestamps.
 * </p>
 *
 * <p>
 * Examples:<br/>
 * &nbsp;&nbsp;month="*"<br/>
 * &nbsp;&nbsp;day="1-7"<br/>
 * &nbsp;&nbsp;dayOfWeek="sun"<br/>
 * &nbsp;&nbsp;hour="0,6,12,18"<br/>
 * &nbsp;&nbsp;minute="0"<br/>
 * <br/>
 * This pattern could define an execution of a job on the first sunday of every
 * month 4 times a day at midnight, 6:00, 12:00 and 18:00.
 * </p>
 *
 * <p>
 * &nbsp;&nbsp;month="4-10"<br/>
 * &nbsp;&nbsp;day="*"<br/>
 * &nbsp;&nbsp;dayOfWeek="mon-fri"<br/>
 * &nbsp;&nbsp;hour="8-14,16-22"<br/>
 * &nbsp;&nbsp;minute="*"<br/>
 * <br/>
 * This pattern could be used to control the admittance to a location that is open from
 * April to October on weekdays (monday to friday). On these days, it opens at 8:00, closes
 * at 14:59:59 and reopens at 16:00 again to finally close at 22:59:59.
 * </p>
 *
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Alexander Bieber - alex at nightlabs dot de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public abstract class TimePattern
	implements Serializable
{
	private static final long serialVersionUID = 1L;
	public static final byte YEAR = 0;
	public static final byte MONTH = 1;
	public static final byte DAY = 2;
	public static final byte DAY_OF_WEEK = 3;
	public static final byte HOUR = 4;
	public static final byte MINUTE = 5;
	
	protected static final int YEAR_MIN = 1970;
	protected static final int YEAR_MAX = 2100;

	protected static final int MONTH_MIN = 1;
	protected static final int MONTH_MAX = 12;

	protected static final int DAY_MIN = 1;
	protected static final int DAY_MAX = 31;

	protected static final int DAY_OF_WEEK_MIN = 0;
	protected static final int DAY_OF_WEEK_MAX = 7;

	protected static final int HOUR_MIN = 0;
	protected static final int HOUR_MAX = 23;

	protected static final int MINUTE_MIN = 0;
	protected static final int MINUTE_MAX = 59;

	public static final String SUNDAY_NAME = "sun";
	public static final String MONDAY_NAME = "mon";
	public static final String TUESDAY_NAME = "tue";
	public static final String WEDNESDAY_NAME = "wed";
	public static final String THURSDAY_NAME = "thu";
	public static final String FRIDAY_NAME = "fri";
	public static final String SATURDAY_NAME = "sat";

	protected static final String[] DAYOFWEEK_NAMES = new String[] {
		SUNDAY_NAME, MONDAY_NAME, TUESDAY_NAME,
		WEDNESDAY_NAME, THURSDAY_NAME, FRIDAY_NAME, SATURDAY_NAME, SUNDAY_NAME
	};
	
	public static final byte CONVERT_DAYOFWEEK_NUMBER_TO_NAME = 1;
	public static final byte CONVERT_DAYOFWEEK_NAME_TO_NUMBER = 2;

	/**
	 * cachedPeriods is used to speed up the matches(...) method by shortcutting
	 * getPeriods(byte). This array is set to <code>null</code> whenever the pattern
	 * is changed. The method getPeriods(byte) initializes it and parses the strings
	 * only if there is no cached value existing.
	 * 
	 * @jdo.field persistence-modifier="none"
	 */
	private transient List[] cachedPeriods = null;

	/**
	 * ??!? jdo tags not necessary here?! Marco.
	 * @!jdo.field persistence-modifier="none"
	 */
	private transient String thisString = null;

	private transient TimePatternFieldPeriod firstAndLastYear = null;
	
	private transient TimePatternFieldPeriod firstAndLastMonth = null;
	
	public static byte dayOfWeekNameToNumber(String dayOfWeek, boolean sundayIsLast)
	{
		if (dayOfWeek == null)
			throw new NullPointerException("dayOfWeek must not be null!");

		for (byte wd = (byte)(sundayIsLast ? 1 : 0); wd < DAYOFWEEK_NAMES.length; ++wd) {
			if (DAYOFWEEK_NAMES[wd].equals(dayOfWeek))
				return wd;
		}
		throw new IllegalArgumentException("dayOfWeek \""+dayOfWeek+"\" is unknown!");
	}

	public static String dayOfWeekNumberToName(int dayOfWeekNumber)
	{
		if (dayOfWeekNumber < 0 || dayOfWeekNumber > DAYOFWEEK_NAMES.length)
			throw new IllegalArgumentException("dayOfWeekNumber must be >=0 and <="+DAYOFWEEK_NAMES.length+"!!!");

		return DAYOFWEEK_NAMES[dayOfWeekNumber];
	}

	public static String convertDayOfWeekPeriodString(String periodString, byte conversionDirection)
		throws TimePatternFormatException
	{
		if (conversionDirection != CONVERT_DAYOFWEEK_NAME_TO_NUMBER &&
				conversionDirection != CONVERT_DAYOFWEEK_NUMBER_TO_NAME)
			throw new IllegalArgumentException("conversionDirection is invalid! Use one of these constants: conversionDirection != CONVERT_DAYOFWEEK_NAME_TO_NUMBER, CONVERT_DAYOFWEEK_NUMBER_TO_NAME");

		if (periodString == null)
			return null;

		StringBuffer sb = new StringBuffer();
		StringTokenizer tok = new StringTokenizer(periodString, " ,-*/", true);
		char lastSymbol = ',';
		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			if (token.equals(" ")) ;
			else if (token.equals(",")) { sb.append(token); lastSymbol = ','; }
			else if (token.equals("-")) { sb.append(token); lastSymbol = '-'; }
			else if (token.equals("*")) { sb.append(token); lastSymbol = '*'; }
			else if (token.equals("/")) { sb.append(token); lastSymbol = '/'; }
			else {
				if (token.matches("(\\d)*")) {
					if (conversionDirection == CONVERT_DAYOFWEEK_NUMBER_TO_NAME && lastSymbol != '/') {
						int weekDayNumber = Integer.parseInt(token);
						sb.append(dayOfWeekNumberToName(weekDayNumber));
					}
					else
						sb.append(token);
				} // if token is a number
				else { // token is a weekday name or is invalid
					token = token.toLowerCase();
					byte dayOfWeekNumber = dayOfWeekNameToNumber(token, lastSymbol == '-');
					if (conversionDirection == CONVERT_DAYOFWEEK_NAME_TO_NUMBER)
						sb.append(dayOfWeekNumber);
					else
						sb.append(token);
				}
			}
				
		}
		return sb.toString();
	}

	/**
	 * Create a new TimePattern.
	 * <p>
	 * <strong>Warning!</strong> This constructor cannot be used to create a functional instance of TimePattern! It exists
	 *		only to allow JDO implementations or similar that require such a constructor and solve the problems by
	 *		magic (i.e. enhancement, reflection etc.).
	 */
	protected TimePattern() { }

	/**
	 * Create a new TimePattern.
	 * @param timePatternSet The parent time pattern set
	 */
	public TimePattern(TimePatternSet timePatternSet)
	{
		this.setTimePatternSet(timePatternSet);
	}

	/**
	 * Create a new TimePattern.
	 * @param timePatternSet The parent time pattern set
	 * @param year The year field
	 * @param month The month field
	 * @param day The day field
	 * @param dayOfWeek The day-of-week field
	 * @param hour The hour field
	 * @param minute The minute field
	 * @throws TimePatternFormatException If the time pattern is illegal
	 */
	public TimePattern(TimePatternSet timePatternSet, String year, String month, String day, String dayOfWeek, String hour, String minute)
		throws TimePatternFormatException
	{
		this.setTimePatternSet(timePatternSet);
		this.setYear(year);
		this.setMonth(month);
		this.setDay(day);
		this.setDayOfWeek(dayOfWeek);
		this.setHour(hour);
		this.setMinute(minute);
	}

	/**
	 * TODO: docmument this method! Waht does it do? what is it used for?
	 * @return Instances of {@link TimePatternFieldPeriod}.
	 */
	protected static List<TimePatternFieldPeriod> getSkipPeriods (int minVal, int maxVal, int step, boolean divisionMode)
	{
		LinkedList<TimePatternFieldPeriod> periods = new LinkedList<TimePatternFieldPeriod>();
		if (divisionMode) {
			for (int v = minVal; v <= maxVal; v++) {
				if (v % step == 0)
					periods.add(new TimePatternFieldPeriod(v, v));
			}
		}
		else {
			int v = minVal;
			while (v <= maxVal) {
				periods.add(new TimePatternFieldPeriod(v, v));
				v += step;
			}
		}
		return periods;
	}
	
	/**
	 * This method parses the requested field and returns its values as periods.
	 * The calculated time periods are cached until the time pattern changes.
	 *
	 * @param field The ID of the field you want to get. Use one of the following
	 * 		constants: {@link #YEAR}, {@link #MONTH}, {@link #DAY},
	 * 		{@link #DAY_OF_WEEK}, {@link #HOUR}, {@link #MINUTE}
	 *
	 * @return A List of {@link TimePatternFieldPeriod} objects.
	 */
	public List<TimePatternFieldPeriod> getTimePeriods(byte field)
	{
		try {
			List<TimePatternFieldPeriod> cachedTimePeriods = getCachedTimePeriods(field);
			if(cachedTimePeriods != null)
				return cachedTimePeriods;
	
			String value;
			int minVal;
			int maxVal;
			switch (field) {
				case YEAR:
					value = getYear();
					minVal = YEAR_MIN;
					maxVal = YEAR_MAX;
				break;
				case MONTH:
					value = getMonth();
					minVal = MONTH_MIN;
					maxVal = MONTH_MAX;
				break;
				case DAY:
					value = getDay();
					minVal = DAY_MIN;
					maxVal = DAY_MAX;
				break;
				case DAY_OF_WEEK:
					value = convertDayOfWeekPeriodString(getDayOfWeek(), CONVERT_DAYOFWEEK_NAME_TO_NUMBER);
					minVal = DAY_OF_WEEK_MIN;
					maxVal = DAY_OF_WEEK_MAX;
				break;
				case HOUR:
					value = getHour();
					minVal = HOUR_MIN;
					maxVal = HOUR_MAX;
				break;
				case MINUTE:
					value = getMinute();
					minVal = MINUTE_MIN;
					maxVal = MINUTE_MAX;
				break;
				default:
					throw new IllegalArgumentException("Parameter field contains an invalid value!");
			} // switch (field) {
	
			List<TimePatternFieldPeriod> timePeriods = calculateTimePeriods(value, minVal, maxVal);
			setCachedTimePeriods(field, timePeriods);
			return timePeriods;
			
		} catch (TimePatternFormatException x) {
			throw new RuntimeException("How does it happen that this instance of TimePattern contains invalid data?!", x);
		}
	}

	/**
	 * Get the cached time periods for a given field.
	 * @param field The field - {@link #YEAR} to {@link #MINUTE}.
	 * @return The cached time period or <code>null</code> if
	 * 		no cached time period exists for the given field.
	 */
	@SuppressWarnings("unchecked")
	protected List<TimePatternFieldPeriod> getCachedTimePeriods(byte field)
	{
		if(cachedPeriods == null)
			return null;
		return cachedPeriods[field];
	}
	
	/**
	 * Store the given time periods in the time period cache.
	 * @param field The field - {@link #YEAR} to {@link #MINUTE}.
	 * @param timePeriods The time periods to cache
	 */
	protected void setCachedTimePeriods(byte field, List<TimePatternFieldPeriod> timePeriods)
	{
		if (cachedPeriods == null)
			cachedPeriods = new List[6];
		cachedPeriods[field] = timePeriods;
	}

	/**
	 * Calculate the time periods in which the given time pattern value is active.
	 * @param value The time pattern field value
	 * @param minVal The minimum allowed value for the field
	 * @param maxVal The maximum allowed value for the field
	 * @return Instances of {@link TimePatternFieldPeriod}.
	 * @throws TimePatternFormatException If the time pattern is illegal for this field
	 */
	private static List<TimePatternFieldPeriod> calculateTimePeriods(String value, int minVal, int maxVal)
		throws TimePatternFormatException
	{
		if (value == null)
			value = "";
		else
			value = value.replaceAll(" ", "");

		ArrayList<TimePatternFieldPeriod> periods = new ArrayList<TimePatternFieldPeriod>();

		StringTokenizer st = new StringTokenizer(value, ",", false);

		if (!st.hasMoreTokens())
			periods.add(new TimePatternFieldPeriod(minVal, maxVal));

		while (st.hasMoreTokens()) {
			String currPeriod = st.nextToken();
			if (currPeriod.equals("*"))
				periods.add(new TimePatternFieldPeriod(minVal, maxVal));
			else if (currPeriod.startsWith("*/")) {
				int step = Integer.parseInt(currPeriod.substring(currPeriod.indexOf('/')+1));
				periods.addAll(getSkipPeriods(minVal, maxVal, step, false));
			}
			else if (currPeriod.startsWith("/")) {
				int div = Integer.parseInt(currPeriod.substring(currPeriod.indexOf('/')+1));
				periods.addAll(getSkipPeriods(minVal, maxVal, div, true));
			}
			else {
				StringTokenizer pt = new StringTokenizer(currPeriod, "-/", true);
				if (!pt.hasMoreTokens())
					throw new TimePatternFormatException("Empty value in list!");

				String fromStr = pt.nextToken();

				int from;
				try {
					from = Integer.parseInt(fromStr);
				} catch (NumberFormatException x) {
					throw new TimePatternFormatException("from value \""+fromStr+"\" (in \""+currPeriod+"\") (before \"-\") is not a valid integer!", x);
				}
				int to = -1;
				String toStr = null;
				int step = -1;
				String stepStr = null;

				if (pt.hasMoreTokens()) {
					String sep = pt.nextToken();
					if ("-".equals(sep)) {
						if (pt.hasMoreTokens())
							toStr = pt.nextToken();
						else
							throw new TimePatternFormatException("to value is missing after \"-\" (in \""+currPeriod+"\")!");
					}
					else if ("/".equals(sep)) {
						if (pt.hasMoreTokens())
							stepStr = pt.nextToken();
						else
							throw new TimePatternFormatException("step value is missing after \"/\" (in \""+currPeriod+"\")!");
					}
					else
						throw new TimePatternFormatException("Invalid token after from value (in \""+currPeriod+"\")! Expected '-' or '/'!");
				}

				if (stepStr == null && pt.hasMoreTokens()) {
					String sep = pt.nextToken();

					if ("/".equals(sep)) {
						if (pt.hasMoreTokens())
							stepStr = pt.nextToken();
						else
							throw new TimePatternFormatException("step value is missing after \"/\") (in \""+currPeriod+"\")!");
					}
					else
						throw new TimePatternFormatException("Invalid token after to value (in \""+currPeriod+"\")! Expected '/'!");
				}

				if (toStr == null)
					to = from;
				else {
					try {
						to = Integer.parseInt(toStr);
					} catch (NumberFormatException x) {
						throw new TimePatternFormatException("to value \""+toStr+"\" (in \""+currPeriod+"\") is not a valid integer!", x);
					}
				}

				if (stepStr != null) {
					try {
						step = Integer.parseInt(stepStr);
					} catch (NumberFormatException x) {
						throw new TimePatternFormatException("step value \"" + stepStr + "\" (after \"/\") is not a valid integer!", x);
					}
	
					if (step < 1)
						throw new TimePatternFormatException("step value \"" + stepStr + "\" (after \"/\") is not >= 1!");
				}

				if (pt.hasMoreTokens())
					throw new TimePatternFormatException("Too many tokens in period definition (in \""+currPeriod+"\")! Only one allowed: \"from-to/step\" where to and step are optional (together with their symbols '-' or '/')!");

				if (from < minVal)
					throw new TimePatternFormatException("from < minVal: " + from + " < " + minVal);

				if (from > maxVal)
					throw new TimePatternFormatException("from > maxVal: " + from + " > " + maxVal);

				if (to < minVal)
					throw new TimePatternFormatException("to < minVal: " + to + " < " + minVal);

				if (to > maxVal)
					throw new TimePatternFormatException("to > maxVal: " + to + " > " + maxVal);

				if (step > 0)
					periods.addAll(getSkipPeriods(from, to, step, false));
				else
					periods.add(new TimePatternFieldPeriod(from, to));
			}
		} // while (st.hasMoreTokens()) {

		return periods;
	}

	/**
	 * Check whether a given time stamp (in milliseconds) is within this pattern.
	 * <p>
	 * The value of maxDepth defines down to wich period the check should go. Use a value
	 * between {@link #YEAR} and {@link #MINUTE} inclusive.
	 * <p>
	 * For example to check weather the
	 * time pattern matches for a certain day use matches(yourTimeStamp, TimePattern.DAY); In
	 * this case, it would not check whether the given day-of-week, hour or minute is within the pattern -
	 * only year, month and day would be checked.
	 *
	 * @param timeStamp The timeStamp you want to check.
	 * @param maxDepth The maximum depth that should be taken into account.
	 *
	 * @return <code>true</code>, if the given time is within the pattern held by this object -
	 * 		<code>false</code> otherwise.
	 */
	public boolean matches(long timeStamp, byte maxDepth)
	{
		return matches(timeStamp, YEAR, maxDepth);
	}
	
	/**
	 * Check whether a given time stamp (in milliseconds) is within this pattern.
	 * <p>
	 * The value of minDepth defines with wich period to begin the check with, the
	 * value of maxDepth defines down to wich period the check should go. Use values
	 * between {@link #YEAR} and {@link #MINUTE} inclusive where minDepth &lt;= maxDepth.
	 * <p>
	 * For example to check weather the
	 * time pattern matches for a certain day only, use matches(yourTimeStamp, TimePattern.DAY, TimePattern.DAY); In
	 * this case, it would only check wheather the given day-of-month is within the
	 * time pattern. All other fields are not takein into account.
	 *
	 * @param timeStamp The timeStamp you want to check.
	 * @param minDepth The minimum depth that should be taken into account.
	 * @param maxDepth The maximum depth that should be taken into account.
	 *
	 * @return <code>true</code>, if the given time is within the pattern held by this object -
	 * 		<code>false</code> otherwise.
	 */
	public boolean matches(long timeStamp, byte minDepth, byte maxDepth)
	{
		if(maxDepth < minDepth)
			throw new IllegalArgumentException("maxDepth < minDepth");
		if(minDepth < YEAR || minDepth > MINUTE)
			throw new IllegalArgumentException("Illegal value for minDepth");
		if(maxDepth < YEAR || maxDepth > MINUTE)
			throw new IllegalArgumentException("Illegal value for maxDepth");
		
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(timeStamp);

		int[] values = new int[MINUTE + 1];

		values[YEAR] = cal.get(Calendar.YEAR);

		if(minDepth <= YEAR && YEAR <= maxDepth) {
			TimePatternFieldPeriod firstAndLastYear = getFirstAndLastYear();
			if (values[YEAR] < firstAndLastYear.getFrom() ||
					values[YEAR] > firstAndLastYear.getTo())
				return false;
		}

		values[MONTH] = cal.get(Calendar.MONTH) + 1;

		if(minDepth <= MONTH && MONTH <= maxDepth) {
			TimePatternFieldPeriod firstAndLastMonth = getFirstAndLastMonth();
			if (values[MONTH] < firstAndLastMonth.getFrom() ||
					values[MONTH] > firstAndLastMonth.getTo())
				return false;
		}

		values[DAY] = cal.get(Calendar.DAY_OF_MONTH);
		values[DAY_OF_WEEK] = cal.get(Calendar.DAY_OF_WEEK) - 1;
		values[HOUR] = cal.get(Calendar.HOUR_OF_DAY);
		values[MINUTE] = cal.get(Calendar.MINUTE);

		for (byte field = minDepth; field <= maxDepth; field++) {
			List<TimePatternFieldPeriod> periods = getTimePeriods(field);

			boolean match = false;
			for (TimePatternFieldPeriod period : periods) {
				if (period.getFrom() <= values[field] &&
						values[field] <= period.getTo()) {
					match = true;
					break;
				}

				// not nice, but should work: sunday can be both: 0 or 7
				if (field == DAY_OF_WEEK && values[DAY_OF_WEEK] == 0) {
					if (period.getFrom() <= 7 && 7 <= period.getTo()) {
						match = true;
						break;
					}
				}
			} // for (int i = 0; i < periods.length; i++) {

			if (!match)
				return false;
		} // for (byte field = 0; field <= MINUTE; field++) {

		return true;
	}
	
	/**
	 * Check whether a given time stamp (in milliseconds) is within this pattern.
	 * @param timeStamp The timeStamp you want to check.
	 * @return <code>true</code>, if the given time is within the pattern held by this object -
	 * 		<code>false</code> otherwise.
	 */
	public boolean matches(long timeStamp)
	{
		return matches(timeStamp, MINUTE);
	}

	/**
	 * Get the parent time pattern set.
	 * @return Returns the parent time pattern set.
	 */
	public abstract TimePatternSet getTimePatternSet();
	
	/**
	 * Set the parent time pattern set.
	 * @param timePatternSet The parent time pattern set to set
	 */
	protected abstract void setTimePatternSet(TimePatternSet timePatternSet);

	/**
	 * Get the year pattern field.
	 * @return The year pattern field
	 */
	public abstract String getYear();
	
	/**
	 * Set the year pattern field. The given pattern
	 * field value is always formatted correctly, the
	 * value is checked before this method gets called.
	 * @param year The year pattern field to set.
	 */
	protected abstract void _setYear(String year);
	
	/**
	 * Set the year pattern field.
	 * @param year The year pattern field to set.
	 * @throws TimePatternFormatException if the pattern field is illegal
	 */
	public void setYear(String year)
		throws TimePatternFormatException
	{
		// just for sanity checking:
		calculateTimePeriods(year, YEAR_MIN, YEAR_MAX);
		_setYear(year);
		changed();
	}

	/**
	 * Get the month pattern field.
	 * @return The month pattern field
	 */
	public abstract String getMonth();

	/**
	 * Set the month pattern field. The given pattern
	 * field value is always formatted correctly, the
	 * value is checked for sanity before this method gets called.
	 * @param month The month pattern field to set.
	 */
	protected abstract void _setMonth(String month);

	/**
	 * Set the month pattern field.
	 * @param month The month pattern field to set.
	 * @throws TimePatternFormatException if the pattern field is illegal
	 */
	public void setMonth(String month)
		throws TimePatternFormatException
	{
		// just for sanity checking:
		calculateTimePeriods(month, MONTH_MIN, MONTH_MAX);
		_setMonth(month);
		changed();
	}

	/**
	 * Get the day pattern field.
	 * @return The day pattern field
	 */
	public abstract String getDay();
	
	/**
	 * Set the day pattern field. The given pattern
	 * field value is always formatted correctly, the
	 * value is checked for sanity before this method gets called.
	 * @param day The day pattern field to set.
	 */
	protected abstract void _setDay(String day);
	
	/**
	 * Set the day pattern field.
	 * @param day The day pattern field to set.
	 * @throws TimePatternFormatException if the pattern field is illegal
	 */
	public void setDay(String day)
		throws TimePatternFormatException
	{
		calculateTimePeriods(day, DAY_MIN, DAY_MAX);
		_setDay(day);
		changed();
	}
	
	/**
	 * Get the day-of-week pattern field.
	 * @return The day-of-week pattern field
	 */
	public abstract String getDayOfWeek();
	
	/**
	 * Set the day-of-week pattern field. The given pattern
	 * field value is always formatted correctly, the
	 * value is checked for sanity before this method gets called.
	 * @param dayOfWeek The day-of-week pattern field to set.
	 */
	protected abstract void _setDayOfWeek(String dayOfWeek);
	
	/**
	 * Set the day-of-week pattern field.
	 * @param dayOfWeek The day-of-week pattern field to set.
	 * @throws TimePatternFormatException if the pattern field is illegal
	 */
	public void setDayOfWeek(String dayOfWeek)
		throws TimePatternFormatException
	{
		String dayOfWeek_numbered = convertDayOfWeekPeriodString(dayOfWeek, CONVERT_DAYOFWEEK_NAME_TO_NUMBER);
		calculateTimePeriods(dayOfWeek_numbered, DAY_OF_WEEK_MIN, DAY_OF_WEEK_MAX);
		_setDayOfWeek(dayOfWeek);
		changed();
	}
	
	/**
	 * Get the hour pattern field.
	 * @return The hour pattern field
	 */
	public abstract String getHour();

	/**
	 * Set the hour pattern field. The given pattern
	 * field value is always formatted correctly, the
	 * value is checked for for sanity before this method gets called.
	 * @param hour The hour pattern field to set.
	 */
	protected abstract void _setHour(String hour);
	
	/**
	 * Set the hour pattern field.
	 * @param hour The hour pattern field to set.
	 * @throws TimePatternFormatException if the pattern field is illegal
	 */
	public void setHour(String hour)
		throws TimePatternFormatException
	{
		calculateTimePeriods(hour, HOUR_MIN, HOUR_MAX);
		_setHour(hour);
		changed();
	}

	/**
	 * Get the minute pattern field.
	 * @return The minute pattern field
	 */
	public abstract String getMinute();

	/**
	 * Set the minute pattern field. The given pattern
	 * field value is always formatted correctly, the
	 * value is checked for for sanity before this method gets called.
	 * @param minute The minute pattern field to set.
	 */
	protected abstract void _setMinute(String minute);

	/**
	 * Set the minute pattern field.
	 * @param minute The minute pattern field to set.
	 * @throws TimePatternFormatException if the pattern field is illegal
	 */
	public void setMinute(String minute)
		throws TimePatternFormatException
	{
		calculateTimePeriods(minute, MINUTE_MIN, MINUTE_MAX);
		_setMinute(minute);
		changed();
	}

	/**
	 * This method is called whenever the time pattern changes.
	 * All cache fields will be reset.
	 */
	protected void changed()
	{
		cachedPeriods = null;
		firstAndLastYear = null;
		firstAndLastMonth = null;
		thisString = null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		if (thisString == null) {
			StringBuffer sb = new StringBuffer(this.getClass().getName());
			sb.append("[\"");
			sb.append(getYear());
			sb.append("\",\"");
			sb.append(getMonth());
			sb.append("\",\"");
			sb.append(getDay());
			sb.append("\",\"");
			sb.append(getDayOfWeek());
			sb.append("\",\"");
			sb.append(getHour());
			sb.append("\",\"");
			sb.append(getMinute());
			sb.append("\"]");
			thisString = sb.toString();
		}
		return thisString;
	}

	/**
	 * Get the time period this time patterns month field
	 * is valid for.
	 * @return The time period this time patterns month field
	 * 		is valid for or a time pattern {@link #MONTH_MIN} -
	 * 		{@link #MONTH_MAX} if all months are valid.
	 */
	protected TimePatternFieldPeriod getFirstAndLastMonth()
	{
		if (firstAndLastMonth == null) {
			int monthMin = Integer.MAX_VALUE;
			int monthMax = Integer.MIN_VALUE;

			for (Object element : getTimePeriods(TimePattern.MONTH)) {
				TimePatternFieldPeriod period = (TimePatternFieldPeriod) element;
				if (period.getFrom() != -1) {
					monthMin = Math.min(monthMin, period.getFrom());
					monthMax = Math.max(monthMax, period.getTo());
				}
			}

			if (monthMin < MONTH_MIN || monthMin > MONTH_MAX)
				monthMin = MONTH_MIN;

			if (monthMax > MONTH_MAX || monthMax < MONTH_MIN)
				monthMax = MONTH_MAX;

			firstAndLastMonth = new TimePatternFieldPeriod(monthMin, monthMax);
		}

		return firstAndLastMonth;
	}

	/**
	 * Get the time period this time patterns year field
	 * is valid for.
	 * @return The time period this time patterns year field
	 * 		is valid for or a time pattern {@link #YEAR_MIN} -
	 * 		{@link #YEAR_MAX} if all years are valid.
	 */
	protected TimePatternFieldPeriod getFirstAndLastYear()
	{
		if (firstAndLastYear == null) {
			int yearMin = Integer.MAX_VALUE;
			int yearMax = Integer.MIN_VALUE;

			for (Object element : getTimePeriods(TimePattern.YEAR)) {
				TimePatternFieldPeriod period = (TimePatternFieldPeriod) element;
				if (period.getFrom() != -1) {
					yearMin = Math.min(yearMin, period.getFrom());
					yearMax = Math.max(yearMax, period.getTo());
				}
			}

			if (yearMin < YEAR_MIN || yearMin > YEAR_MAX)
				yearMin = YEAR_MIN;

			if (yearMax > YEAR_MAX || yearMax < YEAR_MIN)
				yearMax = YEAR_MAX;

			firstAndLastYear = new TimePatternFieldPeriod(yearMin, yearMax);
		}

		return firstAndLastYear;
	}

	
	/**
	 * Check for equality of a field.
	 * @param one A field
	 * @param another Another field
	 * @return <code>true</code> if the fields are equal.
	 */
	private static boolean fieldEquals(Object one, Object another)
	{
		return one == another || (one != null && one.equals(another));
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null || !(obj instanceof TimePattern))
			return false;
		TimePattern other = (TimePattern)obj;
		return
				fieldEquals(getYear(), other.getYear()) &&
				fieldEquals(getMonth(), other.getMonth()) &&
				fieldEquals(getDay(), other.getDay()) &&
				fieldEquals(getDayOfWeek(), other.getDayOfWeek()) &&
				fieldEquals(getHour(), other.getHour()) &&
				fieldEquals(getMinute(), other.getMinute());
	}
	
	/**
	 * Get the hash code for a field.
	 * @param field The field to hash
	 * @return the hash code of a field or <code>null</code> if the field is empty
	 */
	private static int fieldHashCode(Object field)
	{
		return field == null ? 0 : field.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return
				fieldHashCode(getYear()) ^
				fieldHashCode(getMonth()) ^
				fieldHashCode(getDay()) ^
				fieldHashCode(getDayOfWeek()) ^
				fieldHashCode(getHour()) ^
				fieldHashCode(getMinute());
	}
}
