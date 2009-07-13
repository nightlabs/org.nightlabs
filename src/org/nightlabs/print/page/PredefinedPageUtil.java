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

import java.awt.Dimension;
import java.awt.Rectangle;

import org.nightlabs.i18n.unit.IUnit;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class PredefinedPageUtil
{
	public static Rectangle getPageBounds(IUnit unit, IPredefinedPage page)
	{
		Dimension dimension = getPageSize(unit, page);
		return new Rectangle(0, 0, dimension.width, dimension.height);
	}
	
	public static Dimension getPageSize(IUnit unit, IPredefinedPage page)
	{
		IUnit pageUnit = page.getUnit();
		double factor = unit.getFactor();
//		double pageHeight = page.getPageHeight() * pageUnit.getFactor();
//		double pageWidth = page.getPageWidth() * pageUnit.getFactor();
		double pageHeight = page.getPageHeight() / pageUnit.getFactor();
		double pageWidth = page.getPageWidth() / pageUnit.getFactor();
		
		if (factor != pageUnit.getFactor()) {
			pageWidth = pageWidth * factor;
			pageHeight = pageHeight * factor;
		}
		Dimension dimension = new Dimension();
		dimension.setSize(pageWidth, pageHeight);
		return dimension;
	}
}
