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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nightlabs.util.Util;

/**
 * This class is used to <code>version</code> the client plugins of JFire and may be used to act
 * as a version identifier in any project.
 *
 * The pattern a version <code>String</code> is:
 * <code>major</code>{@value #SEPARATOR}<code>minor</code>{@value #SEPARATOR}
 * <code>release</code>[{@value #SEPARATOR}<code>patchLevel</code>
 * [{@value #SEPARATOR}<code>suffix</code>]] <br>
 * The suffix may consist of 'a-zA-Z0-9#$!_-'. Additionally the following version string is
 * also valid: '<pre>1.2.3.test</pre>'.
 *
 * <p>Examples of valid versions are:
 * <ul>
 * 	<li><pre>1.2.3</pre></li>
 * 	<li><pre>1.2.3.4</pre></li>
 * 	<li><pre>1.2.3.4.test</pre></li>
 * 	<li><pre>1.2.3.t_e!st</pre></li>
 * </ul>
 * </p>
 *
 *
 * <p>Note: This class is immutable!</p>
 *
 * @author Marius Heinzmann marius[AT]NightLabs[DOT]de
 * @author Marco Schulze marco@nightlabs.de
 * @author Niklas Schiffler <nick@nightlabs.de>
 *
 * Old JDO Tags:
 *
 * 	identity-type="application"
 *	objectid-class="org.nightlabs.version.VersionID"
 *
 * @!jdo.create-objectid-class
 * 	field-order="major, minor, release, patchLevel, suffix"
 *
 * jdo.persistence-capable
 *	embedded-only="true"
 * 	detachable="true"
 *
 */
public class Version implements Comparable<Version>, Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * The minimum Version that can exist.
	 */
	public static final Version MIN_VERSION = new Version(0,0,0,0);

	/**
	 * The maximum Version possible.
	 */
	public static final Version MAX_VERSION = new Version(Integer.MAX_VALUE, Integer.MAX_VALUE,
			Integer.MAX_VALUE, Integer.MAX_VALUE);

	/**
	 * @jdo.field
	 *	persistence-modifier="persistent"
	 */
	private int major;
	/**
	 * @jdo.field
	 *	persistence-modifier="persistent"
	 */
	private int minor;
	/**
	 * @jdo.field
	 *	persistence-modifier="persistent"
	 */
	private int release;
	/**
	 * @jdo.field
	 *	persistence-modifier="persistent"
	 */
	private int patchLevel;
	/**
	 * @jdo.field
	 *	persistence-modifier="persistent"
	 */
	private String suffix;

	private static final String SEPARATOR = ".";
	protected static final String EMPTYSTRING = "";

	/**
	 * @deprecated only for JDO!
	 */
	@Deprecated
	protected Version() {}

	/**
	 *
	 * @param versionStr
	 * @throws MalformedVersionException
	 */
	public Version(String versionStr)
	throws MalformedVersionException
	{
		if (versionStr == null || versionStr.length() == 0)
			throw new IllegalArgumentException("versionStr must NEITHER be null NOR empty!");

		parseVersion(versionStr);
		validate();
	}

	/**
	 * @param major
	 * @param minor
	 * @param release
	 * @param patchLevel
	 */
	public Version(int major, int minor, int release, int patchLevel) {
		this(major, minor, release, patchLevel, null);
	}

	/**
	 * @param major
	 * @param minor
	 * @param release
	 * @param patchLevel
	 * @param suffix
	 */
	public Version(int major, int minor, int release, int patchLevel, String suffix) {
		this.major = major;
		this.minor = minor;
		this.release = release;
		this.patchLevel = patchLevel;
		if (suffix == null)
			suffix = EMPTYSTRING;
//		TODO: should we limit the length of a suffix?? There is no need to, but it might become a
//					Problem when persisting in the DB.
//		else if (suffix.length() > MAX_STRING.length())
//			throw new IllegalArgumentException("The given suffix is too long! It is allowed to have a " +
//					"maximum of "+MAX_STRING.length()+" characters!");

		this.suffix = suffix;
		validate();
	}

	/**
	 * A regex that is used to check the suffix for valid characters.
	 */
	public static Pattern validityCheck = null;

	private void validate() {
		if (major < 0)
			throw new IllegalArgumentException("Negative major versions are invalid!");
		if (minor < 0)
			throw new IllegalArgumentException("Negative minor versions are invalid!");
		if (release < 0)
			throw new IllegalArgumentException("Negative release versions are invalid!");
		if (patchLevel < 0)
			throw new IllegalArgumentException("Negative patchlevels are invalid!");

		if (suffix == null || suffix.length() == 0)
			return;

		// This omits parsing problems of other classes which may as well be represented as strings
		// and have other separators which may be used inside this suffix.
		if (validityCheck == null)
			validityCheck = Pattern.compile("[\\w#\\$!\\-]*");

		 if (! validityCheck.matcher(suffix).matches())
			 throw new IllegalArgumentException("The suffix contains illegal characters! Suffix='"
					 +suffix+"'. Legal characters ='a-zA-Z0-9#$!_-'.");
	}

	/**
	 * @return Returns the major.
	 */
	public int getMajor() {
		return major;
	}

	/**
	 * @param major The major with which to create a new Version.
	 * @return A copy of this Version except for the given new major.
	 */
	public Version changeMajor(int major) {
		return new Version(major, minor, release, patchLevel, suffix);
	}

	/**
	 * @return Returns the minor.
	 */
	public int getMinor() {
		return minor;
	}

	/**
	 * @param minor The minor with which to create a new Version.
	 * @return A copy of this Version except for the given new minor.
	 */
	public Version changeMinor(int minor) {
		return new Version(major, minor, release, patchLevel, suffix);
	}

	/**
	 * @return Returns the patchLevel.
	 */
	public int getPatchLevel() {
		return patchLevel;
	}

	/**
	 * @param patchLevel The patchLevel with which to create a new Version.
	 * @return A copy of this Version except for the given new patchLevel.
	 */
	public Version changePatchLevel(int patchLevel) {
		return new Version(major, minor, release, patchLevel, suffix);
	}

	/**
	 * @return Returns the release.
	 */
	public int getRelease() {
		return release;
	}

	/**
	 * @param release The <code>release</code> with which to create a new Version.
	 * @return A copy of this Version except for the given new <code>release</code>.
	 */
	public Version changeRelease(int release) {
		return new Version(major, minor, release, patchLevel, suffix);
	}

	/**
	 * @return Returns the suffix.
	 */
	public String getSuffix() {
		return suffix;
	}

	/**
	 * @param suffix The <code>suffix</code> with which to create a new Version.
	 * @return A copy of this Version except for the given new <code>suffix</code>.
	 */
	public Version changeSuffix(String suffix) {
		return new Version(major, minor, release, patchLevel, suffix);
	}

	/**
	 * Returns the String representation of this version, which is defined as:
 * <code>major</code>{@value #SEPARATOR}<code>minor</code>{@value #SEPARATOR}
 * <code>release</code>[{@value #SEPARATOR}<code>patchLevel</code>
 * [{@value #SEPARATOR}<code>suffix</code>]] <br>
 * The suffix may consist of 'a-zA-Z0-9#$!_-'. Additionally the following version string is
 * also valid: '<pre>1.2.3.test</pre>'.
	 *
	 * @return the <code>String</code> representation of this version.
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(major);
		sb.append(SEPARATOR);
		sb.append(minor);
		sb.append(SEPARATOR);
		sb.append(release);
		sb.append(SEPARATOR);
		sb.append(patchLevel);
		if (suffix != null && !EMPTYSTRING.equals(suffix)) {
			sb.append(SEPARATOR);
			sb.append(suffix);
		}
		return sb.toString();
	}

	/**
	 * Creates a Version out of its String ({@link #toString()}) representation. The String should
	 * conform to the following pattern:
	 * <code>major</code>{@value #SEPARATOR}<code>minor</code>{@value #SEPARATOR}
	 * <code>release</code>{@value #SEPARATOR}<code>patchLevel</code>
	 * {@value #SEPARATOR}<code>suffix</code>.
	 *
	 * @param version the {@link #toString()} representation of a version.
	 * @throws MalformedVersionException if the given <code>String</code> does not conform to the
	 * 	pattern described.
	 */
	private void parseVersion(String version)
		throws MalformedVersionException
	{
		Pattern versionPattern = Pattern.compile(
//		The whole rexexp not escaped
//			(\d+)\.(\d+)\.(\d+)(?:[-\.](?:(\d+)|([\w#\$!\-]+)|(?:(\d+)[-\.]([\w#\$!\-]+))))?\s*\z
				"(\\d+)\\.(\\d+)\\.(\\d+)(?:[-\\.](?:(\\d+)|([\\w#\\$!\\-]+)|(?:(\\d+)[-\\.]([\\w#\\$!\\-]+))))?\\s*\\z");
//		EOI = end of input; OPT(..) = optional; | = or
//        major  . minor  . release OPT( ./- (patchLevel  | suffix | patchLevel ./- suffix)) whitespace* EOI
//		the endofinput prohibits the parsing of '1.7.0-9-asf.narf' which would otherwise be read as
//		'1.7.0-9-asf'.
		Matcher m = versionPattern.matcher(version);
		if(m.find())
		{
			this.major = Integer.parseInt(m.group(1));
			this.minor = Integer.parseInt(m.group(2));
			this.release = Integer.parseInt(m.group(3));

			// Assumption is that if the element in the 4th group is parseable as an int -> it is an int.
			// So given the string '1.2.3.02' the '02' is interpreted as an int, not a string!
			if (m.group(4) != null)
			{
				try
				{
//				assume we got a small JFire version without suffix.
					this.patchLevel = Integer.parseInt(m.group(4));
					this.suffix = "";
				}
				catch (NumberFormatException nfe)
				{
//				since the number is too big -> it must be a suffix consisting only of numbers
					this.patchLevel = 0;
					this.suffix = m.group(4);
				}
			}
			else if (m.group(5) != null)
			{
//			assume that we are given an osgi-version -> group(5) = suffix
				this.patchLevel = 0;
				this.suffix = m.group(5);
			}
			else if (m.group(6) != null && m.group(7) != null)
			{
//				assume we have a full JFire version
				try {
					this.patchLevel = Integer.parseInt(m.group(6));
					this.suffix = m.group(7);
				} catch (NumberFormatException nfe2) {
					throw new MalformedVersionException("The patchlevel has to be a parseable number! " +
							"given patchlevel: '"+m.group(6)+"' given encoded version: '"+version+"'");
				}
			}
			else
			{
				// assume we have only major.minor.release
				for (int i = 4; i < 8; i++)
				{
					if (m.group(i) != null)
						throw new MalformedVersionException("Malformed version string: " + version);
				}
			}
		}
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof Version)) return false;

		final Version otherVersion = (Version) other;

		return major == otherVersion.major
				&& minor == otherVersion.minor
				&& release == otherVersion.release
				&& patchLevel == otherVersion.patchLevel
				&& suffix.equals(otherVersion.suffix);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = result * PRIME + major;
		result = result * PRIME + minor;
		result = result * PRIME + release;
		result = result * PRIME + patchLevel;
		result = result * PRIME + Util.hashCode(suffix);
		return result;
	}

	/**
	 * Compares this <code>Version</code> object to another one.
	 *
	 * <p>
	 * A version is considered to be <b>less than </b> another version if its major component is less
	 * than the other version's major component, or the major components are equal and its minor
	 * component is less than the other version's minor component, or the major and minor components
	 * are equal and its release component is less than the other version's release component.
	 * The suffix is not relevant for the natural ordering of Versions, though for equality.
	 * <p>
	 * Note: Given two version <code>v1,v2</code> and <code>v1.compareTo(Version v2) = 0</code>,
	 * this does not imply that <code>v1</code> and <code>v2</code> are equal according to equals!
	 * </p>
	 *
	 * <p>
	 * A version is considered to be <b>equal to</b> another version if the
	 * major, minor and micro components are equal and the qualifier component
	 * is equal (see {@link #equals(Object)}).
	 * </p>
	 *
	 * @param other The <code>Version</code> object to be compared.
	 * @return A negative integer, zero, or a positive integer if this object is
	 *         less than, equal to, or greater than the specified
	 *         <code>Version</code> object.
	 */
	public int compareTo(Version other) {
		if (this == other)
			return 0;

		int difference = major - other.major;
		if (difference != 0)
			return difference;

		difference = minor - other.minor;
		if (difference != 0)
			return difference;

		difference = release - other.release;
		if (difference != 0)
			return difference;

		return patchLevel - other.patchLevel;
	}

}
