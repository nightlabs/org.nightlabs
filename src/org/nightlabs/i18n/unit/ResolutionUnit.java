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

import org.nightlabs.i18n.unit.resolution.Resolution;


/**
 * This the is the abstract base for {@link IUnit}s which do not have an definite representation
 * but are depended on a certain resolution
 *
 * @version $Revision: 13277 $ - $Date: 2009-01-30 16:35:57 +0100 (Fr, 30 Jan 2009) $
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 */
public abstract class ResolutionUnit
extends Unit
{
	/**
	 * The serial version UID of this class.
	 */
	private static final long serialVersionUID = 1L;
	
	public ResolutionUnit(Resolution resolution)
	{
//		setName(NLLocale.getDefault().getLanguage(), initName());
		getName().setText(Locale.ENGLISH.getLanguage(), initName());
		unitID = initUnitID();
		symbol = initUnitSymbol();
		setResolution(resolution);
	}

	protected abstract String initName();
	protected abstract String initUnitID();
	protected abstract String initUnitSymbol();

	protected Resolution resolution = null;
	public void setResolution(Resolution resolution)
	{
		IUnit unit = resolution.getResolutionUnit().getUnit();
		double resolutionX = resolution.getResolutionX();
		double resolutionY = resolution.getResolutionY();
		if (resolutionX != resolutionY)
			throw new IllegalArgumentException("The Resolution must have identical resolutions for X and Y!");

		this.resolution = resolution;
		double unitFactor = unit.getFactor();
		double factor = resolutionX * unitFactor;
//		setFactor(factor);
		this.factor = factor;
	}

	public Resolution getResolution() {
		return resolution;
	}
}
