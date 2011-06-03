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

import java.io.Serializable;


/**
 * This Interface defines a resolution (e.g. for a document)
 * 
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public interface Resolution
extends Cloneable, Serializable
{
	public static final int DEFAULT_RESOLUTION_DPI = 762; // == 300 DPCM
	public static final String DEFAULT_RESOLUTION_UNIT_ID = DPIResolutionUnit.RESOLUTION_ID;
	
	/**
	 * Get the the resolution unit (e.g. dpi or dpcm)
	 * @return the resolution unit
	 * @see IResolutionUnit
	 */
	IResolutionUnit getResolutionUnit();
	
	/**
	 * sets the resolution unit (e.g. dpi or dpcm)
	 * 
	 * if the resolution unit changes the value (getResolution()) is
	 * converted automatically
	 * @param unit the resolution unit to set
	 * @see IResolutionUnit
	 */
	void setResolutionUnit(IResolutionUnit unit);
	
	/**
	 * returns the resolution in X for the given resolution unit
	 * @return the resolution in X for the given unit
	 * @see #getResolutionUnit()
	 */
	double getResolutionX();

	/**
	 * returns the resolution in Y for the given resolution unit
	 * @return the resolution in Y for the given unit
	 * @see #getResolutionUnit()
	 */
	double getResolutionY();
	
	/**
	 * returns the resolution in X for the given {@link IResolutionUnit}
	 * 
	 * @param unit the resolutionUnit to get the corresponding resolution for
	 * @return the resolution in X for the given resolutionUnit
	 */
	double getResolutionX(IResolutionUnit unit);

	/**
	 * returns the resolution in Y for the given {@link IResolutionUnit}
	 * 
	 * @param unit the resolutionUnit to get the corresponding resolution for
	 * @return the resolution in Y for the given resolutionUnit
	 */
	double getResolutionY(IResolutionUnit unit);
		
	/**
	 * sets the resolution in X for the given resolution unit
	 * @param value the resolution in X to set for the given resolution unit
	 * @see #getResolutionUnit()
	 */
	void setResolutionX(double value);

	/**
	 * sets the resolution in Y for the given resolution unit
	 * @param value the resolution in Y to set for the given resolution unit
	 * @see #getResolutionUnit()
	 */
	void setResolutionY(double value);
	
	/**
	 * return a cloned Resolution
	 * @return a clone
	 */
	public Object clone();
}
