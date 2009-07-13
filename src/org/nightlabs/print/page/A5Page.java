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

import java.util.Locale;

import org.nightlabs.i18n.unit.MMUnit;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class A5Page
extends PredefinedPage
{
	public static final String PAGE_ID = "A5";
	
//	public A5Page()
//	{
//		super();
//		setName(Locale.ENGLISH.getLanguage(), "DIN A5");
//		setPageID(PAGE_ID);
//		setUnit(new MMUnit());
//		pageWidth = 148;
//		pageHeight = 210;
//	}

	public A5Page()
	{
		super();
		i18nText.setText(Locale.ENGLISH.getLanguage(), "DIN A5");
		pageID = PAGE_ID;
		unit = new MMUnit();
		pageWidth = 148;
		pageHeight = 210;
	}
	
}
