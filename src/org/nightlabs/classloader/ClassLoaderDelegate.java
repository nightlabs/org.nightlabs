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
package org.nightlabs.classloader;


/**
 * @author Alexander Bieber
 */
public interface ClassLoaderDelegate extends ResourceFinder 
{
	public Class loadClass(String className);
}
