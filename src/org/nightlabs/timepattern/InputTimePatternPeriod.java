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
import java.util.Date;

import org.apache.log4j.Logger;
import org.nightlabs.util.TimePeriod;

/**
 * Container for two {@link InputTimePattern}s which can return a {@link TimePattern}.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class InputTimePatternPeriod implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(InputTimePatternPeriod.class);
	
	private InputTimePattern from;
	private InputTimePattern to;
	
	/**
	 * Create a new {@link InputTimePatternPeriod}.
	 */
	public InputTimePatternPeriod() {
	}

	/**
	 * Create a new {@link InputTimePatternPeriod} within the given patterns.
	 * @param from The start {@link InputTimePattern} for the new {@link InputTimePatternPeriod} (can be null).
	 * @param from The end {@link InputTimePattern} for the new {@link InputTimePatternPeriod} (can be null).
	 */
	public InputTimePatternPeriod(InputTimePattern from, InputTimePattern to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * Returns whether the start {@link Date} of this {@link InputTimePatternPeriod} is set (!= null).
	 * 
	 * @return Whether the start {@link Date} of this {@link InputTimePatternPeriod} is set (!= null).
	 */
	public boolean isFromSet() {
		return from != null;
	}
	
	/**
	 * Returns whether the end {@link Date} of this {@link InputTimePatternPeriod} is set (!= null).
	 * 
	 * @return Whether the end {@link Date} of this {@link InputTimePatternPeriod} is set (!= null).
	 */
	public boolean isToSet() {
		return to != null;
	}

	/**
	 * Returns whether this {@link InputTimePatternPeriod} somehow confines the time,
	 * i.e. whether {@link #isFromSet()} or {@link #isToSet()}.
	 * 
	 * @return Whether this {@link InputTimePatternPeriod} somehow confines the time
	 */
	public boolean isConfining() {
		return isToSet() || isFromSet();
	}

	/**
	 * Returns the {@link TimePeriod} that results from the execution of the from- and
	 * to-InputTimePatterns of this {@link InputTimePatternPeriod}.
	 * 
	 * @param input The input-time to calculate the {@link TimePeriod} upon.
	 * @param throwExceptionOnError Whether or not errors in calculating the time from the
	 *            {@link InputTimePattern}s should be re-thrown. If this is <code>false</code> the
	 *            errors will be ignored and the part of the pattern will be null.
	 * @return An {@link TimePeriod} calculated from the InputTimePatterns of this period based on
	 *         the input-time.
	 * @throws Exception If throwExceptionOnError is <code>true</code> and an error occurs when
	 *             calculating the times.
	 */
	public TimePeriod getTimePeriod(Date input, boolean throwExceptionOnError) throws Exception {
		Date tpFrom = null;
		Date tpTo = null;
		if (from != null) {
			try {
				tpFrom = from.getTime(input);
			} catch (Exception e) {
				if (throwExceptionOnError) {
					throw e;
				} else {
					logger.error("Catched exception while executing from-InputTimePattern for InputTimePatternPeriod " + this
							+ ". Will ignore the error.", e);
					tpFrom = null;
				}
			}
		}
		if (to != null) {
			try {
				tpTo = to.getTime(input);
			} catch (Exception e) {
				if (throwExceptionOnError) {
					throw e;
				} else {
					logger.error("Catched exception while executing to-InputTimePattern for InputTimePatternPeriod " + this
							+ ". Will ignore the error.", e);
					tpFrom = null;
				}
			}
		}
		return new TimePeriod(tpFrom, tpTo);
	}
	
	@Override
	public String toString() {
		return "InputTimePatternPeriod(From: " + from + ", To: " + to + ")";
	}
}
