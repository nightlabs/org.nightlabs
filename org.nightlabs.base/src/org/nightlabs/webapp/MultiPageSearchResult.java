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

import java.io.Serializable;
import java.util.List;

/**
 * @deprecated should not be used anymore
 * @author marco
 */
@Deprecated
public class MultiPageSearchResult
	implements Serializable
{
	private static final long serialVersionUID = 1L;

	public MultiPageSearchResult(
			int _itemsFound, int _itemsPerPage, int _pageIndex,
			List<?> _items)
	{
		this.itemsFound = _itemsFound;
		this.itemsPerPage = _itemsPerPage;
		this.pageIndex = _pageIndex;
		this.setItems(_items);
		if (this.itemsPerPage < 1)
			this.itemsPerPage = 1;
	}

	private int itemsFound;
	private int itemsPerPage;
	private int pageIndex;
	protected List<?> items;

	/**
	 * @return Returns the items.
	 */
	public List<?> getItems() {
		return items;
	}
	protected void setItems(List<?> _items) {
		if (_items == null)
			throw new NullPointerException("items must not be null!");
		this.items = _items;
	}
	/**
	 * @return Returns the pageIndex.
	 */
	public int getPageIndex() {
		return pageIndex;
	}
	/**
	 * @return Returns the itemsFound.
	 */
	public int getItemsFound() {
		return itemsFound;
	}
	/**
	 * @return Returns the itemsPerPage.
	 */
	public int getItemsPerPage() {
		return itemsPerPage;
	}
}
