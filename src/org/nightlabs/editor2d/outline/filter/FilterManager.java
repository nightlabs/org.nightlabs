/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

package org.nightlabs.editor2d.outline.filter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.nightlabs.editor2d.MultiLayerDrawComponent;

/**
 * Used to set Filters in the Outline Page
 */
public class FilterManager 
{
  public static final String FILTER_CHANGED = "Filter changed";
  public static final String FILTER_ADDED = "Filter added";
  public static final String FILTERS_ADDED = "Filters added";
	    
  public String getTypeName(Class c) {
  	return nameProvider.getTypeName(c);
  }
  
  protected List filters;
  protected List allFilters;
  protected NameProvider nameProvider = null;  
  public FilterManager(NameProvider nameProvider) 
  {
    super();
    this.nameProvider = nameProvider;
  }
    
  public void addFilter(Class clazz) 
  {
  	if (clazz == null)
			throw new IllegalArgumentException("Param clazz must not be null!");
  	
    getFilters().add(clazz);
    getAllFilters().add(clazz);
    
    checkIgnore();    
    pcs.firePropertyChange(FILTER_ADDED, null, clazz);
  }
  
  public void addFilters(Collection classes) 
  {
  	if (classes == null)
			throw new IllegalArgumentException("Param classes must not be null!");
  	
    getFilters().addAll(classes);
    getAllFilters().addAll(classes); 
    
    checkIgnore();    
    pcs.firePropertyChange(FILTERS_ADDED, null, classes);    
  }
  
  public List getFilters() 
  {
    if (filters == null)
      filters = new ArrayList();
    
    return filters;
  }
  
  public List getAllFilters() 
  {
    if (allFilters == null)
      allFilters = new ArrayList();
    
    return allFilters;
  }
  
  public void setFilter(Class clazz) 
  {
  	if (clazz == null)
			throw new IllegalArgumentException("Param clazz must not be null!");
  	
    getFilters().clear();
    getFilters().add(clazz);
    pcs.firePropertyChange(FILTER_CHANGED, null, getFilters());
  }
  
  public void setFilter(Collection classes) 
  {
  	if (classes == null)
			throw new IllegalArgumentException("Param classes must not be null!");
  	
    getFilters().clear();
    getFilters().addAll(classes);
    pcs.firePropertyChange(FILTER_CHANGED, null, getFilters());    
  }
  
  public void setAllFiltersAvailable() {
    setFilter(getAllFilters());
  }
    
  public boolean isAllFilterSet() {
  	if (filters != null && allFilters != null) {
    	if (filters.size() == allFilters.size())
    		return true;
    	else
    		return false;
  	}
  	return true;
  }
  
  protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(pcl);
	}
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		pcs.removePropertyChangeListener(pcl);
	}
	
	protected PropertyChangeListener newTypeListener = new PropertyChangeListener()
	{	
		public void propertyChange(PropertyChangeEvent evt) 
		{
			if (evt.getPropertyName().equals(MultiLayerDrawComponent.TYPE_ADDED)) {
				Class c = (Class) evt.getNewValue();
				addFilter(c);
			}
		}	
	};
	
	public PropertyChangeListener getTypeListener() {
		return newTypeListener;
	}
	
	protected List ignoreClasses = new ArrayList();
	public void ignoreClass(Class c) 
	{
		ignoreClasses.add(c);
		checkIgnore();
	}
	
	protected void checkIgnore() 
	{
		for (Iterator it = ignoreClasses.iterator(); it.hasNext(); ) {
			Class c = (Class) it.next();
			if (getAllFilters().contains(c)) {				
				getAllFilters().remove(c);
				if (getFilters().contains(c))
					getFilters().remove(c);
			}
		}		
	}
	
}
