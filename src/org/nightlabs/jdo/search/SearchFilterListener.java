/* *****************************************************************************
 * NightLabsJDO - NightLabs Utilities for JDO                                  *
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

package org.nightlabs.jdo.search;

/**
 * Callback interface for listener on
 * the list of SearchFilterItems of
 * a SearchFilter.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public interface SearchFilterListener {
	
	/**
	 * Called after the passed item was added.
	 * 
	 * @param item
	 */
	public void itemAdded(ISearchFilterItem item);
	
	/**
	 * Called after the passed item was removed.
	 * 
	 * @param item
	 */
	public void itemRemoved(ISearchFilterItem item);

	/**
	 * Called when the passed item has to be updated.
	 * 
	 * @param item
	 */
	public void updateItem(ISearchFilterItem item);
}
