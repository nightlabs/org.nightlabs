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
package org.nightlabs.webapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @deprecated should not be used anymore
 * @author marco
 * @author Marc Klinger - marc at nightlabs dot de (API documentation fixes)
 */
@Deprecated
public class PageLink
{

	public static List<PageLink> createPageLinks(Map<String, String> urlParams, MultiPageSearchResult searchResult)
	{
		int currentPageIndex = searchResult.getPageIndex();

		List<PageLink> pageLinks = new ArrayList<PageLink>();
		int pageCount = searchResult.getItemsFound() / searchResult.getItemsPerPage();
		if (searchResult.getItemsFound() % searchResult.getItemsPerPage() != 0)
			++pageCount;

		boolean haveDots = true;
		for (int i = 0; i < pageCount; ++i) {
			if (i <= 2 || i >= pageCount - 3 || (i >= currentPageIndex - 2 && i <= currentPageIndex + 2)) {
				PageLink pl = new PageLink();
				pl.setCaption(Integer.toString(i));
				pl.setPageIndex(i);

				if (i != currentPageIndex) {
					pl.setUrlParams(new HashMap<String, String>(urlParams));
					pl.setUrlParam("pageIndex", Integer.toString(i));
					pl.setUrlParam("itemsPerPage", new Integer(searchResult.getItemsPerPage()));
				} // if (i != currentPageIndex) {
				pageLinks.add(pl);
				haveDots = true;
			}
			else {
				if (haveDots) {
					PageLink pl = new PageLink();
					pl.setCaption("...");
					pageLinks.add(pl);
					haveDots = false;
				}
			}
		}

		return pageLinks;
	}

	private int pageIndex;
	private String caption;
	private Map<String, String> urlParams = null;

	/**
	 * @return Returns the caption.
	 */
	public String getCaption() {
		return caption;
	}
	/**
	 * @param caption The caption to set.
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}
	/**
	 * @return Returns the pageIndex.
	 */
	public int getPageIndex() {
		return pageIndex;
	}
	/**
	 * @param pageIndex The pageIndex to set.
	 */
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	/**
	 * @return Returns the urlParams.
	 */
	public Map<String, String> getUrlParams() {
		return urlParams;
	}
	/**
	 * @param url The urlParams to set.
	 */
	public void setUrlParams(Map<String, String> url) {
		this.urlParams = url;
	}

	public void setUrlParam(String name, Object value)
	{
		if (urlParams == null)
			urlParams = new HashMap<String, String>();

		urlParams.put(name, value.toString());
	}

}
