/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.entitylist;

import java.rmi.RemoteException;
import org.nightlabs.ModuleException;

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
