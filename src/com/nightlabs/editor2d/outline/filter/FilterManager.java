/**
 * <p> Project: com.nightlabs.editor2d.model </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 23.05.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.outline.filter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.nightlabs.editor2d.MultiLayerDrawComponent;

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
  protected FilterNameProvider nameProvider = null;  
  public FilterManager(FilterNameProvider nameProvider) 
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
