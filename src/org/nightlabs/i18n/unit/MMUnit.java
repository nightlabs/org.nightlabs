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

import java.util.Locale;


/**
 * @version $Revision$ - $Date$
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 */
public class MMUnit
extends Unit
{
	/**
	 * The serial version UID of this class.
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String UNIT_ID = "millimetre";

//	public MMUnit() {
//		setName(Locale.ENGLISH.getLanguage(), "mm");
//		setFactor(1);
//		setUnitSymbol("mm");
//		setUnitID(UNIT_ID);
//	}

	public MMUnit()
	{
		super();
		i18nText.setText(Locale.ENGLISH.getLanguage(), "mm");
		factor = 1;
		symbol = "mm";
		unitID = UNIT_ID;
	}

}
