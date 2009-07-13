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

import org.nightlabs.i18n.unit.resolution.IResolutionUnit;
import org.nightlabs.i18n.unit.resolution.ResolutionImpl;

/**
 * @version $Revision$ - $Date$
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 */
public class DefaultScreenUnit
extends ResolutionUnit
{
	/**
	 * The serial version UID of this class.
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String UNIT_ID = "defaultScreenUnit";

	public DefaultScreenUnit() {
		super(new ResolutionImpl(IResolutionUnit.dpiUnit, 72));
	}

	@Override
	protected String initName() {
		return "ScreenUnit";
	}

	@Override
	protected String initUnitID() {
		return UNIT_ID;
	}

	@Override
	protected String initUnitSymbol() {
		return "72dpi";
	}

}
