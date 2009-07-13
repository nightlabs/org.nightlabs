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

import java.util.Date;

/**
 * Utility methods for {@link TimePattern}s and {@link TimePatternSet}s.
 * 
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @author Marco Schulze - marco at nightlabs dot de
 * 
 * @deprecated All methods in this class are directly accessible in {@link TimePatternSet}.
 * 		Internal usage should delegate to {@link TimePatternSetUtil}.
 */
@Deprecated
public class TimePatternUtil
{
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
	 * @see #getNextMatchTime(TimePatternSet, Long)
	 * @deprecated Use {@link TimePatternSet#getNextMatchTime(Date)} directly.
	 */
	@Deprecated
	public static Date getNextMatchTime(TimePatternSet tps, Date lastExecDT)
	{
		return tps.getNextMatchTime(lastExecDT);
	}
	
	/**
	 * Get the next match time for a time pattern set. This method is protected,
	 * because it breaks old code to make it public and because the preferred
	 * API is {@link TimePatternSet#getNextMatchTime(Long)} anyway.
	 *
	 * @param tps The time pattern set
	 * @param lastExecDT The time of last execution or <code>null</code> if
	 * 		no execution happened before.
	 * @return The next time when the time pattern set matches
	 * 		or <code>null</code> if no such time can be found within the time
	 * 		until {@link TimePattern#YEAR_MAX}.
	 *
	 * @see #getNextMatchTime(TimePatternSet, Date)
	 * @deprecated Use {@link TimePatternSet#getNextMatchTime(Long)} directly.
	 */
	@Deprecated
	protected static Long getNextMatchTime(TimePatternSet tps, Long lastExecTimestamp)
	{
		return tps.getNextMatchTime(lastExecTimestamp);
	}
}
