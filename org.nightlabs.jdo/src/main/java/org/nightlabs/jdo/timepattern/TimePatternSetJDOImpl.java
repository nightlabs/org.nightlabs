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

import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.FetchGroups;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PersistenceModifier;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.nightlabs.jdo.timepattern.id.TimePatternSetID;
import org.nightlabs.timepattern.TimePattern;
import org.nightlabs.timepattern.TimePatternFormatException;
import org.nightlabs.timepattern.TimePatternSet;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Alexander Bieber  <alex [AT] nightlabs [DOT] de>
 * @author Marco Schulze - marco at nightlabs dot de
 *
 * @jdo.persistence-capable
 *		identity-type="application"
 *		objectid-class="org.nightlabs.jdo.timepattern.id.TimePatternSetID"
 *		detachable="true"
 *		table="NightLabsJDO_TimePatternSet"
 *
 * @jdo.create-objectid-class
 *
 * @jdo.inheritance strategy="new-table"
 *
 * @jdo.fetch-group name="TimePatternSetJDOImpl.timePatterns" fields="timePatterns"
 */
@PersistenceCapable(
	objectIdClass=TimePatternSetID.class,
	identityType=IdentityType.APPLICATION,
	detachable="true",
	table="NightLabsJDO_TimePatternSet")
@FetchGroups(
	@FetchGroup(
		name=TimePatternSetJDOImpl.FETCH_GROUP_TIME_PATTERNS,
		members=@Persistent(name="timePatterns"))
)
@Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
public class TimePatternSetJDOImpl
extends TimePatternSet
{
	private static final long serialVersionUID = 1L;

	public static final String FETCH_GROUP_TIME_PATTERNS = "TimePatternSetJDOImpl.timePatterns";

	/**
	 * @jdo.field primary-key="true"
	 * @jdo.column length="100"
	 */
	@PrimaryKey
	@Column(length=100)
	private String timePatternSetID;

	/**
	 * @deprecated Only for JDO!
	 */
	@Deprecated
	protected TimePatternSetJDOImpl() { }

	public TimePatternSetJDOImpl(String timePatternSetID) {
		this.timePatternSetID = timePatternSetID;
		timePatterns = new HashSet<TimePatternJDOImpl>();
	}

	public String getTimePatternSetID()
	{
		return timePatternSetID;
	}

	/**
	 * @jdo.field
	 *		persistence-modifier="persistent"
	 *		collection-type="collection"
	 *		element-type="TimePatternJDOImpl"
	 *		dependent-element="true"
	 *		mapped-by="timePatternSet"
	 */
	@Persistent(
		dependentElement="true",
		mappedBy="timePatternSet",
		persistenceModifier=PersistenceModifier.PERSISTENT
	)
	private Set<TimePatternJDOImpl> timePatterns;

	@Override
	public Set<TimePattern> _getTimePatterns() {
		return CollectionUtil.castSet(timePatterns);
	}

	@Override
	public void addTimePattern(TimePattern pattern)
	{
		if (!(pattern instanceof TimePatternJDOImpl))
			throw new IllegalArgumentException("pattern is not an implementation of TimePatternJDOImpl");

		super.addTimePattern(pattern);
	}

	@Override
	public TimePattern createTimePattern() {
		TimePattern n = new TimePatternJDOImpl(this);
		_getTimePatterns().add(n);
		return n;
	}

	@Override
	public TimePattern createTimePattern(String _year, String _month,
			String _day, String _dayOfWeek, String _hour, String _minute)
			throws TimePatternFormatException {
		TimePattern n = new TimePatternJDOImpl(this, _year, _month, _day ,_dayOfWeek, _hour, _minute);
		_getTimePatterns().add(n);
		return n;
	}

	@Override
	public int hashCode()
	{
		return timePatternSetID.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;

		if (!(obj instanceof TimePatternSetJDOImpl))
			return false;

		TimePatternSetJDOImpl o = (TimePatternSetJDOImpl) obj;

		return this.timePatternSetID.equals(o.timePatternSetID);
	}
}
