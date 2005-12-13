/* ************************************************************************** *
 * Copyright (C) 2004 NightLabs GmbH, Alexander Bieber                        *
 * All rights reserved.                                                       *
 * http://www.NightLabs.de                                                    *
 *                                                                            *
 * This program and the accompanying materials are free software; you can re- *
 * distribute it and/or modify it under the terms of the GNU Lesser General   *
 * Public License as published by the Free Software Foundation; either ver 2  *
 * of the License, or any later version.                                      *
 *                                                                            *
 * This module is distributed in the hope that it will be useful, but WITHOUT *
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FIT- *
 * NESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more *
 * details.                                                                   *
 *                                                                            *
 * You should have received a copy of the GNU General Public License along    *
 * with this module; if not, write to the Free Software Foundation, Inc.:     *
 *    59 Temple Place, Suite 330                                              *
 *    Boston MA 02111-1307                                                    *
 *    USA                                                                     *
 *                                                                            *
 * Or get it online:                                                          *
 *    http://www.opensource.org/licenses/lgpl-license.php                     *
 *                                                                            *
 * In case, you want to use this module or parts of it in a commercial way    * 
 * that is not allowed by the LGPL, pleas contact us and we will provide a    *
 * commercial licence.                                                        *
 * ************************************************************************** */

/*
 * Created 	on Oct 29, 2004
 * 					by Alexander Bieber
 *
 */
package com.nightlabs.classloader;

import java.io.IOException;
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
	 *
	 * @param name The name of the resource as it is given to the ClassLoader in findResource(...) and findResources(...)
	 * @param returnAfterFoundFirst Whether to find only one resource [called by ClassLoader.findResource(...)] or all [called by ClassLoader.findResources(...)].
	 * @return Returns <tt>null</tt> or a <tt>List</tt> of <tt>URL</tt> instances - preferably a <tt>LinkedList</tt>.
	 * @throws IOException
	 */
	public List getResources(String name, boolean returnAfterFoundFirst) throws IOException;
}
