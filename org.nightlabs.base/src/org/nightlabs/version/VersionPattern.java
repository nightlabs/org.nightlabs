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

import java.io.Serializable;
import java.util.StringTokenizer;

import org.nightlabs.version.VersionRangeEndPoint.EndPointLocation;

/**
 * A VersionPattern represents an interval of valid versions. The interval is defined by two end
 * points, a lower end point <code>min</code> and an upper end point <code>max</code>, where
 * <code>min</code> has to be smaller than <code>max</code>. Additionally it is possible to define
 * if <code>min</code> and <code>max</code> belong to this interval of valid versions by setting
 * <code>minInclusive = true</code>, <code>maxInclusive = true</code> respectively.
 *
 *  <p>Note: This class is immutable!</p>
 *
 * @author Marius Heinzmann -- Marius[at]NightLabs[dot]de
 *
 *	old JDO-Tags:
 *
 * 	identity-type="application"
 *	objectid-class="org.nightlabs.version.VersionPatternID"
 *
 * @!jdo.create-objectid-class
 * 	field-order="min, max, minInclusive, maxInclusive"
 *
 * jdo.persistence-capable
 *	embedded-only="true"
 * 	detachable="true"
 *
 * @deprecated Moved to separate artifact "org.nightlabs.version". The package "org.nightlabs.version" should
 * be removed from artifact "org.nightlabs.base" and a dependency onto artifact "org.nightlabs.version" should
 * be introduced instead. Or even better we should check if we can migrate to the version-handling-classes from OSGI
 * (e.g. org.osgi.framework.Version and org.eclipse.osgi.service.resolver.VersionRange). Marco :-)
 */
@Deprecated
public class VersionPattern implements Serializable, IVersionFilter {

	private static final long serialVersionUID = 1L;

	/**
	 * This pattern matches all possible Versions.
	 */
	public static final VersionPattern MATCHES_ALL = new VersionPattern(
			new VersionRangeEndPoint(Version.MIN_VERSION, true, EndPointLocation.LOWER),
			new VersionRangeEndPoint(Version.MAX_VERSION, true, EndPointLocation.UPPER)
			);

	/**
	 * The lower end point of this interval of {@link Version}s.
	 */
	private VersionRangeEndPoint min;

	/**
	 * The upper end point of this interval of {@link Version}s.
	 */
	private VersionRangeEndPoint max;

	/**
	 * Describes rules which easily allow defining ranges given the lower bound (<code>min</code>).
	 */
	public enum MatchRule {
		/**
		 * Only the given Version is valid
		 */
		Perfect("perfect"),

		/**
		 * Describes a pattern, in which all versions starting from given <code>min</code>
		 * (e.g. 1.0.3-6-test) to max 1.0.MAX_INT_VALUE-MAX_INT_VALUE .
		 * In other words: All version greater or equal <code>min</code> but not with a greater major
		 * or minor are valid.
		 */
		Equivalent("equivalent"),

		/**
		 * Describes a pattern, in which all versions starting from given <code>min</code>
		 * (e.g. 1.0.3-6-test) to max 1.MAX_INT_VALUE.MAX_INT_VALUE-MAX_INT_VALUE .
		 * In other words: All version greater or equal <code>min</code> but not with a greater major
		 * are valid.
		 */
		Compatible("compatible"),

		/**
		 * Describes a patter, in which all versions greater or equal the given <code>min</code> version
		 * are valid.
		 */
		GreaterOrEqual("greaterOrEqual");

		private String featureXMLString;

		private MatchRule(String featureXMLString) {
			this.featureXMLString = featureXMLString;
		}

		public static MatchRule getMatchRuleFromString(String matchRule) {
			if (matchRule == null || matchRule.length() == 0)
				return Compatible; // is default value

			if (Perfect.featureXMLString.equals(matchRule))
				return Perfect;
			else if (Equivalent.featureXMLString.equalsIgnoreCase(matchRule))
				return Equivalent;
			else if (Compatible.featureXMLString.equalsIgnoreCase(matchRule))
				return Compatible;
			else if (GreaterOrEqual.featureXMLString.equalsIgnoreCase(matchRule))
				return GreaterOrEqual;
			else
				throw new IllegalArgumentException("The given string '"+matchRule+"' is not defined " +
						"as a MatchRule in the Eclipse Feature Manifest!");
		}
	}

	/**
	 * Creates a new VersionPattern with the interval from <code>min</code> to <code>max</code>, where
	 * <code>min</code> and <code>max</code> are part of the valid versions interval if
	 * <code>minInclusive == true</code>, <code>maxInclusive == true</code> respectively.
	 * <p>
	 * Note: The invariant is that <code>min</code> is always the smaller version (lower end point)
	 * and <code>max</code> is the bigger version (upper end point). So make sure you pass the smaller
	 * version as <code>min</code> and the bigger version as <code>max</code>!
	 * </p>
	 * @param min the lower end point of the valid versions interval
	 * @param max the upper end point of the valid versions interval
	 * @param minInclusive whether <code>min</code> is part of the valid versions interval.
	 * @param maxInclusive whether <code>max</code> is part of the valid versions interval.
	 */
	public VersionPattern(VersionRangeEndPoint min, VersionRangeEndPoint max) {
		if (min == null || max == null)
			throw new IllegalArgumentException("min and max must NOT be null!");
		if (min.compareTo(max) > 0)
			throw new IllegalArgumentException("The given min version is not allowed to be greater than max!");
		this.min = min;
		this.max = max;
	}

	/**
	 * Creates a new VersionPattern by giving the lower (valid) end point (<code>min</code>) and
	 * specifying one of the {@link MatchRule}s, which defines how the upper end point is defined.
	 *
	 * @param min the lower end point of the valid versions interval.
	 * @param rule the {@link MatchRule} to use for defining the upper end point of the valid versions
	 * 		interval.
	 */
	public VersionPattern(VersionRangeEndPoint min, MatchRule rule) {
		this(min, getUpperBoundForRule(min, rule));
	}

	public VersionPattern(String versionPatternString) {
		parsePatternString(versionPatternString);
	}

	/**
	 * Returns the upper bound of valid versions given a minimum version and a {@link MatchRule}.
	 *
	 * @param min the minimum Version, the lower bound of the valid version range.
	 * @param rule the {@link MatchRule} to use for determining the upper bound (<code>max</code>).
	 * @return the upper bound of valid versions given a minimum version and a {@link MatchRule}.
	 */
	private static VersionRangeEndPoint getUpperBoundForRule(VersionRangeEndPoint min, MatchRule rule) {
		VersionRangeEndPoint max = min.changeLocation(EndPointLocation.UPPER);
		if (! max.isInclusive())
			max = max.changeInclusive(true);

		Version upperEndPoint = max.getEndPoint();
		boolean changed = false;
		switch (rule) {
		case GreaterOrEqual:
			upperEndPoint = upperEndPoint.changeMajor(Integer.MAX_VALUE);
		case Equivalent:
			upperEndPoint = upperEndPoint.changeMinor(Integer.MAX_VALUE);
		case Compatible:
			upperEndPoint = upperEndPoint.changeRelease(Integer.MAX_VALUE);
			upperEndPoint = upperEndPoint.changePatchLevel(Integer.MAX_VALUE);
			changed = true;
		case Perfect:
			if (changed)
				max = max.changeVersion(upperEndPoint);
			return max;

		default:
			throw new RuntimeException("The MatchRule enum has been extended but the " +
					"'getUpperBoundForRule' method hasn't been updated!");
		}
	}

	/**
	 * Returns <code>true</code> if the given <code>version</code> is in the range of valid versions,
	 * <code>false</code> otherwise.
	 *
	 * @param version the version to check for validity.
	 * @return <code>true</code> if the given <code>version</code> is in the range of valid versions,
	 * <code>false</code> otherwise.
	 */
	@Override
	public boolean matches(Version version) {
		boolean lowerBoundHolds = false;
		if (min.getEndPoint().compareTo(version) < 0 || min.getEndPoint().compareTo(version) == 0
				&& min.isInclusive())
			lowerBoundHolds = true;

		boolean upperBoundHolds = false;
		if (max.getEndPoint().compareTo(version) > 0 || max.getEndPoint().compareTo(version) == 0
				&& max.isInclusive())
			upperBoundHolds = true;

		return lowerBoundHolds & upperBoundHolds;
	}

	/**
	 * @return The minimum of the valid range of versions covered by this Pattern.
	 */
	public VersionRangeEndPoint getMin() {
		return min;
	}

	/**
	 * @param max the new upper end point of the valid version interval.
	 * @return a Duplicate of the current VersionPattern with the new upper end point.
	 */
	public VersionPattern changeMax(VersionRangeEndPoint max) {
		return new VersionPattern(min, max);
	}

	/**
	 * @param min the new lower end point of the valid version interval.
	 * @return a Duplicate of the current VersionPattern with the new lower end point.
	 */
	public VersionPattern changeMin(VersionRangeEndPoint min) {
		return new VersionPattern(min, max);
	}

	/**
	 *
	 * @param rule
	 * @return
	 */
	public VersionPattern changeRule(MatchRule rule) {
		return new VersionPattern(min, getUpperBoundForRule(min, rule));
	}

	/**
	 * @return The maximum of the valid range of versions covered by this Pattern.
	 */
	public VersionRangeEndPoint getMax() {
		return max;
	}

	/**
	 * @return <code>true</code> if the minimum version (<code>min</code>) is part of the valid
	 * 		versions of this pattern, <code>false</code> otherwise (<code>min</code> is an infimum).
	 */
	public boolean isMinInclusive() {
		return min.isInclusive();
	}

	public char getMinInclusiveChar() {
		return min.getInclusiveChar();
	}

	/**
	 * @return <code>true</code> if the maximum version (<code>max</code>) is part of the valid
	 * 		versions of this pattern, <code>false</code> otherwise (<code>max</code> is a supremum).
	 */
	public boolean isMaxInclusive() {
		return max.isInclusive();
	}

	public char getMaxInclusiveChar() {
		return max.getInclusiveChar();
	}

	/**
	 * sets this VersionPattern to the one corresponding to the string representation in the given
	 * <code>patternString</code>. The given string has to be in the format described in
	 * {@link #toString()}.
	 *
	 * @param patternString the string to parse.
	 */
	private void parsePatternString(String patternString) {
		if (patternString == null || patternString.trim().length() == 0)
			throw new IllegalArgumentException("The given VersionPattern string must not " +
					"be null or empty!");

		patternString = patternString.trim();

		StringTokenizer tokenizer = new StringTokenizer(patternString, String.valueOf(VERSION_SEPARATOR));
		if (tokenizer.countTokens() != 2)
			throw new IllegalArgumentException("The given patternString is unparseable, since the " +
					"separator char '"+VERSION_SEPARATOR+"' is encountered more than once in the given " +
					"String");

		try {
			min = new VersionRangeEndPoint(tokenizer.nextToken());
			max = new VersionRangeEndPoint(tokenizer.nextToken());
		} catch (MalformedVersionException e) {
			throw new RuntimeException("The given string representations of either min or max Versions" +
					" are invalid!", e);
		}
	}

	public static final char VERSION_SEPARATOR = ',';

	/**
	 * @return A String representation of this filter / pattern, which follows this convention:
	 * 	{@link #min} + {@value #VERSION_SEPARATOR} + {@link #max}
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(min.toString());
		buffer.append(VERSION_SEPARATOR);
		buffer.append(max.toString());
		return buffer.toString();
	}

}
