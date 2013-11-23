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

public class TimePatternFieldPeriod
{
	public TimePatternFieldPeriod(int from, int to)
	{
		this.from = from;
		this.to = to;
	}

	private int from;
	private int to;

	public int getFrom() { return this.from; }
	public int getTo() { return this.to; }
	
	public void setFrom(int _from)
	{
		this.from = _from;
		thisString = null;
	}
	public void setTo(int _to)
	{
		this.to = _to;
		thisString = null;
	}

	protected String thisString = null;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (thisString == null) {
			StringBuffer sb = new StringBuffer(this.getClass().getName());
			sb.append('{');
			sb.append(from);
			sb.append(',');
			sb.append(to);
			sb.append('}');
			thisString = sb.toString();
		}

		return thisString;
	}
}