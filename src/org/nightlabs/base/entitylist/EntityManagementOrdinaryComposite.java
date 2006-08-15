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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;

import org.nightlabs.base.composite.XComposite;

/**
 * @author Niklas Schiffler <nick@nightlabs.de>
 *
 */
public abstract class EntityManagementOrdinaryComposite
extends XComposite
implements EntityManager
{
	protected Set dataChangedListeners;
	
	public EntityManagementOrdinaryComposite(Composite parent, int style)
	{
		this(parent, style, false);
	}

	public EntityManagementOrdinaryComposite(Composite parent, int style, boolean doSetLayoutData)
	{
		super(parent, style,
				LayoutMode.ORDINARY_WRAPPER,
				doSetLayoutData ? LayoutDataMode.GRID_DATA : LayoutDataMode.NONE);
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

  /**
   * @deprecated this feature is never used und thus don't works
   * FIXME: remove this!
   */
	private boolean changed = false;
	
  /**
   * @deprecated this feature is never used und thus don't works
   * FIXME: remove this!
   */
	public void setChanged(boolean changed) {
		this.changed = changed;
		if (changed)
			notifyDataChangedListeners();
	}
	
  /**
   * @deprecated this feature is never used und thus don't works
   * FIXME: remove this!
   */
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
