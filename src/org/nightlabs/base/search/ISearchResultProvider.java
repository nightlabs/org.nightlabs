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
package org.nightlabs.base.search;

import java.util.Collection;

import org.nightlabs.base.notification.SelectionManager;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.notification.NotificationEvent;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public interface ISearchResultProvider<T> 
{
//	/**
//	 * Implementations of this interface need to implement here, what should happen
//	 * if somebody pressed the search button in the {@link SearchContributionItem}
//	 * for their type 
//	 *  
//	 */
//	void searchPressed();
	
	/**
	 * returns a Collection which contains the result of the search
	 * @return a Collection which contains the result of the search 
	 */
	Collection<T> getSelectedObjects();
	
	/**
	 * @return the class of the results of the search 
	 */
	Class getResultTypeClass();	
	
	/**
	 * returns the zone of the selection which will be used for firing selectionEvents
	 * if the getSelectedObjects() is not empty
	 * 
	 * @return the zone of the selection which will be used for firing selectionEvents
	 * if the getSelectedObjects() is not empty
	 * @see SelectionManager
	 * @see NotificationEvent
	 */
	String getSelectionZone();
		
	/**
	 * returns the multilanguage capable name of the type of search
	 * @return the multilanguage capable name of the type of search
	 * @see I18nText
	 */
	I18nText getName();
	
	/**
	 * return the searchContext
	 * @return the searchContext
	 */
	String getContext();
	
	/**
	 * sets the search text
	 * @param text the searchText to set
	 */
	void setSearchText(String text);
}
