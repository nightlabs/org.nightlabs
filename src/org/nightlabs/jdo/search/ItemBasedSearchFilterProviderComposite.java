/* *****************************************************************************
 * org.nightlabs.jdo - NightLabs Eclipse utilities for JDO                     *
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

import org.eclipse.swt.widgets.Composite;

/**
 * Default implementation of an ItemBased SearchFilterProviderComposite.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public class ItemBasedSearchFilterProviderComposite extends
		AbstractItemBasedSearchFilterProviderComposite {

	/**
	 * @param parent
	 * @param style
	 * @param searchFilterProvider
	 * @param listMutator
	 * @param resultFetcher
	 * @param login
	 */
	public ItemBasedSearchFilterProviderComposite(Composite parent, int style,
			SearchFilterProvider searchFilterProvider,
			SearchFilterItemListMutator listMutator,
			SearchResultFetcher resultFetcher) {
		super(parent, style, searchFilterProvider, listMutator, resultFetcher);
	}

	/**
	 * @see org.nightlabs.jdo.search.AbstractItemBasedSearchFilterProviderComposite#createSearchFilterItemList(org.eclipse.swt.widgets.Composite, int)
	 */
	public SearchFilterItemList createSearchFilterItemList(Composite parent,
			int style) {
		return new SearchFilterItemList(parent, style);
	}

}
