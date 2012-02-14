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
package org.nightlabs.version;

/**
 * @author Marius Heinzmann -- Marius[at]NightLabs[dot]de
 *
 */
public class VersionRangeEndPoint
	implements Comparable<VersionRangeEndPoint>
{
	private Version endPoint;
	private boolean inclusive;
	private EndPointLocation location;
	
	public enum EndPointLocation {
		LOWER, UPPER
	}

	public VersionRangeEndPoint(Version endPoint, boolean inclusive, EndPointLocation location) {
		this.endPoint = endPoint;
		this.inclusive = inclusive;
		this.location = location;
	}
	
	public VersionRangeEndPoint(String rangePointString) throws MalformedVersionException {
		parseRangeString(rangePointString);
	}
	
	public char getInclusiveChar() {
		char[] symbols = inclusive ? INCLUSIVE_BRACKETS : EXCLUSIVE_BRACKETS;
		if (EndPointLocation.LOWER.equals(location))
			return symbols[0];
		else
			return symbols[1];
	}
	
	public Version getEndPoint() {
		return endPoint;
	}

	public boolean isInclusive() {
		return inclusive;
	}

	public EndPointLocation getLocation() {
		return location;
	}

	public VersionRangeEndPoint changeVersion(Version newEndPoint) {
		return new VersionRangeEndPoint(newEndPoint, isInclusive(), location);
	}
	
	public VersionRangeEndPoint changeInclusive(boolean inclusive) {
		return new VersionRangeEndPoint(endPoint, inclusive, location);
	}
	
	public VersionRangeEndPoint changeLocation(EndPointLocation location) {
		return new VersionRangeEndPoint(endPoint, inclusive, location);
	}
	
	public int compareTo(VersionRangeEndPoint other) {
		return endPoint.compareTo(other.endPoint);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj) 
			return true;
		if (! (getClass().equals(obj.getClass())) )
			return false;
		
		final VersionRangeEndPoint other = (VersionRangeEndPoint) obj;
		
		return endPoint.equals(other.endPoint) &&
					 inclusive == other.inclusive &&
					 location.equals(other.location);
	}
	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = result * PRIME + endPoint.hashCode();
		result = result * PRIME + Boolean.valueOf(inclusive).hashCode();
		result = result * PRIME + location.hashCode();
		return result;
	}
	
	public static final char[] INCLUSIVE_BRACKETS = new char[] { '[', ']' };
	public static final char[] EXCLUSIVE_BRACKETS = new char[] { '(', ')' };

	private void parseRangeString(String rangePointString) throws MalformedVersionException {
		if (rangePointString == null || rangePointString.trim().length() == 0)
			throw new IllegalArgumentException("The given string of an encoded VersionRangeEndPoint " +
					"must NOT be null or empty!");
		
		rangePointString = rangePointString.trim();
		if (rangePointString.startsWith(String.valueOf(INCLUSIVE_BRACKETS[0])) ||
				rangePointString.endsWith(String.valueOf(INCLUSIVE_BRACKETS[1])) )
			inclusive = true;
		else if (rangePointString.startsWith(String.valueOf(EXCLUSIVE_BRACKETS[0])) ||
				rangePointString.endsWith(String.valueOf(EXCLUSIVE_BRACKETS[1])) )
			inclusive = false;
		else
			throw new IllegalArgumentException("The given string of an encoded VersionRangeEndPoint " +
					"does not start/end with a valid inclusion/exclusion symbol! Valid Symbols:"+
					String.valueOf(INCLUSIVE_BRACKETS)+" or "+String.valueOf(EXCLUSIVE_BRACKETS)
					+". Given String: "+	rangePointString);
		
		if (rangePointString.startsWith(String.valueOf(INCLUSIVE_BRACKETS[0])) ||
				rangePointString.startsWith(String.valueOf(EXCLUSIVE_BRACKETS[0])) )
			location = EndPointLocation.LOWER;
		else
			location = EndPointLocation.UPPER;

		// remove all inclusive/exclusive symbols
		String versionString = rangePointString.replaceAll(
				"[\\"+INCLUSIVE_BRACKETS[0]+"\\"+INCLUSIVE_BRACKETS[1]
		    +"\\"+EXCLUSIVE_BRACKETS[0]+"\\"+EXCLUSIVE_BRACKETS[1]+"]", "");
		
		endPoint = new Version(versionString);
	}
	
	@Override
	public String toString() {
		return toString(true);
	}

	public String toString(boolean printInclusiveChar) {
		StringBuffer buffer = new StringBuffer();
		
		if (printInclusiveChar && EndPointLocation.LOWER.equals(location)) {
			if (inclusive)
				buffer.append(INCLUSIVE_BRACKETS[0]);
			else
				buffer.append(EXCLUSIVE_BRACKETS[0]);
		}

		buffer.append(endPoint.toString());
		
		if (printInclusiveChar && EndPointLocation.UPPER.equals(location)) {
			if (inclusive)
				buffer.append(INCLUSIVE_BRACKETS[1]);
			else
				buffer.append(EXCLUSIVE_BRACKETS[1]);
		}
		
		return buffer.toString();
	}
}
