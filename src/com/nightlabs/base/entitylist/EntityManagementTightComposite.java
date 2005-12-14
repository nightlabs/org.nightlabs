/*
 * Created on Jun 12, 2005
 *
 */
package com.nightlabs.base.entitylist;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;

import com.nightlabs.rcp.composite.XComposite;


/**
 * @author Niklas Schiffler <nick@nightlabs.de>
 *
 */
public abstract class EntityManagementTightComposite extends XComposite implements EntityManager
{
	protected Set dataChangedListeners;

	public EntityManagementTightComposite(Composite parent, int style)
	{
		this(parent, style, false);
	}

	public EntityManagementTightComposite(Composite parent, int style, boolean doSetLayoutData)
	{
		super(parent, style,
				XComposite.LAYOUT_MODE_TIGHT_WRAPPER,
				doSetLayoutData ? XComposite.LAYOUT_DATA_MODE_GRID_DATA : XComposite.LAYOUT_DATA_MODE_NONE);
		dataChangedListeners = new HashSet();
	}

	/**
   * Call this when you modified the entity object.
   *
   */
  public void notifyDataChangedListeners()
  {
  	Iterator i = dataChangedListeners.iterator();
  	while(i.hasNext())
  		((EntityDataChangedListener)i.next()).entityDataChanged(this);
  }

  /**
   * Listen for modifications of the entity object
   * @param listener your listener
   */
  public void addDataChangedListener(EntityDataChangedListener listener)
  {
  	if(!dataChangedListeners.contains(listener))
  		dataChangedListeners.add(listener);
  }

  /**
   * Remove a listener
   * @param listener the listener
   */
  public void removeDataChangedListener(EntityDataChangedListener listener)
  {
  	if(dataChangedListeners.contains(listener))
  		dataChangedListeners.remove(listener);
  }
	
	private boolean changed = false;
	
	public void setChanged(boolean changed) {
		this.changed = changed;
		if (changed)
			notifyDataChangedListeners();
	}
	
	public boolean isChanged() {
		return changed;
	}
	
  public void dispose()
  {
  	super.dispose();
  	dataChangedListeners.clear();
  	dataChangedListeners = null;
  }

}
