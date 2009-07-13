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
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.i18n.unit.IUnit;
import org.nightlabs.i18n.unit.MMUnit;
import org.nightlabs.util.NLLocale;
import org.nightlabs.util.Util;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class PredefinedPage
implements IPredefinedPage
{
	protected PredefinedPage() {
		super();
	}
	
	public PredefinedPage(String pageID, String name, double width, double height, IUnit unit)
	{
		super();
		this.pageID = pageID;
		i18nText.setText(NLLocale.getDefault().getLanguage(), name);
		pageWidth = width;
		pageHeight = height;
		this.unit = unit;
	}
	
	protected String pageID = "A4";
	/**
	 * @see org.nightlabs.print.page.IPredefinedPage#getPageID()
	 */
	public String getPageID() {
		return pageID;
	}
//	/**
//	 * @see org.nightlabs.print.page.IPredefinedPage#setPageID(String)
//	 */
//	public void setPageID(String pageID) {
//		this.pageID = pageID;
//	}
	
	protected I18nTextBuffer i18nText = new I18nTextBuffer();
	/**
	 * @see org.nightlabs.print.page.IPredefinedPage#getName()
	 */
	public I18nText getName() {
		return i18nText;
	}
//	/**
//	 * @see org.nightlabs.print.page.IPredefinedPage#setName(java.lang.String, java.lang.String)
//	 */
//	public void setName(String languageID, String text) {
//		i18nText.setText(languageID, text);
//	}

	protected IUnit unit = new MMUnit();
	/**
	 * by default a PredefinedPage uses a "mm" as unit (MMUnit)
	 * @see org.nightlabs.print.page.IPredefinedPage#getUnit()
	 */
	public IUnit getUnit() {
		return unit;
	}
	
//	/**
//	 * sets the Unit for this Predefined page, automatically converts the width
//	 * and height if the unit changes
//	 *
//	 * @param unit the Unit to set for this page preset
//	 * @see org.nightlabs.print.page.IPredefinedPage#setUnit(IUnit)
//	 */
//	public void setUnit(IUnit unit)
//	{
//		this.unit = unit;
//
//		pageWidth = pageWidth * unit.getFactor();
//		pageHeight = pageHeight * unit.getFactor();
//	}
	
	protected double pageWidth = 210;
	/**
	 * the pageWidth automatically is converted if the unit changes
	 * by default the unit is mm and the default width is 100
	 * @see org.nightlabs.print.page.IPredefinedPage#getPageWidth()
	 */
	public double getPageWidth() {
		return pageWidth;
	}

	protected double pageHeight = 297;
	/**
	 * the pageheight automatically is converted if the unit changes
	 * by default the unit is mm and the default height is 100
	 * 
	 * @see org.nightlabs.print.page.IPredefinedPage#getPageHeight()
	 */
	public double getPageHeight() {
		return pageHeight;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;

		if (!(obj instanceof IPredefinedPage))
				return false;

		IPredefinedPage page = (IPredefinedPage) obj;
		if (page.getPageID().equals(getPageID()))
			return true;
		
		return false;
	}

	@Override
	public int hashCode() {
		return Util.hashCode(pageID);
	}
	
}
