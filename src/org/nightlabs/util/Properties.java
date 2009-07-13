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
package org.nightlabs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link java.util.Properties} utilities.
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public abstract class Properties
{
	/**
	 * Get all matches where property keys match the given pattern
	 * @param properties The properties to match
	 * @param pattern The pattern to match against
	 * @return The {@link Matcher}s that matched.
	 */
	public static Collection<Matcher> getPropertyKeyMatches(java.util.Properties properties, Pattern pattern)
	{
		Collection<Matcher> matches = new ArrayList<Matcher>();
		for (Object element : properties.keySet()) {
			String key = (String) element;
			Matcher m = pattern.matcher(key);
			if(m.matches())
				matches.add(m);
		}
		return matches;
	}
	
	/**
	 * Get all properties which keys start with the given prefix.
	 * <p>
	 *  The returned property elements have the form <code>(key,value)</code> where <code>key</code> is
	 *  the part of the original key after the given <code>keyPrefix</code> and <code>value</code> is
	 *  the original value.
	 * </p>
	 * @param properties The properties to filter.
	 * @param keyPrefix The kex prefix to use
	 * @return the properties that start with the given prefix
	 */
	public static java.util.Properties getProperties(java.util.Properties properties, String keyPrefix)
	{
		java.util.Properties newProperties = new java.util.Properties();
		Collection<Matcher> matches = getPropertyKeyMatches(properties, Pattern.compile("^"+Pattern.quote(keyPrefix)+"(.*)$"));
		for (Matcher m : matches)
			newProperties.put(m.group(1), properties.get(m.group(0)));
		return newProperties;
	}

	public static void putAll(java.util.Properties source, java.util.Properties target)
	{
		for (Object element : source.keySet()) {
			String key = (String) element;
			target.setProperty(key, source.getProperty(key));
		}
	}

	public static java.util.Properties load(String filename) throws IOException
	{
		return load(filename != null ? new File(filename) : null);
	}

	public static java.util.Properties load(File file) throws IOException
	{
		FileInputStream in = new FileInputStream(file);
		try {
			java.util.Properties properties = new java.util.Properties();
			properties.load(in);
			return properties;
		} finally {
			in.close();
		}
	}

	public static void store(String filename, java.util.Properties properties, String comment) throws IOException
	{
		store(filename != null ? new File(filename) : null, properties, comment);
	}
	
	public static void store(File file, java.util.Properties properties, String comment) throws IOException
	{
		FileOutputStream out = new FileOutputStream(file);
		try {
			properties.store(out, comment);
		} finally {
			out.close();
		}
	}
	
}
