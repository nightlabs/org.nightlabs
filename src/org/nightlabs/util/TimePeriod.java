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

import java.io.Serializable;
import java.util.Date;

/**
 * Container for two {@link Date}s used for filtering.
 * It has {@link #getFrom()} and {@link #getTo()} which both can be null.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class TimePeriod implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Date from;
	private Date to;
	
	/**
	 * Create a new {@link TimePeriod}.
	 */
	public TimePeriod() {
	}

	/**
	 * Create a new {@link TimePeriod} within the given dates.
	 * @param from The start {@link Date} for the new {@link TimePeriod} (can be null).
	 * @param from The end {@link Date} for the new {@link TimePeriod} (can be null).
	 */
	public TimePeriod(Date from, Date to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * Retuns the start {@link Date} of this {@link TimePeriod} (might be null).
	 * 
	 * @return The start {@link Date} of this {@link TimePeriod}.
	 */
	public Date getFrom() {
		return from;
	}

	/**
	 * Set this {@link TimePeriod}s start {@link Date}.
	 * 
	 * @param from The new start {@link Date} for this {@link TimePeriod}
	 */
	public void setFrom(Date from) {
		this.from = from;
	}

	/**
	 * Retuns the end {@link Date} of this {@link TimePeriod} (might be null).
	 * 
	 * @return The end {@link Date} of this {@link TimePeriod}.
	 */
	public Date getTo() {
		return to;
	}

	/**
	 * Set this {@link TimePeriod}s end {@link Date}.
	 * 
	 * @param from The new end {@link Date} for this {@link TimePeriod}
	 */
	public void setTo(Date to) {
		this.to = to;
	}
	
	/**
	 * Returns whether the start {@link Date} of this {@link TimePeriod} is set (!= null).
	 * 
	 * @return Whether the start {@link Date} of this {@link TimePeriod} is set (!= null).
	 */
	public boolean isFromSet() {
		return from != null;
	}
	
	/**
	 * Returns whether the end {@link Date} of this {@link TimePeriod} is set (!= null).
	 * 
	 * @return Whether the end {@link Date} of this {@link TimePeriod} is set (!= null).
	 */
	public boolean isToSet() {
		return to != null;
	}

	/**
	 * Returns whether this {@link TimePeriod} somehow confines the time,
	 * i.e. whether {@link #isFromSet()} or {@link #isToSet()}.
	 * 
	 * @return Whether this {@link TimePeriod} somehow confines the time
	 */
	public boolean isConfining() {
		return isToSet() || isFromSet();
	}
	
	@Override
	public String toString() {
		return "TimePeriod(From: " + from + ", To: " + to + ")";
	}
}
