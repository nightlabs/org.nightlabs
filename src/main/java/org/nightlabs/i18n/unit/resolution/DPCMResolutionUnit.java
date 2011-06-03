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

import java.util.Locale;

import org.nightlabs.i18n.unit.CMUnit;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 */
public class DPCMResolutionUnit
extends ResolutionUnit
{
	/**
	 * The serial version of this class.
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String RESOLUTION_ID = "DPCM";
	
	public DPCMResolutionUnit()
	{
		super();
		setName(Locale.ENGLISH.getLanguage(), "DPCM");
//		setFactor(1);
		setUnit(new CMUnit());
		setResolutionID(RESOLUTION_ID);
	}

//	/**
//	 * @see org.nightlabs.editor2d.resolution.IResolutionUnit#getFactor()
//	 */
//	public double getFactor() {
//		return 1;
//	}

}
