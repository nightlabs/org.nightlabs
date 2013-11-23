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
package org.nightlabs.io;

import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * This interface can be used to provide {@link IOFilter}s with additional information
 * e.g. different options for save or load
 * 
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * 
 */
public interface IOFilterInformationProvider
{
	/**
	 * the {@link URL} of the source data if read
	 * or the {@link URL} of the target data if write
	 * 
	 * @param url the url of the data to set
	 */
	void setURL(URL url);
	
	/**
	 * returns the {@link URL} of the source data if read
	 * or the {@link URL} of the target data if write
	 *
	 * @return the {@link URL} of the source data if read
	 * or the {@link URL} of the target data if write
	 */
	URL getURL();
	
	/**
	 * returns a {@link Map} which contains all the additional information for the read process
	 * of the ioFilter as values
	 * 
	 * @param keys a {@link Set} of Strings which define the keys for the required data
	 * @return a {@link Map} which contains all the values for the given keys
	 */
	Map<String, Object> getAdditionalDataForRead(Set<String> keys);
	
	/**
	 * returns a {@link Map} which contains all the additional information for the write process
	 * of the ioFilter as values
	 * 
	 * @param keys a {@link Set} of Strings which define the keys for the required data
	 * @return a {@link Map} which contains all the values for the given keys
	 */
	Map<String, Object> getAdditionalDataForWrite(Set<String> keys);
}
