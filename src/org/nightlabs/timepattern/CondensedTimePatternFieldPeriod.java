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

public class CondensedTimePatternFieldPeriod
{
	public CondensedTimePatternFieldPeriod() { }
	public CondensedTimePatternFieldPeriod(Date _fromDT, Date _toDT)
	{
		this.fromDT = _fromDT;
		this.toDT = _toDT;
	}
	
	private Date fromDT;
	private Date toDT;

	/**
	 * @return Returns the fromDT.
	 */
	public Date getFromDT() {
		return fromDT;
	}
	/**
	 * @param fromDT The fromDT to set.
	 */
	public void setFromDT(Date fromDT) {
		this.fromDT = fromDT;
	}
	/**
	 * @return Returns the toDT.
	 */
	public Date getToDT() {
		return toDT;
	}
	/**
	 * @param toDT The toDT to set.
	 */
	public void setToDT(Date toDT) {
		this.toDT = toDT;
	}
}