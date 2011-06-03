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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


/**
 * @version $Revision: 12386 $ - $Date: 2008-10-10 14:42:28 +0200 (Fr, 10 Okt 2008) $
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 */
public class UnitRegistry
implements UnitConstants
{
	protected UnitRegistry()
	{
		super();
		addUnit(new MMUnit(), GLOBAL_CONTEXT);
		addUnit(new CMUnit(), GLOBAL_CONTEXT);
		addUnit(new InchUnit(), GLOBAL_CONTEXT);
		addUnit(new DefaultScreenUnit(), DEVICE_CONTEXT);
	}

	private static UnitRegistry unitRegistry = null;
	public static UnitRegistry sharedInstance() {
		if (unitRegistry == null) {
			unitRegistry = new UnitRegistry();
		}
		return unitRegistry;
	}

	private Map<String, Set<IUnit>> context2Units = null;
	private Map<String, Set<IUnit>> getContext2Units()
	{
		if (context2Units == null) {
			context2Units = new HashMap<String, Set<IUnit>>();
		}
		return context2Units;
	}

	private Map<String, IUnit> unitID2unit = null;
	private Map<String, IUnit> getUnitID2Unit() {
		if (unitID2unit == null)
			unitID2unit = new HashMap<String, IUnit>();
		return unitID2unit;
	}

	/**
	 * adds a {@link IUnit} to the given context
	 *
	 * @param unit the {@link IUnit} to add
	 * @param context the context to which the unit should be added,
	 * use {@link UnitRegistry#GLOBAL_CONTEXT} when the unit should be added global
	 */
	public void addUnit(IUnit unit, String context)
	{
		if (unit == null)
			throw new IllegalArgumentException("Param unit must not be null!");

		if (context == null)
			throw new IllegalArgumentException("Param context must not be null!");

		Set<IUnit> units = getContext2Units().get(context);
		if (units == null) {
			units = new LinkedHashSet<IUnit>();
			units.add(unit);
			getContext2Units().put(context, units);
		}
		else {
			units.add(unit);
		}

		getUnitID2Unit().put(unit.getUnitID(), unit);
	}

	/**
	 * removes a {@link IUnit} from the given context
	 * @param unit the {@link IUnit} to remove
	 * @param context the context from which the unit should be removed
	 */
	public void removeUnit(IUnit unit, String context)
	{
		Set<IUnit> units = getContext2Units().get(context);
		if (units != null) {
			units.remove(unit);
		}
		if (unit != null)
			getUnitID2Unit().remove(unit.getUnitID());
	}

	/**
	 * returns all global registered units
	 * @return a {@link Set} which contains all unit which have been added to the global context
	 * @see UnitRegistry#GLOBAL_CONTEXT
	 */
	public Set<IUnit> getGlobalUnits()
	{
		return getContext2Units().get(GLOBAL_CONTEXT);
	}

	/**
	 * returns all units which have been added to the given context and dependend on the boolean
	 * <code>global</code> if the global added units hould be contained as well.
	 *
	 * @param context the context to which the units have been added
	 * @param global determines if additional to the context units also the global units should be contained
	 * @return a {@link Set} containing all {@link IUnit}s which have been added to the given context
	 */
	public Set<IUnit> getUnits(String context, boolean global)
	{
		Set<IUnit> contextUnits = getContext2Units().get(context);
		if (!global) {
			return contextUnits;
		} else {
			Set<IUnit> allUnits = new LinkedHashSet<IUnit>(getGlobalUnits());
			if (contextUnits != null)
				allUnits.addAll(contextUnits);
			return allUnits;
		}
	}

	/**
	 * returns the {@link IUnit} with the given unitID
	 *
	 * @param unitID the id of the unit
	 * @return the {@link IUnit} with the given unitID
	 * @see IUnit#getUnitID()
	 */
	public IUnit getUnit(String unitID) {
		return getUnitID2Unit().get(unitID);
	}
}
