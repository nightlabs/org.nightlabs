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
import java.util.Collection;

import org.nightlabs.editor2d.page.PageRegistry;
import org.nightlabs.editor2d.page.PageRegistryEP;
import org.nightlabs.editor2d.page.unit.DotUnit;
import org.nightlabs.i18n.IUnit;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class UnitManager 
{	
	public UnitManager() {
		super();
		getUnits();
		getCurrentUnit();
	}
	
	public UnitManager(Collection<IUnit> units, IUnit currentUnit) {
		super();
		setUnits(units);
		setCurrentUnit(currentUnit);
	}
	
	private Collection<IUnit> units = null;
	public Collection<IUnit> getUnits() 
	{
		if (units == null)
			units = getPageRegistry().getUnits();
		return units;
	}	
	public void setUnits(Collection<IUnit> units) {
		this.units = units;
	}
	
	public PageRegistry getPageRegistry() {
		return PageRegistryEP.sharedInstance().getPageRegistry();
	}
	
  protected IUnit currentUnit = null;
  public IUnit getCurrentUnit() 
  {
  	if (currentUnit == null)
  		currentUnit = getPageRegistry().getUnit(DotUnit.UNIT_ID);   		
  	return currentUnit;
  }
  public void setCurrentUnit(IUnit unit) 
  {
  	if (!getUnits().contains(unit))
  		throw new IllegalArgumentException("Param unit is not contained in getUnits()!");
  	
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
