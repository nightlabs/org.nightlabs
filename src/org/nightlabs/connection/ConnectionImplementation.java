/* *****************************************************************************
 * NightLabsConnection - Provides a unified & extensible connection API        *
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
 ******************************************************************************/

package org.nightlabs.connection;

import java.util.Locale;
import java.util.Map;

import org.nightlabs.i18n.I18nUtil;

public class ConnectionImplementation
{
	private String connectionClassName;
	private Class<? extends Connection> connectionClass;
	private Map<Locale, String> names;
	private Map<Locale, String> descriptions;

	public ConnectionImplementation(Connection connection)
	{
		this.connectionClass = connection.getClass();
		this.connectionClassName = connectionClass.getName();
		this.names = connection.getNames();
		this.descriptions = connection.getDescriptions();
	}

	public String getConnectionClassName()
	{
		return connectionClassName;
	}

	public void setConnectionClassName(String className)
	{
		this.connectionClassName = className;
	}

	public Class<? extends Connection> getConnectionClass()
	{
		return connectionClass;
	}

	public void setConnectionClass(Class<? extends Connection> clazz)
	{
		this.connectionClass = clazz;
	}

	public Map<Locale, String> getDescriptions()
	{
		return descriptions;
	}

	public void setDescriptions(Map<Locale, String> descriptions)
	{
		this.descriptions = descriptions;
	}

	public Map<Locale, String> getNames()
	{
		return names;
	}

	public String getName(Locale locale)
	{
		return I18nUtil.getClosestL10n(names, locale);
	}

	public String getDescription(Locale locale)
	{
		return I18nUtil.getClosestL10n(descriptions, locale);
	}

	public void setNames(Map<Locale, String> names)
	{
		this.names = names;
	}
}
