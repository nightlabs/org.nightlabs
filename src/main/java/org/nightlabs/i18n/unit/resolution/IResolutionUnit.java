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
package org.nightlabs.i18n.unit.resolution;

import java.io.Serializable;

import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.unit.IUnit;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public interface IResolutionUnit
extends Serializable
{
	public static final IResolutionUnit dpiUnit = new DPIResolutionUnit();
	
	/**
	 * returns the ID of the resolution unit
	 * @return the ID of the resolution unit
	 */
	String getResolutionID();
	
	/**
	 * return the multilanguage name of the resolution unit
	 * @return the {@link I18nText} of the resolution unit
	 */
	I18nText getName();
	
	/**
	 * sets the name of the resolution unit
	 * @param languageID the languageID
	 * @param text the text for the given langauageID
	 */
	void setName(String languageID, String text);
	
	/**
	 * 
	 * @return the unit
	 */
	IUnit getUnit();
	
}
