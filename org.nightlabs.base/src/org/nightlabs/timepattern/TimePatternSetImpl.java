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

import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexander Bieber
 */
public class TimePatternSetImpl extends TimePatternSet {

	private static final long serialVersionUID = 1L;

	public TimePatternSetImpl()
	{
	}

	private Set<TimePattern> timePatterns;

	@Override
	public TimePattern createTimePattern() {
		TimePattern n = new TimePatternImpl(this);
		_getTimePatterns().add(n);
		return n;
	}

	@Override
	public TimePattern createTimePattern(String _year, String _month,
			String _day, String _dayOfWeek, String _hour, String _minute)
			throws TimePatternFormatException
	{
		TimePattern n = new TimePatternImpl(this, _year, _month, _day, _dayOfWeek, _hour, _minute);
		_getTimePatterns().add(n);
		return n;
	}

	@Override
	protected Set<TimePattern> _getTimePatterns() {
		if (timePatterns == null)
			timePatterns = new HashSet<TimePattern>();
		return timePatterns;
	}

}
