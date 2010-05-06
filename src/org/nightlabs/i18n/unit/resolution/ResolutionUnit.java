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

import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.i18n.unit.IUnit;
import org.nightlabs.util.NLLocale;

/**
 * This is a Unit which defines a resolution (e.g. DPI or DPCM)
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 */
public class ResolutionUnit
implements IResolutionUnit
{
	/**
	 * The serial version of this class.
	 */
	private static final long serialVersionUID = 1L;

	protected ResolutionUnit() {
		super();
	}

	public ResolutionUnit(String resolutionID, String name, IUnit unit) {
		super();
		this.resolutionID = resolutionID;
		setName(NLLocale.getDefault().getLanguage(), name);
		setUnit(unit);
	}
	
	private String resolutionID = "dpcm";
	
	/* (non-Javadoc)
	 * @see org.nightlabs.i18n.unit.resolution.IResolutionUnit#getResolutionID()
	 */
	public String getResolutionID() {
		return resolutionID;
	}
	
	/**
	 * Set the resolution id.
	 * @param resolutionID The resulution id to set
	 */
	protected void setResolutionID(String resolutionID) {
		this.resolutionID = resolutionID;
	}
	
	private I18nText i18nText = new I18nTextBuffer();
	
	/* (non-Javadoc)
	 * @see org.nightlabs.i18n.unit.resolution.IResolutionUnit#getName()
	 */
	public I18nText getName() {
		return i18nText;
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.i18n.unit.resolution.IResolutionUnit#setName(java.lang.String, java.lang.String)
	 */
	public void setName(String languageID, String text) {
		i18nText.setText(languageID, text);
	}

	private IUnit unit = null;
	
	/* (non-Javadoc)
	 * @see org.nightlabs.i18n.unit.resolution.IResolutionUnit#getUnit()
	 */
	public IUnit getUnit() {
		return unit;
	}
	
	protected void setUnit(IUnit unit) {
		this.unit = unit;
	}
	
}
