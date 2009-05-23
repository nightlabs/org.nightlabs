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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A config module to store externalized parts
 * of the configuration in multi-file-mode.
 * @version $Revision: 13277 $ - $Date: 2009-01-30 16:35:57 +0100 (Fr, 30 Jan 2009) $
 * @author Marco Schulze
 */
public class ConfigIncludes extends ConfigModule
{
	private static final long serialVersionUID = 1L;

	public ConfigIncludes()
	{
	}

	private CfModList<ConfigInclude> configIncludes;
	public CfModList<ConfigInclude> getConfigIncludes() { return this.configIncludes; }
	public void setConfigIncludes(CfModList<ConfigInclude> _configIncludes)
	{
		this.configIncludes = _configIncludes;
		this.configIncludes.setOwnerCfMod(this);
	}

	/**
	 * @deprecated Only for downward compatibility in order to be able to load an old config file!
	 */
	@Deprecated
	public void setConfigIncludes(ArrayList<ConfigInclude> _configIncludes)
	{
		this.configIncludes = new CfModList<ConfigInclude>(_configIncludes);
		this.configIncludes.setOwnerCfMod(this);
	}

	@Override
	public void init()
	{
		if (configIncludes == null) {
			configIncludes = new CfModList<ConfigInclude>();
			configIncludes.setOwnerCfMod(this);
		}
	}

	@Override
	public void setChanged(boolean changed)
	{
		super.setChanged(changed);

		if (changed)
			className2ConfigIncludeList = null;
	}

	private transient Map<String, List<ConfigInclude>> className2ConfigIncludeList = null;

	public List<ConfigInclude> getConfigIncludeListForConfigModuleClass(Class<?> configModuleClass)
	{
		return getConfigIncludeListForConfigModuleClass(configModuleClass.getName());
	}

	public List<ConfigInclude> getConfigIncludeListForConfigModuleClass(String configModuleClassName)
	{
		if (className2ConfigIncludeList == null) {
			Map<String, List<ConfigInclude>> m = new HashMap<String, List<ConfigInclude>>();

			for (ConfigInclude configInclude : configIncludes) {
				List<ConfigInclude> l = m.get(configInclude.getIncludeClass());
				if (l == null) {
					l = new ArrayList<ConfigInclude>();
					m.put(configInclude.getIncludeClass(), l);
				}
				l.add(configInclude);
			}

			className2ConfigIncludeList = m;
		}

		return className2ConfigIncludeList.get(configModuleClassName);
	}
}
