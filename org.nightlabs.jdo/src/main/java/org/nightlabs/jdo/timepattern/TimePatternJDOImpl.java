/* *****************************************************************************
 * NightLabsJDO - NightLabs Utilities for JDO                                  *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jdo.timepattern;

import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.timepattern.TimePattern;
import org.nightlabs.timepattern.TimePatternFormatException;
import org.nightlabs.timepattern.TimePatternSet;

import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.FetchGroups;
import org.nightlabs.jdo.timepattern.TimePatternID;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceModifier;


/**
 * @author Alexander Bieber  <alex [AT] nightlabs [DOT] de>
 * @author Marco Schulze - marco at nightlabs dot de
 * 
 * @jdo.persistence-capable
 *		identity-type="application"
 *		objectid-class="org.nightlabs.jdo.timepattern.TimePatternID"
 *		detachable="true"
 *		table="NightLabsJDO_TimePattern"
 *
 * @jdo.create-objectid-class
 *
 * @jdo.inheritance strategy="new-table"
 *
 * @jdo.fetch-group name="TimePatternJDOImpl.timePatternSet" fields="timePatternSet"
 * @jdo.fetch-group name="TimePatternSetJDOImpl.timePatterns" fields="timePatternSet"
 */
@PersistenceCapable(
	objectIdClass=TimePatternID.class,
	identityType=IdentityType.APPLICATION,
	detachable="true",
	table="NightLabsJDO_TimePattern")
@FetchGroups({
	@FetchGroup(
		name=TimePatternJDOImpl.FETCH_GROUP_TIME_PATTERN_SET,
		members=@Persistent(name="timePatternSet")),
	@FetchGroup(
		name="TimePatternSetJDOImpl.timePatterns",
		members=@Persistent(name="timePatternSet"))
})
@Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
public class TimePatternJDOImpl extends TimePattern
{
	private static final long serialVersionUID = 1L;

	public static final String FETCH_GROUP_TIME_PATTERN_SET = "TimePatternJDOImpl.timePatternSet";

	/**
	 * @jdo.field primary-key="true"
	 * @jdo.column length="50"
	 */
	@PrimaryKey
	@Column(length=50)
	private String timePatternID;

	/**
	 * @deprecated Only for JDO!
	 */
	@Deprecated
	protected TimePatternJDOImpl() {
	}

	/**
	 * @param _timePatternSet
	 */
	public TimePatternJDOImpl(TimePatternSet _timePatternSet) {
		super(_timePatternSet);
		timePatternID = ObjectIDUtil.makeValidIDString(null);
		year = "*";
		month = "*";
		day = "*";
		dayOfWeek = "*";
		hour = "*";
		minute = "*";
	}

	public String getTimePatternID()
	{
		return timePatternID;
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
	public TimePatternJDOImpl(TimePatternSet _timePatternSet, String _year,
			String _month, String _day, String _dayOfWeek, String _hour,
			String _minute) throws TimePatternFormatException {
		super(_timePatternSet, _year, _month, _day, _dayOfWeek, _hour, _minute);
		timePatternID = ObjectIDUtil.makeValidIDString(null);
	}

	/**
	 * contains the years as numbers (0-MAXINT)
	 * 
	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	private String year;

	@Override
	public String getYear() {
		return year;
	}

	@Override
	protected void _setYear(String _year) {
		this.year = _year;
	}


	/**
	 * contains the months as numbers (1-12)
	 * 
	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	private String month;
	
	@Override
	public String getMonth() {
		return month;
	}

	@Override
	protected void _setMonth(String _month) {
		this.month = _month;
	}

	
	/**
	 * contains the days of month (1-31)
	 * 
	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	private String day;
	
	@Override
	public String getDay() {
		return day;
	}

	@Override
	protected void _setDay(String _day) {
		this.day = _day;
	}

	/**
	 * contains the days of week
	 * 
	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	private String dayOfWeek;
	
	@Override
	public String getDayOfWeek() {
		return dayOfWeek;
	}

	@Override
	protected void _setDayOfWeek(String _dayOfWeek) {
		this.dayOfWeek = _dayOfWeek;
	}

	/**
	 * contains the hours (0-23)
	 * 
	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	private String hour;
	
	@Override
	public String getHour() {
		return hour;
	}

	@Override
	protected void _setHour(String _hour) {
		this.hour = _hour;
	}

	/**
	 * contains the minutes (0-59)
	 * 
	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	private String minute;
	
	@Override
	public String getMinute() {
		return minute;
	}

	@Override
	protected void _setMinute(String _minute) {
		this.minute = _minute;
	}

	/**
	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	private TimePatternSetJDOImpl timePatternSet;
	
	@Override
	public TimePatternSet getTimePatternSet() {
		return timePatternSet;
	}

	@Override
	protected void setTimePatternSet(TimePatternSet set) {
		this.timePatternSet = (TimePatternSetJDOImpl) set;
	}

	@Override
	public int hashCode()
	{
		return timePatternID.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;

		if (!(obj instanceof TimePatternJDOImpl))
			return false;

		TimePatternJDOImpl o = (TimePatternJDOImpl) obj;

		return this.timePatternID.equals(o.timePatternID);
	}
}
