/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/
package org.nightlabs.editor2d.unit;

import org.nightlabs.i18n.unit.ResolutionUnit;
import org.nightlabs.i18n.unit.resolution.Resolution;

/**
 * Represents the native measurement of the screen resolution
 * e.g. with a screen resolution of 72 DPI one pixel is 1/72 inch
 * 
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 */
public class PixelUnit
extends ResolutionUnit
{
	public static final String UNIT_ID = "pixel";
	
	public PixelUnit(Resolution resolution) {
		super(resolution);
	}

	@Override
	protected String initName() {
		return "pixel";
	}

	@Override
	protected String initUnitID() {
		return UNIT_ID;
	}

	@Override
	protected String initUnitSymbol() {
		return "px";
	}
	
}
