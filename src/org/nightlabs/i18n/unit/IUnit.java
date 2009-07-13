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
package org.nightlabs.i18n.unit;

import java.io.Serializable;

import org.nightlabs.i18n.I18nText;

/**
 * @version $Revision$ - $Date$
 */
public interface IUnit extends Serializable
{
	/**
	 * @return the ISO 31-1 unique name of a measurement unit.
	 * @see <a href="http://en.wikipedia.org/wiki/ISO_31-1">Wikipedia about ISO_31-1</a>
	 */
	String getUnitID();

//	/**
//	 * sets the ISO 31-1 unique name of a measurement unit.
//	 * (e.g. metre)
//	 *
//	 * @see <a href="http://en.wikipedia.org/wiki/ISO_31-1">Wikipedia about ISO_31-1</a>
//	 * @param unitID the unitID to set
//	 */
//	void setUnitID(String unitID);

	/**
	 * returns the symbol of the measurement unit (e.g. "m" for "metre")
	 * @return the (optional) Symbol of a ISO 31-1 measurement unit
	 * @see <a href="http://en.wikipedia.org/wiki/ISO_31-1">Wikipedia about ISO_31-1</a>
	 */
	String getUnitSymbol();

//	/**
//	 * sets the ISO 31-1 symbol of the measurement unit (e.g. "m" for "metre")
//	 * @param unitSymbol the symbol of the measurement unit to set
//	 * @see <a href="http://en.wikipedia.org/wiki/ISO_31-1">Wikipedia about ISO_31-1</a>
//	 */
//	void setUnitSymbol(String unitSymbol);

	/**
	 * returns the multilanguage name of the unit
	 * @return the multilanguage name of the unit
	 * @see org.nightlabs.i18n.I18nText
	 */
	I18nText getName();

	/**
//	 * sets the name of the unit for a languageID
//	 * @param languageID the languageID (e.g. Locale.ENGLISH.getLanguage())
//	 * @param text the name for the corresponding languageID
//	 * @see org.nightlabs.i18n.I18nText
//	 */
//	void setName(String languageID, String text);

	/**
	 * returns the calculation factor for transforming a value from the
	 * standard unit (mm) into this one. This means {@link IUnit#getFactor()}
	 * x 1 mm is one
	 *
	 * @return the calculation factor for transforming a value from the
	 * standard unit (mm) into this one.
	 */
	double getFactor();

//	/**
//	 * sets the calculation factor for transforming a value from the
//	 * standard unit (mm) into this one
//	 * @param factor the calculation factor to transform this unit into mm
//	 */
//	void setFactor(double factor);

	/**
	 * This method checks whether the other object implements <code>IUnit</code>
	 * and then compares solely the unitID as returned by {@link #getUnitID()},
	 * because this property is the primary key of an <code>IUnit</code>.
	 *
	 * @param obj Any other object. If it does not implement <code>IUnit</code>,
	 *		this method will always return <code>false</code>.
	 * @return Returns true if the unique ID as returned by {@link #getUnitID()} is equal.
	 */
	boolean equals(Object obj);

	/**
	 * This method calculates the hashCode of an <code>IUnit</code> by solely using
	 * the unique identifier as returned by {@link #getUnitID()}.
	 *
	 * @return Returns the hash code.
	 */
	int hashCode();
}
