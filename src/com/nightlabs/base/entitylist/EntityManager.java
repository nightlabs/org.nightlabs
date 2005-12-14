/*
 * Created on Jun 1, 2005
 *
 */
package com.nightlabs.base.entitylist;

import java.rmi.RemoteException;
import com.nightlabs.ModuleException;

/**
 * @author Niklas Schiffler <nick@nightlabs.de>
 *
 */
public interface EntityManager 
{
  /**
   * Set the entity to manage
   * @param entity the entity
   */
  public void setEntity(Object entity);

	/**
	 * Save changes in the management view
	 * @throws ModuleException
	 * @throws RemoteException
	 */
  public void save()	throws ModuleException, RemoteException;

  /**
   * Call this when you modified the entity object.
   *
   */
  public void notifyDataChangedListeners();

  /**
   * Listen for modifications of the entity object
   * @param listener your listener
   */
  public void addDataChangedListener(EntityDataChangedListener listener);

  /**
   * Remove a listener
   * @param listener the listener
   */
  public void removeDataChangedListener(EntityDataChangedListener listener);

}
