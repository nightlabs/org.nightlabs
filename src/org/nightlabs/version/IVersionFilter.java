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
package org.nightlabs.version;

/**
 * @author Marius Heinzmann -- Marius[at]NightLabs[dot]de
 * @deprecated Moved to separate artifact "org.nightlabs.version". The package "org.nightlabs.version" should
 * be removed from artifact "org.nightlabs.base" and a dependency onto artifact "org.nightlabs.version" should
 * be introduced instead. Or even better we should check if we can migrate to the version-handling-classes from OSGI
 * (e.g. org.osgi.framework.Version and org.eclipse.osgi.service.resolver.VersionRange). Marco :-)
 */
@Deprecated
public interface IVersionFilter {

	/**
	 * Returns <code>true</code> if the given Version matches some pattern stored in the implementing
	 * class, <code>false</code> otherwise.
	 *
	 * @param version the {@link Version}, which should be checked against a pattern stored by the
	 * 		implementing class.
	 * @return <code>true</code> if the given Version matches some pattern stored in the implementing
	 * class, <code>false</code> otherwise.
	 */
	boolean matches(Version version);
}
