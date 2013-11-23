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
package org.nightlabs.print.page;

import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.unit.IUnit;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public interface IPredefinedPage
{
	/**
	 * return the id of the page (ISO 216, e.g. A4)
	 * @return the ISO 216 standard paper size name of Predefined page
	 * @see <a href="http://en.wikipedia.org/wiki/ISO_216">Wikipedia about ISO_216</a>
	 */
	String getPageID();
	
//	/**
//	 * sets the id of the page (ISO 216, e.g. A4)
//	 * @param pageID the id of the page to set
//	 * @see <a href="http://en.wikipedia.org/wiki/ISO_216">Wikipedia about ISO_216</a>
//	 */
//	void setPageID(String pageID);
	
	/**
	 * returns the width of the predefined page in the given unit
	 * @return the width of the predefined page in the given unit
	 */
	double getPageWidth();
	
	/**
	 * returns the height of the predefined page in the given unit
	 * @return the height of the predefined page in the given unit
	 */
	double getPageHeight();
	
	/**
	 * returns the multilanguage name of the page
	 * @return the multilanguage name of the page
	 * @see org.nightlabs.i18n.I18nText
	 */
	I18nText getName();
	
//	/**
//	 *
//	 * @param languageID the languageID (e.g. Locale.ENGLISH.getLanguage())
//	 * @param text the name for the corresponding languageID
//	 * @see org.nightlabs.i18n.I18nText
//	 */
//	void setName(String languageID, String text);
	
	/**
	 * returns the Unit for the page
	 * @return the unit (e.g. mm) for the page
	 * @see IUnit
	 */
	IUnit getUnit();
	
//	/**
//	 * sets the Unit for the page (e.g. mm)
//	 * @param unit the unit to set
//	 * @see IUnit
//	 */
//	void setUnit(IUnit unit);
	
	/**
	 * This method checks whether the other object implements <code>IPredefinedPage</code>
	 * and then compares solely the pageID as returned by {@link #getPageID()},
	 * because this property is the primary key of an <code>IPredefinedPage</code>.
	 *
	 * @param obj Any other object. If it does not implement <code>IPredefinedPage</code>,
	 *		this method will always return <code>false</code>.
	 * @return Returns true if the unique ID as returned by {@link #getPageID()} is equal.
	 */
	boolean equals(Object o);
	
	/**
	 * This method calculates the hashCode of an <code>IPredefinedPage</code> by solely using
	 * the unique identifier as returned by {@link #getPageID()}.
	 *
	 * @return Returns the hash code.
	 */
	int hashCode();
}
