/* *****************************************************************************
 * DelegatingClassLoader - NightLabs extendable classloader                    *
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

package org.nightlabs.classloader.delegating;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @author Alexander Bieber
 */
public interface ResourceFinder {
	/**
	 * This method must return a List (preferably a LinkedList for speed reasons) with instances of URL.
	 * If returnAfterFoundFirst is true, it should immediately return, after it found one resource matching
	 * the name. If it is false, it should search all JARs and directories and collect all resources that
	 * match.
	 * <p>
	 * This method is called by {@link ClassLoadingDelegator#findDelegateResources(String, boolean)}, if the resource
	 * was not already searched for before.
	 * </p>
	 *
	 * @param name The name of the resource as it is given to the ClassLoader in findResource(...) and findResources(...)
	 * @param returnAfterFoundFirst Whether to find only one resource [called by ClassLoader.findResource(...)] or all [called by ClassLoader.findResources(...)].
	 * @return Returns <tt>null</tt> or a <tt>List</tt> of <tt>URL</tt> instances - preferably a <tt>LinkedList</tt>.
	 * @throws IOException
	 */
	public List<URL> getResources(String name, boolean returnAfterFoundFirst) throws IOException;
}
