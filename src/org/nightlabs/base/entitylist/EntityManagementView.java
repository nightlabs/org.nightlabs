/*
 * Created on Jun 1, 2005
 *
 */
package org.nightlabs.base.entitylist;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.ui.part.ViewPart;

/**
 * @author Niklas Schiffler <nick@nightlabs.de>
 *
 */
public abstract class EntityManagementView extends ViewPart implements EntityManager
{
	protected Set dataChangedListeners;

	public EntityManagementView()
	{
		dataChangedListeners = new HashSet();
	}

	/**
	 * Get the id of this view
	 * @return the view id
	 */
	public abstract String getViewID();

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
   * Remove a data changed listener
   * @param listener the listener
   */
  public void removeDataChangedListener(EntityDataChangedListener listener)
  {
  	if(dataChangedListeners.contains(listener))
  		dataChangedListeners.remove(listener);
  }

  public void dispose()
  {
  	super.dispose();
  	dataChangedListeners.clear();
  	dataChangedListeners = null;
  }

}
