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
package org.nightlabs.config;

import java.util.Arrays;

/**
 * A config module to store an externalized part
 * of the configuration in multi-file-mode.
 * @version $Revision: 12386 $ - $Date: 2008-10-10 14:42:28 +0200 (Fr, 10 Okt 2008) $
 * @author Marco Schulze
 * @author Marc Klinger
 */
public class ConfigInclude extends ConfigModule
{
	private static final long serialVersionUID = 1L;

	private String includeClass;
	private String includeFile;

	public ConfigInclude()
	{
	}

	public ConfigInclude(ConfigModule includeModule, String includeFile)
	{
		setIncludeClass(includeModule.getClass().getName());
		setIncludeFile(includeFile);
		super.setSearchClass(includeModule.getSearchClass());
	}

	public String getIncludeClass()
	{
		return this.includeClass;
	}
	public void setIncludeClass(String _includeClass)
	{
		this.includeClass = _includeClass;
	}

	public String getIncludeFile()
	{
		return this.includeFile;
	}
	public void setIncludeFile(String _includeFile)
	{
		this.includeFile = _includeFile;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this) return true;
		if (!(obj instanceof ConfigInclude)) return false;
		ConfigInclude o = (ConfigInclude) obj;
		return equals(o.includeClass, this.includeClass) && equals(o.includeFile, this.includeFile);
	}

	private static boolean equals(Object obj0, Object obj1)
	{
		if (obj0 instanceof Object[] && obj1 instanceof Object[])
			return obj0 == obj1 || Arrays.equals((Object[])obj0, (Object[])obj1);
		return obj0 == obj1 || (obj0 != null && obj0.equals(obj1));
	}

	@Override
	public int hashCode()
	{
		return 31 * (includeClass == null ? 0 : includeClass.hashCode()) + (includeFile == null ? 0 : includeFile.hashCode());
	}
}
