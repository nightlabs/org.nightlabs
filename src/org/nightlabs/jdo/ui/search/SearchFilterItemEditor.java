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
import org.eclipse.swt.widgets.Control;
import org.nightlabs.jdo.search.SearchFilterItem;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public abstract class SearchFilterItemEditor {
	
	/**
	 * After the first call this method should always
	 * return the same control. So from the second call
	 * the parent parameter should be neglected.
	 * 
	 * @param parent
	 * @return
	 */
	public abstract Control getControl(Composite parent);
	
	/**
	 * Should return the SearchFilterItem this 
	 * editor has build.
	 * 
	 * @return
	 */
	public abstract SearchFilterItem getSearchFilterItem();
	
	/**
	 * Will be called when the
	 * editor is closed. It should be
	 * used for cleanup (removing listeners), 
	 * not for disposing widgets.
	 */
	public abstract void close();
	
	/**
	 * Creates a new instance of the current class.
	 * 
	 * @return
	 */
	public SearchFilterItemEditor newInstance() {
		SearchFilterItemEditor newEditor = null;
		try {
			newEditor = (SearchFilterItemEditor) this.getClass().newInstance();
		} catch (Throwable t) {
			IllegalStateException ill = new IllegalStateException("Could not create new instance of SearchFilterItemEditor "+this); //$NON-NLS-1$
			ill.initCause(t);
			throw ill;
		}
		return newEditor;
	}
	
}
