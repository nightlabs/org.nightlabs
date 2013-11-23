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
 * @version $Revision: 13277 $ - $Date: 2009-01-30 16:35:57 +0100 (Fr, 30 Jan 2009) $
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 */
public class InchUnit
extends Unit
{
	/**
	 * The serial version UID of this class.
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String UNIT_ID = "inch";

//	public InchUnit()
//	{
//		setName(Locale.ENGLISH.getLanguage(), "inch");
//		setName(Locale.GERMAN.getLanguage(), "Zoll");
//		setFactor(1/25.4);
//		setUnitID(UNIT_ID);
//		setUnitSymbol("in");
//	}

	public InchUnit()
	{
		super();
		i18nText.setText(Locale.ENGLISH.getLanguage(), "inch");
		factor = 1 / 25.4;
		unitID = UNIT_ID;
		symbol = "inch";
	}
}
