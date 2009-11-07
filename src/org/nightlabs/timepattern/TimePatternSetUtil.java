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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * This class is intended for internal usage within this package only.
 * It provides methods for next execution time and time period calculations.
 *
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
class TimePatternSetUtil
{
	/**
	 * The Log4J logger used by this class.
	 */
	private static Logger logger = Logger.getLogger(TimePatternSetUtil.class);

	protected static final int[] CALFIELDS = {
		Calendar.MINUTE,
		Calendar.HOUR_OF_DAY,
		Calendar.DAY_OF_MONTH,
		Calendar.MONTH,
		Calendar.YEAR
	};

	public static int S_MIN = 0;
	public static int S_HOUR = 1;
	public static int S_DAY = 2;
	public static int S_MONTH = 3;
	public static int S_YEAR = 4;

	protected static final int CPCOMP_MODE_UP = 1;
	protected static final int CPCOMP_MODE_DOWN = -1;

	/**
	 * Get the next match time for a time pattern set.
	 *
	 * @param tps The time pattern set
	 * @param lastExecDT The time of last execution or <code>null</code> if
	 * 		no execution happened before.
	 * @return The next time when the time pattern set matches
	 * 		or <code>null</code> if no such time can be found within the time
	 * 		until {@link TimePattern#YEAR_MAX}.
	 *
	 * @see #getNextMatchTime(TimePatternSet, Date)
	 */
	protected static Long getNextMatchTime(TimePatternSet tps, Long lastExecTimestamp)
	{
		long next;
		if (lastExecTimestamp == null)
			next = System.currentTimeMillis();
		else
			next = lastExecTimestamp.longValue() + 60000;

		next = ((long) Math.floor(next / 60000L)) * 60000L; // a Task cannot be called more than once a minute - see TimePatternSet

		for(byte field=TimePattern.YEAR; field<=TimePattern.MINUTE; field++) {
			if(!tps.matches(next, field, field)) {
				next = stepToNext(next, field);
				// stay in same field
				field--;
			} else if(field > TimePattern.YEAR) {
				// check if upper fields still match. If not, start from the first field again (YEAR)
				byte previousField = (byte)(field-1);
				if(!tps.matches(next, previousField)) {
					field = TimePattern.YEAR;
					next = stepToNext(next, previousField);
				}
			}
			if (isOverMaximum(next))
				return null;
		}
		return Long.valueOf(next);
	}

	/**
	 * Cached value for the first moment in time when
	 * the year is &gt; {@link TimePattern#YEAR_MAX}.
	 */
	private static long overMaximumValue = -1;

	/**
	 * Check whther the time value is over the maximum defined by
	 * link TimePattern#YEAR_MAX}.
	 * @param timestamp The timestamp to check
	 * @return <code>true</code> if the value is over the maximum -
	 * 		<code>false</code> otherwise.
	 */
	private static boolean isOverMaximum(long timestamp)
	{
		if(overMaximumValue < 0) {
			GregorianCalendar cal = new GregorianCalendar(TimePattern.YEAR_MAX+1, 0, 1);
			overMaximumValue = cal.getTimeInMillis();
		}
		return timestamp >= overMaximumValue;
	}

	/**
	 * Step to the next timestamp value for an increment of the
	 * given {@link TimePattern} field.
	 * @param timestamp The original timestamp
	 * @param field The {@link TimePattern} field ({@link TimePattern#YEAR} - {@link TimePattern#MINUTE}).
	 * @return The new timestamp (the first minute within the next value
	 * 		of the given {@link TimePattern} field.
	 */
	private static long stepToNext(long timestamp, byte field)
	{
		// reduce creation of unneeded calendars by checking for minute here:
		if(field == TimePattern.MINUTE)
			return timestamp + 60000;

		// all other fields need the calendar:
		GregorianCalendar oldCalendar = new GregorianCalendar();
		oldCalendar.setTimeInMillis(timestamp);
		switch(field) {
		case TimePattern.YEAR:
			return new GregorianCalendar(oldCalendar.get(Calendar.YEAR)+1, 0, 1).getTimeInMillis();
		case TimePattern.MONTH:
			return new GregorianCalendar(oldCalendar.get(Calendar.YEAR), oldCalendar.get(Calendar.MONTH)+1, 1).getTimeInMillis();
		case TimePattern.DAY:
		case TimePattern.DAY_OF_WEEK:
			return new GregorianCalendar(oldCalendar.get(Calendar.YEAR), oldCalendar.get(Calendar.MONTH), oldCalendar.get(Calendar.DAY_OF_MONTH)+1).getTimeInMillis();
		case TimePattern.HOUR:
			return new GregorianCalendar(oldCalendar.get(Calendar.YEAR), oldCalendar.get(Calendar.MONTH), oldCalendar.get(Calendar.DAY_OF_MONTH), oldCalendar.get(Calendar.HOUR_OF_DAY)+1, 0, 0).getTimeInMillis();
		default:
			throw new IllegalArgumentException("Illegal time pattern field: "+field);
		}
	}













	protected static void createTimePatternFromFieldPeriods(TimePatternSet tps, TimePatternFieldPeriod[] fieldPeriods)
	{
		String[] fieldPeriodsStr = new String[fieldPeriods.length];

		for (int i = 0; i < fieldPeriods.length; ++i) {
			int from = fieldPeriods[i].getFrom();
			if (from < 0)
				fieldPeriodsStr[i] = "*";
			else {
				int to = fieldPeriods[i].getTo();
				if (from == to)
					fieldPeriodsStr[i] = Integer.toString(from);
				else {
					StringBuffer sb = new StringBuffer(Integer.toString(from));
					if (from != to)
						sb.append('-').append(Integer.toString(to));
					fieldPeriodsStr[i] = sb.toString();
				}
			}
		}

		try {
			TimePattern timePattern = tps.createTimePattern(
					fieldPeriodsStr[S_YEAR],
					fieldPeriodsStr[S_MONTH],
					fieldPeriodsStr[S_DAY],
					"*",
					fieldPeriodsStr[S_HOUR],
					fieldPeriodsStr[S_MIN]);
			if(logger.isDebugEnabled())
				logger.debug("Created time pattern: "+timePattern);
		} catch (TimePatternFormatException e) {
			throw new RuntimeException("Something within the method \"addCondensedPeriod(...)\" went really wrong!", e);
		}
	}

	/**
	 * This method adds as many TimePatterns as there are necessary to represent the given
	 * CondensedPeriod.
	 *
	 * @param cp The CondensedPeriod to add.
	 */
	protected static void addCondensedPeriod(TimePatternSet tps, CondensedTimePatternFieldPeriod cp)
	{
		if (cp.getFromDT().compareTo(cp.getToDT()) > 0)
			throw new IllegalArgumentException("cp.fromDT > cp.toDT!!!");

		TimePatternCalendar calFromDT = new TimePatternCalendar();
		calFromDT.setTime(cp.getFromDT());
		TimePatternCalendar calToDT = new TimePatternCalendar();
		calToDT.setTime(cp.getToDT());
		TimePatternCalendar calWork = new TimePatternCalendar();

		if (calFromDT.get(S_YEAR) > TimePatternSet.YEAR_MAX)
			throw new IllegalArgumentException("cp.fromDT.year > "+TimePatternSet.YEAR_MAX+"!");

		if (calFromDT.get(S_YEAR) < TimePatternSet.YEAR_MIN)
			throw new IllegalArgumentException("cp.fromDT.year < "+TimePatternSet.YEAR_MIN+"!");

		if (calToDT.get(S_YEAR) > TimePatternSet.YEAR_MAX)
			throw new IllegalArgumentException("cp.toDT.year > "+TimePatternSet.YEAR_MAX+"!");

		if (calToDT.get(S_YEAR) < TimePatternSet.YEAR_MIN)
			throw new IllegalArgumentException("cp.toDT.year < "+TimePatternSet.YEAR_MIN+"!");


		List<TimePatternFieldPeriod[]> periodsModeUp = new ArrayList<TimePatternFieldPeriod[]>();
		List<TimePatternFieldPeriod[]> periodsModeDown = new ArrayList<TimePatternFieldPeriod[]>();
		boolean isFirstFieldIdx = true;
		int diffFieldIdx = 0; boolean exitMode = false;
		int mode = CPCOMP_MODE_UP;
		int highestFieldIdxModeUp = 0;
		boolean addPeriod = true;
//		boolean lastRowHadCompleteField = false;
		while (diffFieldIdx <= CALFIELDS.length) {
//			lastRowHadCompleteField = false;
			addPeriod = true;
			TimePatternCalendar calSource;
			TimePatternCalendar calDest;

			if (mode == CPCOMP_MODE_UP) {
				calSource = calFromDT;
				calDest = calToDT;
			}
			else {
				calSource = calToDT;
				calDest = calFromDT;
			}

			calWork.setTimeInMillis(calSource.getTimeInMillis());

			TimePatternFieldPeriod[] fieldPeriods = new TimePatternFieldPeriod[CALFIELDS.length];
			for (int fieldIdx = 0; fieldIdx < CALFIELDS.length; ++fieldIdx) {
				if (fieldIdx < diffFieldIdx) {
					fieldPeriods[fieldIdx] = new TimePatternFieldPeriod(-1, -1); // becomes "*"
					calWork.set(fieldIdx, calDest.get(fieldIdx));
				}
				else if (fieldIdx > diffFieldIdx) {
					int i = calWork.get(fieldIdx);
					fieldPeriods[fieldIdx] = new TimePatternFieldPeriod(i, i);
				}
				else {
					int from = calWork.get(fieldIdx);
					boolean currFieldIsComplete = false;

					if (isFirstFieldIdx) {
//						if (!lastRowHadCompleteField)
						isFirstFieldIdx = false;
					}
					else
						from += mode;

					if (from == (mode == CPCOMP_MODE_UP ? calWork.getActualMinimum(fieldIdx) : calWork.getActualMaximum(fieldIdx)))
					{
						calWork.set(fieldIdx, calSource.get(fieldIdx));
//						if (mode == CPCOMP_MODE_UP)
//							addPeriod = false;
//						lastRowHadCompleteField = true;
						currFieldIsComplete = true;
					}

					int to = mode == CPCOMP_MODE_UP ? calWork.getActualMaximum(fieldIdx) : calWork.getActualMinimum(fieldIdx);
					if (mode * from > mode * to) {
						addPeriod = false;
						fieldPeriods = null;
						break;
					}
					calWork.set(fieldIdx, to);
					if (mode * calWork.getTimeInMillis() >= mode * calDest.getTimeInMillis())
					{
						to = calDest.get(fieldIdx) - mode;
						exitMode = true;
					}

					if (mode * from > mode * to) {
						addPeriod = false;
						fieldPeriods = null;
						break;
					}

					if (mode == CPCOMP_MODE_DOWN) {
						int i = from;
						from = to;
						to = i;
					}
					if (currFieldIsComplete) {
						currFieldIsComplete = false;
						fieldPeriods[fieldIdx] = new TimePatternFieldPeriod(-1, -1); // becomes "*"
					}
					else {
						fieldPeriods[fieldIdx] = new TimePatternFieldPeriod(from, to);
					}
				}
			}

			if (addPeriod && fieldPeriods != null) {
				// DEBUG
				if(logger.isDebugEnabled()) {
					StringBuffer sb = new StringBuffer();
					for (TimePatternFieldPeriod element : fieldPeriods) {
						sb.append(element.toString());
						sb.append(' ');
					}
					logger.debug(sb.toString());
				}
				// END DEBUG

				if (mode == CPCOMP_MODE_UP)
					periodsModeUp.add(0, fieldPeriods);
				else
					periodsModeDown.add(0, fieldPeriods);
			}

			if (mode == CPCOMP_MODE_UP && exitMode) {
				exitMode = false;
				mode = CPCOMP_MODE_DOWN;
				highestFieldIdxModeUp = diffFieldIdx;
				diffFieldIdx = -1;
				isFirstFieldIdx = true;
			}

			++diffFieldIdx;
			if (mode == CPCOMP_MODE_DOWN && diffFieldIdx >= highestFieldIdxModeUp)
				break;

		} // while (diffFieldIdx < CALENDARFIELDS.length) {

		for (int m = 0; m < 2; ++m) {
			List<TimePatternFieldPeriod[]> srcPeriods = m == 0 ? periodsModeUp : periodsModeDown;

			for (int periodIdx = 0; periodIdx < srcPeriods.size(); ++periodIdx) {
				TimePatternFieldPeriod[] fieldPeriods0 = srcPeriods.get(periodIdx);
				if (periodIdx + 1 < srcPeriods.size()) {
					TimePatternFieldPeriod[] fieldPeriods1 = srcPeriods.get(periodIdx + 1);
					DoubleWildCardCount wcc = countWildCards(fieldPeriods0, fieldPeriods1);
					if (wcc.wildCardCount0 == wcc.wildCardCount1) {
						if (fieldPeriods0[wcc.wildCardCount0].getTo() + 1 == fieldPeriods1[wcc.wildCardCount1].getFrom()) {
							fieldPeriods0[wcc.wildCardCount0].setTo(fieldPeriods1[wcc.wildCardCount1].getTo());
							makeWildCards(fieldPeriods0);
							srcPeriods.remove(periodIdx + 1);
						}
						if (fieldPeriods0[wcc.wildCardCount0].getFrom() == fieldPeriods1[wcc.wildCardCount1].getTo() + 1) {
							fieldPeriods0[wcc.wildCardCount0].setFrom(fieldPeriods1[wcc.wildCardCount1].getFrom());
							makeWildCards(fieldPeriods1);
							srcPeriods.remove(periodIdx + 1);
						}
					}
				}
			}
		}

		if (!periodsModeUp.isEmpty() && !periodsModeDown.isEmpty()) {
			TimePatternFieldPeriod[] fieldPeriods0 = periodsModeUp.get(0);
			TimePatternFieldPeriod[] fieldPeriods1 = periodsModeDown.get(0);
			DoubleWildCardCount wcc = countWildCards(fieldPeriods0, fieldPeriods1);
			if (wcc.wildCardCount0 == wcc.wildCardCount1) {
				if (fieldPeriods0[wcc.wildCardCount0].getTo() + 1 == fieldPeriods1[wcc.wildCardCount1].getFrom()) {
					fieldPeriods0[wcc.wildCardCount0].setTo(fieldPeriods1[wcc.wildCardCount1].getTo());
					makeWildCards(fieldPeriods0);
					periodsModeDown.remove(0);
				}
				if (fieldPeriods0[wcc.wildCardCount0].getFrom() == fieldPeriods1[wcc.wildCardCount1].getTo() + 1) {
					fieldPeriods0[wcc.wildCardCount0].setFrom(fieldPeriods1[wcc.wildCardCount1].getFrom());
					makeWildCards(fieldPeriods0);
					periodsModeDown.remove(0);
				}
			}
		}

//		for (Iterator it = periods.iterator(); it.hasNext(); )
//			createTimePatternFromFieldPeriods((TimePeriod[]) it.next());

		logger.debug("TimePeriods in mode UP:");
		for (Object element : periodsModeUp)
			createTimePatternFromFieldPeriods(tps, (TimePatternFieldPeriod[]) element);

		logger.debug("TimePeriods in mode DOWN:");
		for (Object element : periodsModeDown)
			createTimePatternFromFieldPeriods(tps, (TimePatternFieldPeriod[]) element);
	}

	private static void makeWildCards(TimePatternFieldPeriod[] periods)
	{
		TimePatternCalendar cal = new TimePatternCalendar();
		int diffFieldIdx = -1;
		for (int i = CALFIELDS.length - 1; i >= 0; --i) {
			if (periods[i].getFrom() >= 0) {
				cal.set(i, periods[i].getFrom());
				if (periods[i].getFrom() != periods[i].getTo())
					diffFieldIdx = i;
			}
			else
				cal.set(i, cal.getActualMinimum(i));
		}
		if (diffFieldIdx >= 0) {
			if (periods[diffFieldIdx].getFrom() == cal.getActualMinimum(diffFieldIdx) &&
					periods[diffFieldIdx].getTo() == cal.getActualMaximum(diffFieldIdx)) {
				periods[diffFieldIdx].setFrom(-1);
				periods[diffFieldIdx].setTo(-1);
			}
		}
	}

	private static class DoubleWildCardCount
	{
		public DoubleWildCardCount(int _wildCardCount0, int _wildCardCount1)
		{
			wildCardCount0 = _wildCardCount0;
			wildCardCount1 = _wildCardCount1;
		}
		public int wildCardCount0;
		public int wildCardCount1;
	}

	private static DoubleWildCardCount countWildCards(TimePatternFieldPeriod[] periods0, TimePatternFieldPeriod[] periods1)
	{
		int wildCardCount0 = 0;
		int wildCardCount1 = 0;
		for (int i = 0; i < CALFIELDS.length; ++i) {
			if (periods0[i].getFrom() < 0)
				wildCardCount0 = i + 1;
			if (periods1[i].getFrom() < 0)
				wildCardCount1 = i + 1;

			if (periods0[i].getFrom() >= 0 && periods1[i].getFrom() >= 0)
				break;
		}
		return new DoubleWildCardCount(wildCardCount0, wildCardCount1);
	}

	private static class TimePatternCalendar
	{
		protected GregorianCalendar cal;

		public TimePatternCalendar()
		{
			cal = new GregorianCalendar();
		}

		public int get(int fieldIdx)
		{
			int value = cal.get(TimePatternSetUtil.CALFIELDS[fieldIdx]);
			if (fieldIdx == S_MONTH)
				++value;
			return value;
		}

		public void set(int fieldIdx, int value)
		{
			if (fieldIdx == S_MONTH)
				--value;

			cal.set(TimePatternSetUtil.CALFIELDS[fieldIdx], value);
			thisString = null;
		}

		public int getActualMinimum(int fieldIdx)
		{
			if (fieldIdx == S_MONTH)
				return 1;

			if (fieldIdx == S_YEAR)
				return 1000;

			return cal.getActualMinimum(CALFIELDS[fieldIdx]);
		}
		public int getActualMaximum(int fieldIdx)
		{
			if (fieldIdx == TimePatternSetUtil.S_MONTH)
				return 12;

			if (fieldIdx == TimePatternSetUtil.S_YEAR)
				return 3000;

			return cal.getActualMaximum(CALFIELDS[fieldIdx]);
		}

		public void setTime(Date date)
		{
			cal.setTime(date);
			thisString = null;
		}

		public long getTimeInMillis()
		{
			return cal.getTimeInMillis();
		}
		public void setTimeInMillis(long millis)
		{
			cal.setTimeInMillis(millis);
			thisString = null;
		}

		String thisString = null;

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			if (thisString == null) {
				StringBuffer sb = new StringBuffer(this.getClass().getName());
				sb.append('{');
				sb.append(get(S_YEAR));
				sb.append('-');
				sb.append(get(S_MONTH));
				sb.append('-');
				sb.append(get(S_DAY));
				sb.append(' ');
				sb.append(get(S_HOUR));
				sb.append(':');
				sb.append(get(S_MIN));
				sb.append('}');
				thisString = sb.toString();
			}
			return thisString;
		}
	}
}
