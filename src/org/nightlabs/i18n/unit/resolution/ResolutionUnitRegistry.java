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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.nightlabs.i18n.unit.IUnit;
import org.nightlabs.print.page.IPredefinedPage;

/**
 * This class is registry where you can register measurement units of type {@link IUnit}
 * (e.g. mm, cm, inch) and resolution units of type {@link IResolutionUnit} (e.g. dpi or dpcm)
 * and predefined sizes of pages of type {@link IPredefinedPage} (e.g. DIN A4)
 * 
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 */
public class ResolutionUnitRegistry
{
	protected static ResolutionUnitRegistry resolutionUnitRegistry = null;
	/**
	 * 
	 * @return a static/shared instance of this class
	 */
	public static ResolutionUnitRegistry sharedInstance()
	{
		if (resolutionUnitRegistry == null) {
			resolutionUnitRegistry = new ResolutionUnitRegistry();
		}
		return resolutionUnitRegistry;
	}
	
	protected ResolutionUnitRegistry()
	{
		super();
		init();
	}
	
	protected void init()
	{
		addResolutionUnit(new DPIResolutionUnit());
	}
	
	private Map<String, IResolutionUnit> resolutionUnitID2resolutionUnit = new HashMap<String, IResolutionUnit>();
	/**
	 * adds a resolution unit
	 * @param unit the resolution unit to add
	 * @see IResolutionUnit
	 */
	public void addResolutionUnit(IResolutionUnit unit)
	{
		if (unit == null)
			throw new IllegalArgumentException("param unit must not be null!");
	
		resolutionUnitID2resolutionUnit.put(unit.getResolutionID(), unit);
	}
	/**
	 * removes a resolution unit
	 * @param unit the resolution unit to remove
	 * @see IResolutionUnit
	 */
	public void removeResolutionUnit(IResolutionUnit unit)
	{
		if (unit == null)
			throw new IllegalArgumentException("param unit must not be null!");
	
		resolutionUnitID2resolutionUnit.remove(unit.getResolutionID());
	}
	/**
	 * returns all registered/added resolution units
	 * @return all registered/added resolution units
	 */
	public Collection<IResolutionUnit> getResolutionUnits() {
		return Collections.unmodifiableCollection(resolutionUnitID2resolutionUnit.values());
	}

	/**
	 * returns the IResolutionUnit with given resolutionID
	 * @param resolutionID the resolutionID of
	 * @return the IResolutionUnit with given resolutionID
	 */
	public IResolutionUnit getResolutionUnit(String resolutionID) {
		return resolutionUnitID2resolutionUnit.get(resolutionID);
	}

	/**
	 * 
	 * @return a collection containing all resolutionIDs of all registered/added {@link IResolutionUnit}s
	 */
	public Collection<String> getResolutionIDs() {
		return Collections.unmodifiableCollection(resolutionUnitID2resolutionUnit.keySet());
	}
}
