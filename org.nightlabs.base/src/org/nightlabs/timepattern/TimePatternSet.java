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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;



/**
 * This class manages a set of {@link TimePattern}s to allow more flexibility in defining times.
 * The patterns are combined by OR; this means, you can check the validity of a time stamp
 * where it is reckoned to match, if one of the patterns matches.
 * <p>
 * If the TimePatternSet is empty (means it does not contain any TimePattern), the
 * <code>matches(...)</code> method always returns <code>false</code> (means it never
 * matches). To make it match always, create one empty pattern with the method
 * <code>createTimePattern()</code>. The contract for an empty TimePattern is to always
 * match.
 *
 * @author Marco Schulze
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public abstract class TimePatternSet
	implements Serializable
{
	private static final long serialVersionUID = 1L;
	public static final int YEAR_MIN = TimePattern.YEAR_MIN;
	public static final int YEAR_MAX = TimePattern.YEAR_MAX;

	/**
	 * Returns an unmodifiable Set of all {@link TimePattern}
	 * in this TimePatternSet.
	 *
	 * @return An unmodifialbe set of all patterns in this set.
	 */
	public Set<TimePattern> getTimePatterns() {
		return Collections.unmodifiableSet(_getTimePatterns());
	}

	/**
	 * This method should return the actual Set the
	 * {@link TimePattern}s are stored in.
	 * <p>
	 * It is used internally to modify the set.
	 *
	 * @return The actual Set where the patterns are stored.
	 */
	protected abstract Set<TimePattern> _getTimePatterns();

	/**
	 * Create a new empty TimePatternSet.
	 */
	public TimePatternSet()
	{
	}

	/**
	 * Create a <em>new</em> {@link TimePattern} and
	 * add it to the set of patterns in this set.
	 *
	 * @return A <em>new</em> {@link TimePattern}.
	 */
	public abstract TimePattern createTimePattern();

	/**
	 * Create a <em>new</em> {@link TimePattern} and
	 * initialize it with the given values. The pattern
	 * will be added to the set of patterns in this set.
	 *
	 * @param _year The year field for the new pattern.
	 * @param _month The month field for the new pattern.
	 * @param _day The day field for the new pattern.
	 * @param _dayOfWeek The dayofweek field for the new pattern.
	 * @param _hour The hour field for the new pattern.
	 * @param _minute The minute field for the new pattern.
	 * @return A <em>new</em> {@link TimePattern}.
	 * @throws TimePatternFormatException
	 */
	public abstract TimePattern createTimePattern(String _year, String _month, String _day, String _dayOfWeek, String _hour, String _minute) throws TimePatternFormatException;

	/**
	 * Creates a <em>new</em> {@link TimePattern} and initializes
	 * with the values of the given pattern.
	 * The new pattern will be added to the set of patterns in this set.
	 *
	 * @param pattern The pattern whose values will serve for the new pattern.
	 * @return A new pattern initialized to the values of the given one.
	 * @throws TimePatternFormatException
	 */
	public TimePattern createTimePattern(TimePattern pattern)
	throws TimePatternFormatException
	{
		return createTimePattern(
				pattern.getYear(), pattern.getMonth(),
				pattern.getDay(), pattern.getDayOfWeek(),
				pattern.getHour(), pattern.getMinute()
			);
	}

	/**
	 * Adds the given {@link TimePattern} to the set of patterns
	 * in this {@link TimePatternSet}.
	 * <p>
	 * Note that this will throw an {@link IllegalStateException} if
	 * the pattern is associated to another {@link TimePatternSet}.
	 * This will be investigated by pattern.getTimePatternSet()
	 *
	 * @param timePattern The pattern to add
	 */
	public void addTimePattern(TimePattern timePattern)
	{
		if (timePattern.getTimePatternSet() != null && !this.equals(timePattern.getTimePatternSet()))
			throw new IllegalStateException("Given TimePattern " + timePattern + " is already associated with another TimePatternSet: " + timePattern.getTimePatternSet());
		timePattern.setTimePatternSet(this);
		_getTimePatterns().add(timePattern);
	}

	/**
	 * Clears the set of time patterns held
	 * by this {@link TimePatternSet}.
	 */
	public void clearTimePatterns() {
		_getTimePatterns().clear();
	}

	public boolean removeTimePattern(TimePattern timePattern)
	{
		return _getTimePatterns().remove(timePattern);
	}

	/**
	 * This method checks a given timestamp whether it matches one of the patterns defined
	 * in this set. If this set is empty, this method returns always false.
	 *
	 * @param timeStamp The time to check whether it matches the patterns.
	 * @param maxDepth The maximum pattern part (YEAR-MINUTE) to check
	 * @return True, if one of the patterns in this set matches. Otherwise, false.
	 */
	public boolean matches(long timeStamp, byte maxDepth)
	{
		return matches(timeStamp, TimePattern.YEAR, maxDepth);
	}

	/**
	 * This is a convenience method calling {@link #matches(long, byte)}.
	 */
	public boolean matches(Date timeStamp, byte maxDepth)
	{
		return matches(timeStamp.getTime(), maxDepth);
	}

	/**
	 * This is a convenience method calling {@link #matches(long, byte, byte)}.
	 */
	public boolean matches(Date timeStamp, byte minDepth, byte maxDepth)
	{
		return matches(timeStamp.getTime(), minDepth, maxDepth);
	}

	public boolean matches(long timeStamp, byte minDepth, byte maxDepth)
	{
		for (Object element : _getTimePatterns()) {
			TimePattern pattern = (TimePattern) element;
			if (pattern.matches(timeStamp, minDepth, maxDepth))
				return true;
		}
		return false;
	}

	/**
	 * Whether this {@link TimePatternSet} with all its
	 * {@link TimePattern}s combined with <em>OR</em> does
	 * match for the given time stamp.
	 *
	 * @param timeStamp The timestamp to check.
	 *
	 * @return Whether this set matches the given time.
	 */
	public boolean matches(long timeStamp) {
		return matches(timeStamp, TimePattern.MINUTE);
	}

	/**
	 * This is a convenience method calling {@link #matches(long)}.
	 *
	 * @param timeStamp The timestamp to check.
	 *
	 * @return Whether this set matches the given time.
	 */
	public boolean matches(Date timeStamp) {
		return matches(timeStamp.getTime());
	}

	/**
	 * This method adds as many TimePatterns as there are necessary to represent the given
	 * CondensedPeriod.
	 *
	 * @param cp The CondensedPeriod to add.
	 */
	public void addCondensedPeriod(CondensedTimePatternFieldPeriod cp)
	{
		TimePatternSetUtil.addCondensedPeriod(this, cp);
	}

	public void setCondensedPeriod(CondensedTimePatternFieldPeriod condensedPeriod)
	{
		setCondensedPeriods(new CondensedTimePatternFieldPeriod[] {condensedPeriod});
	}

	public void setCondensedPeriods(CondensedTimePatternFieldPeriod[] condensedPeriods)
	{
		_getTimePatterns().clear();

		for (CondensedTimePatternFieldPeriod element : condensedPeriods) {
			addCondensedPeriod(element);
		}
	}

	public void setCondensedPeriods(Collection<CondensedTimePatternFieldPeriod> condensedPeriods)
	{
		_getTimePatterns().clear();

		for (Iterator<CondensedTimePatternFieldPeriod> it = condensedPeriods.iterator(); it.hasNext(); ) {
			CondensedTimePatternFieldPeriod cp = it.next();
			addCondensedPeriod(cp);
		}
	}

	public TimePatternFieldPeriod getFirstAndLastYear()
	{
		int yearMin = Integer.MAX_VALUE;
		int yearMax = Integer.MIN_VALUE;

		for (Object element : _getTimePatterns()) {
			TimePattern tp = (TimePattern) element;
			for (Object element0 : tp.getTimePeriods(TimePattern.YEAR)) {
				TimePatternFieldPeriod period = (TimePatternFieldPeriod) element0;
				if (period.getFrom() != -1) {
					yearMin = Math.min(yearMin, period.getFrom());
					yearMax = Math.max(yearMax, period.getTo());
				}
			}
		}

		if (yearMin < YEAR_MIN || yearMin > YEAR_MAX)
			yearMin = YEAR_MIN;

		if (yearMax > YEAR_MAX || yearMax < YEAR_MIN)
			yearMax = YEAR_MAX;

		return new TimePatternFieldPeriod(yearMin, yearMax);
	}

//	public TimePeriod getFirstAndLastMonth()
//	{
//		int monthMin = Integer.MAX_VALUE;
//		int monthMax = Integer.MIN_VALUE;
//
//		for (Iterator itTP = getTimePatterns().iterator(); itTP.hasNext(); ) {
//			TimePattern tp = (TimePattern) itTP.next();
//			for (Iterator itP = tp.getTimePeriods(TimePattern.MONTH).iterator(); itP.hasNext(); ) {
//				TimePeriod period = (TimePeriod) itP.next();
//				if (period.getFrom() != -1) {
//					monthMin = Math.min(monthMin, period.getFrom());
//					monthMax = Math.max(monthMax, period.getTo());
//				}
//			}
//		}
//
//		if (monthMin < TimePattern.MONTH_MIN || monthMin > TimePattern.MONTH_MAX)
//			monthMin = TimePattern.MONTH_MIN;
//
//		if (monthMax > TimePattern.MONTH_MAX || monthMax < TimePattern.MONTH_MIN)
//			monthMax = TimePattern.MONTH_MAX;
//
//		return new TimePeriod(monthMin, monthMax);
//	}

	/**
	 * Get the next match time for a time pattern set.
	 *
	 * @param lastExecDT The time of last execution or <code>null</code> if
	 * 		no execution happened before.
	 * @return The next time when the time pattern set matches
	 * 		or <code>null</code> if no such time can be found within the time
	 * 		until {@link TimePattern#YEAR_MAX}.
	 *
	 * @see #getNextMatchTime(TimePatternSet, Long)
	 */
	public Date getNextMatchTime(Date lastExecDT)
	{
		Long nextMatchTime = TimePatternSetUtil.getNextMatchTime(this, lastExecDT == null ? null : lastExecDT.getTime());
		if(nextMatchTime == null)
			return null;
		else
			return new Date(nextMatchTime);
	}

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
	public Long getNextMatchTime(Long lastExecTimestamp)
	{
		return TimePatternSetUtil.getNextMatchTime(this, lastExecTimestamp);
	}

	/**
	 * This method calculates, which {@link CondensedTimePatternFieldPeriod}s (i.e. simple from-to-ranges)
	 * are within this TimePatternSet. This is only an approximation, limited by
	 * {@link #YEAR_MIN} and {@link #YEAR_MAX},
	 * because a TimePatternSet can express
	 * eternal times (e.g. if no year is defined, there are time periods
	 * in all eternity) and thus an exact representation is not possible.
	 *
	 * @return Returns instances of {@link CondensedTimePatternFieldPeriod}.
	 */
	public List<CondensedTimePatternFieldPeriod> getCondensedPeriods()
	{
		long milliSecPerYear = 365L * 24L * 3600L * 1000L;
		TimePatternFieldPeriod firstAndLastYear = getFirstAndLastYear();

		ArrayList<CondensedTimePatternFieldPeriod> periods = new ArrayList<CondensedTimePatternFieldPeriod>();

		long timeStampMin = milliSecPerYear * (firstAndLastYear.getFrom() - 1970L);
		long timeStampMax = milliSecPerYear * (firstAndLastYear.getTo() + 1L - 1970L);
		long fromDT = -1; long toDT = -1;
		for (long timeStamp = timeStampMin; timeStamp <= timeStampMax; timeStamp += 60000) { // we step by 60 sec, because our patterns don't know anything smaller than a minute
			if ( matches(timeStamp)) {
				if (fromDT < 0)
					fromDT = timeStamp;
				toDT = timeStamp;
			}
			else {
				if (fromDT >= 0) {
					CondensedTimePatternFieldPeriod cp = new CondensedTimePatternFieldPeriod(new Date(fromDT), new Date(toDT));
					periods.add(cp);
					fromDT = -1;
					toDT = -1;
				}
			}
		}

		return periods;
	}

	public void copyFrom(TimePatternSet other)
	throws TimePatternFormatException
	{
		_getTimePatterns().clear();
		for (TimePattern newPattern : other.getTimePatterns()) {
			createTimePattern(newPattern);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null || !(obj instanceof TimePatternSet))
			return false;
		Set<TimePattern> myTimePatterns = _getTimePatterns();
		Set<TimePattern> otherTimePatterns = ((TimePatternSet)obj)._getTimePatterns();
		return
				myTimePatterns == otherTimePatterns ||
				(myTimePatterns != null && myTimePatterns.equals(otherTimePatterns));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		Set<TimePattern> myTimePatterns = _getTimePatterns();
		return myTimePatterns == null ? 0 : myTimePatterns.hashCode();
	}
}
