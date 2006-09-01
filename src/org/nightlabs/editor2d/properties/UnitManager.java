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
package org.nightlabs.editor2d.properties;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.Set;

import org.nightlabs.i18n.unit.IUnit;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class UnitManager 
{	
//	public UnitManager() {
//		super();
//		getUnits();
//		getCurrentUnit();
//	}
	
	public UnitManager(Set<IUnit> units, IUnit currentUnit) 
	{
		if (units == null || units.isEmpty())
			throw new IllegalArgumentException("Param units must not be null nor empty!");
		
		setUnits(units);
		setCurrentUnit(currentUnit);
	}
				
	private Set<IUnit> units = null;
	public Set<IUnit> getUnits() {
		return units;
	}	
	public void setUnits(Set<IUnit> units) {
		this.units = units;
	}
		
  protected IUnit currentUnit = null;
  public IUnit getCurrentUnit() 
  {
  	if (currentUnit == null)   		
  		currentUnit = units.iterator().next();
  	
  	return currentUnit;
  }
  
//  public void setCurrentUnit(IUnit unit) 
//  {
//  	if (!getUnits().contains(unit))
//  		throw new IllegalArgumentException("Param unit is not contained in getUnits()!");
//  	
//  	IUnit oldUnit = currentUnit;
//  	this.currentUnit = unit;
//  	getPropertyChangeSupport().firePropertyChange(PROP_CURRENT_UNIT_CHANGED, oldUnit, currentUnit);
//  }
  public void setCurrentUnit(IUnit unit) 
  {
  	if (unit == null)
  		throw new IllegalArgumentException("Param unit must not be null!");
  	
  	if (!getUnits().contains(unit)) 
  	{
  		Set<IUnit> oldUnits = new HashSet<IUnit>(units);
  		units.add(unit);
  		getPropertyChangeSupport().firePropertyChange(PROP_UNIT_ADDED, oldUnits, units);
  	}
  	
  	IUnit oldUnit = currentUnit;
  	this.currentUnit = unit;
  	getPropertyChangeSupport().firePropertyChange(PROP_CURRENT_UNIT_CHANGED, oldUnit, currentUnit);
  }

  
  public void setCurrentUnit(String unitID) {
  	for (IUnit unit : getUnits()) {
			if (unit.getUnitID().equals(unitID))
				setCurrentUnit(unit);
		}
  }
  
  public static final String PROP_CURRENT_UNIT_CHANGED = "currentUnit changed";
  public static final String PROP_UNIT_ADDED = "Unit added";  
  
  private PropertyChangeSupport pcs = null;
  protected PropertyChangeSupport getPropertyChangeSupport() {
  	if (pcs == null)
  		pcs = new PropertyChangeSupport(this);
  	return pcs;
  }
  
  public void addPropertyChangeListener(PropertyChangeListener pcl) {    	
  	getPropertyChangeSupport().addPropertyChangeListener(pcl);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener pcl) {    	
  	getPropertyChangeSupport().removePropertyChangeListener(pcl);
  }
  
}
