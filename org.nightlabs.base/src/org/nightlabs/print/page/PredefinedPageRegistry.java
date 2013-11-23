/* ********************************************************************
 * NightLabsBase - Utilities by NightLabs                             *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.print.page;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 */
public class PredefinedPageRegistry
{
	protected PredefinedPageRegistry() {
		super();
		addPredefinedPage(new A4Page());
		addPredefinedPage(new A3Page());
		addPredefinedPage(new A5Page());
	}
	
	private static PredefinedPageRegistry pageRegistry = null;
	public static PredefinedPageRegistry sharedInstance() {
		if (pageRegistry == null) {
			pageRegistry = new PredefinedPageRegistry();
		}
		return pageRegistry;
	}
	
	private Set<IPredefinedPage> pages = new LinkedHashSet<IPredefinedPage>();
	private Map<String, IPredefinedPage> pageID2Page = new HashMap<String, IPredefinedPage>();
	
	/**
	 * adds a {@link IPredefinedPage}
	 * @param page the {@link IPredefinedPage} to add
	 */
	public void addPredefinedPage(IPredefinedPage page)
	{
		if (page == null)
			throw new IllegalArgumentException("Param page must not be null!");
		
		pages.add(page);
		pageID2Page.put(page.getPageID(), page);
	}

	/**
	 * removes a {@link IPredefinedPage}
	 * @param page the {@link IPredefinedPage} to remove
	 */
	public void removePredefinedPage(IPredefinedPage page) {
		pages.remove(page);
		if (page != null)
			pageID2Page.remove(page.getPageID());
	}
	
	/**
	 * returns all pages which have been added
	 * @return all pages which have been added
	 */
	public Set<IPredefinedPage> getPages() {
		return pages;
	}
	
	/**
	 * returns the {@link IPredefinedPage} with the given pageID
	 * @param pageID the id of the page
	 * @return the {@link IPredefinedPage} with the given pageID
	 * @see IPredefinedPage#getPageID()
	 */
	public IPredefinedPage getPage(String pageID) {
		return pageID2Page.get(pageID);
	}
	
	/**
	 * returns the IDs of all added pages
	 * @return the IDs of all added pages
	 */
	public Set<String> getPageIDs() {
		return pageID2Page.keySet();
	}
}
