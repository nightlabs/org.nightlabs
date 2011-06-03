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

import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.util.NLLocale;
import org.nightlabs.util.Util;

/**
 * @version $Revision: 13277 $ - $Date: 2009-01-30 16:35:57 +0100 (Fr, 30 Jan 2009) $
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 */
public class Unit
implements IUnit
{
	/**
	 * The serial version UID of this class.
	 */
	private static final long serialVersionUID = 1L;
	
	protected Unit() {
		super();
	}

	public Unit(String unitID, String name, String unitSymbol, double factor) {
		super();
		this.unitID = unitID;
		i18nText.setText(NLLocale.getDefault().getLanguage(), name);
		this.symbol = unitSymbol;
		this.factor = factor;
	}

	// FIXME: why is this the default value for the base class?
	protected String unitID = "millimetre";
	// FIXME: why is this the default value for the base class?
	protected String symbol = "mm";
	protected I18nText i18nText = new I18nTextBuffer();
	protected double factor = 1;

	/* (non-Javadoc)
	 * @see org.nightlabs.i18n.unit.IUnit#getUnitID()
	 */
	public String getUnitID() {
		return unitID;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.i18n.unit.IUnit#getUnitSymbol()
	 */
	public String getUnitSymbol() {
		return symbol;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.i18n.unit.IUnit#getName()
	 */
	public I18nText getName() {
		return i18nText;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.i18n.unit.IUnit#getFactor()
	 */
	public double getFactor() {
		return factor;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;

		if (!(obj instanceof IUnit))
				return false;

		IUnit u = (IUnit) obj;
		if (u.getUnitID().equals(getUnitID()))
			return true;

		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return Util.hashCode(unitID);
	}
}
