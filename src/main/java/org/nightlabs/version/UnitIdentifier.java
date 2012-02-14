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

/**
 * A UnitIdentifier is used to uniquely identify any unit. It is e.g. used to identify a Plugin or a
 * Feature.
 *
 * <p>Note: This class is immutable!</p>
 *
 * @author Marius Heinzmann marius[at]NightLabs[dot]de
 *
 *	old JDO-Tags:
 *
 * 	identity-type="application"
 *	objectid-class="org.nightlabs.version.UnitIdentifierID"
 *
 * @!jdo.create-objectid-class
 * 	field-order="qualifier, version"
 *
 * jdo.persistence-capable
 *	embedded-only="true"
 * 	detachable="true"
 *
 */
public final class UnitIdentifier
	implements Comparable<UnitIdentifier>, Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * The name of the identified object.
	 */
	private String qualifier;

	/**
	 * The version of the identified object.
	 */
	private Version version;

	/**
	 * The type of element this is identifying.
	 */
	private String elementType;

	/**
	 * The separator used to concatenate the String output of this identifier.
	 */
	public static final String SEPARATOR = "/";

	/**
	 * Constructor used to create a new identifier of an entity (mostly used in the update mechanism).
	 *
	 * @param qualifier A qualifier naming the object which shall be identified.
	 * @param elementType the type of element this identifier describes, e.g. a 'plugin' or 'feature', etc.
	 * @param version The version of the described unit (e.g. ClientRCPFeature,
	 * 	ClientRCPPlugin, etc.).
	 */
	public UnitIdentifier(final String qualifier, String elementType, final Version version)
	{
		assert qualifier != null && qualifier.trim().length() > 0;
		assert elementType != null && elementType.trim().length() > 0;
		assert version != null;

		this.qualifier = qualifier;
		this.elementType = elementType;
		this.version = version;
	}

	/**
	 * The number of parts including the {@link #SEPARATOR}s that make up an UnitIdentifier.
	 */
	private static final int IDENTIFIER_PART_COUNT = 5;
//	private static final int IDENTIFIER_PART_COUNT = 3;

	/**
	 * Parses the given <code>identifier</code> string and if matches the pattern described in
	 * {@link #toString()} creates a corresponding UnitIdentifier object.
	 *
	 * @param identifier a string representation of an UnitIdentifier as described in
	 * 	{@link #toString()}.
	 * @return a UnitIdentifier object corresponding to the given string representation.
	 */
	public static UnitIdentifier parseIdentifier(final String identifier)
	{
		assert identifier != null;
		StringTokenizer tokenizer = new StringTokenizer(identifier, SEPARATOR, true);
		if (tokenizer.countTokens() != IDENTIFIER_PART_COUNT)
			throw new IllegalArgumentException("The given String is no valid UnitIdentifier! Make sure '"+
					SEPARATOR+ "' is not used within the qualifier or the suffix of the version!\n"+
					"given String: '"+identifier+"'");

		String qualifier = tokenizer.nextToken();
		if ( !(SEPARATOR.equals(tokenizer.nextToken())) )
				throw new IllegalArgumentException("The given String is not delimited with '" + SEPARATOR
						+ "' and is therefore no valid UnitIdentifier!");

		String elementType = tokenizer.nextToken();
		if ( !(SEPARATOR.equals(tokenizer.nextToken())) )
			throw new IllegalArgumentException("The given String is not delimited with '" + SEPARATOR
					+ "' and is therefore no valid UnitIdentifier!");

		String versionString = tokenizer.nextToken();
		Version version;
		try {
			version = new Version(versionString);
		} catch (MalformedVersionException e) {
			throw new RuntimeException("The string representation of the given version '"+versionString+
					"' is not valid!", e);
		}

		return new UnitIdentifier(qualifier, elementType, version);
	}

	/**
	 * Compares this <code>UnitIdentifier</code> with an<code>other</code> one according to the
	 * following declaration: <br>
	 * A UnitIdentifier <i>a</i> is smaller than another <i>b</i> if <i>a.qualifier < b.qualifier</i>
	 * according to the lexicographical ordering of {@link String}s or if
	 * <i>a.qualifier == b.qualifier</i> and <i>a.version < b.version</i> according to the ordering
	 * of {@link Version}s.
	 *
	 * @param other The <code>UnitItentifier</code>, with which this one will be compared to.
	 * @return returns a number < 0, 0 or a number > 0, if this identifier is smaller, equal or bigger
	 * 		than the <code>other</code> identifier.
	 */
	public int compareTo(final UnitIdentifier other) {
		int result = qualifier.compareTo(other.qualifier);
		if (result != 0)
			return result;

		return version.compareTo(other.version);
	}

	/**
	 * @param obj the object to compare this identifier with.
	 * @return <code>true</code> if this identifier's qualifier matches the <code>other</code> one's
	 * 	and if the <code>version</code> matches the other one's, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) 											return true;
		if (!(obj instanceof UnitIdentifier)) return false;

		final UnitIdentifier other = (UnitIdentifier) obj;
		if (! elementType.equals(other.elementType) || ! (qualifier.equals(other.qualifier)) )
			return false;

		return version.equals(other.version);
	}

	/**
	 * @return the hashcode of this identifier based on the qualifier and the version.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + qualifier.hashCode();
		result = prime * result + elementType.hashCode();
		result = prime * result + version.hashCode();

		return result;
	}

	/**
	 * @return the version associated to this identifier.
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * @return the name of the identified object.
	 */
	public String getQualifier() {
		return qualifier;
	}

	/**
	 * @return the name of the identified object.
	 */
	public String getElementType() {
		return elementType;
	}

	/**
	 * @param version a Version with which a new UnitIdentifier is created from this one.
	 * @return a new UnitIdentifier with the qualifier of this one and the given version.
	 */
	public UnitIdentifier changeVersion(final Version version)
	{
		return new UnitIdentifier(qualifier, elementType, version);
	}

	/**
	 * @param qualifier a qualifier with which a new UnitIdentifier is created from this one.
	 * @return a new UnitIdentifier with the given qualifier and the version of this one.
	 */
	public UnitIdentifier changeQualifier(final String qualifier)
	{
		return new UnitIdentifier(qualifier, elementType, version);
	}

	/**
	 * @param elementType an element type with which a new UnitIdentifier is created from this one.
	 * @return a new UnitIdentifier with the given elementType, qualifier and the version of this one.
	 */
	public UnitIdentifier changeElementType(final String elementType)
	{
		return new UnitIdentifier(qualifier, elementType, version);
	}

	/**
	 * Returns the String representation of this identifier, which is created as follows: <br>
	 * {@link #qualifier}{@value #SEPARATOR}{@link #elementType}{@value #SEPARATOR}{@link #version}.
	 *
	 * @return the String representation of this identifier.
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(qualifier).append(SEPARATOR).append(elementType).append(SEPARATOR).append(version.toString());
		return buffer.toString();
	}

}
