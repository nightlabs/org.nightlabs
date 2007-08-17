/* *****************************************************************************
 * org.nightlabs.jdo.ui - NightLabs Eclipse utilities for JDO                     *
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

package org.nightlabs.jdo.ui.search;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.search.SearchFilter;

/**
 * Common interface to handle different scenarios of 
 * searching entities with the SearchFilter framework.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public interface SearchFilterProvider {
	
	/**
	 * Should create and return a GUI-representation of this
	 * CriteriaBuilder as Composite. 
	 * 
	 * @param parent
	 * @return
	 */
	public Composite createComposite(Composite parent);
	
	/**
	 * Should return the Composite created in {@link #createComposite(Composite)}.
	 * @return
	 */
	public Composite getComposite();
	
	/**
	 * Return the PersonSearchFilter build up by this
	 * Criteria builder.
	 * 
	 * @return
	 */
	public SearchFilter getSearchFilter();
}
