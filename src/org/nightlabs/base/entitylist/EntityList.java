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

import java.util.Collection;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.widgets.Table;

/**
 * EntityLists are used to display lists of certain entities. These are 
 * managed by {@link org.nightlabs.base.entitylist.EntityManagementView}s
 * and EntityManagementComposites.
 * Lists are registered by the extension-point "org.nightlabs.base.entitylist"
 * where you have to specifiy an implementation of this interface and
 * a view that should manage this list.
 * 
 * @see org.nightlabs.base.entitylist.EntityManagementView
 * @see org.nightlabs.base.entitylist.EntityManagementOrdinaryComposite
 * @see org.nightlabs.base.entitylist.EntityManagementTightComposite
 * 
 * @author Niklas Schiffler <nick@nightlabs.de>
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public interface EntityList 
{
	/**
	 * Get the unique list id
	 * @return the list id
	 */
	public String getID();
	
	/**
	 * get a label for the selection
	 * @return the label for this list
	 */
	public String getLabel();

	/**
   * This should return a collection of objects that can be managed by 
   * the EntityManager registered with this list (e.g. a user list)
   * @return a Collection of entity objects
   */
  public Collection getEntities();

  /**
   * This should return a lable provider for the entities in this list
   * @return the label provider
   */
  public ITableLabelProvider getLabelProvider();
	
	/**
	 * Should add as many columns to the table the
	 * ITableLabelProvider expects. Additionally a
	 * TableLayout should be added to the table
	 * when more than one column is added.
	 *  
	 * @param table The cleared Table
	 */
	public void addTableColumns(Table table);
  
}
