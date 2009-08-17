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

import java.util.Locale;

/**
 * 
 * @author Alexander Bieber <!-- alex [at] nightlabs [dot] de -->
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 */
public class NLLocale {

	//org.nightlabs.util.NLLocale
	public static final String SYSTEM_PROPERTY_KEY_NL_LOCALE_CLASS = NLLocale.class.getName();
	private static NLLocale sharedInstance = null;

	protected NLLocale() { }

	/**
	 * This method returns the default {@link Locale} for the current context.
	 * The context might be the running VM, but could also be the currently log
	 * in user in a server environment and the current execution thread.
	 * 
	 * Use this as a substitute for {@link Locale#getDefault()}.
	 *  
	 * @return The default {@link Locale} for the current context.
	 */
	public synchronized static Locale getDefault() 
	{
		if (sharedInstance == null) {
			String className = System.getProperty(SYSTEM_PROPERTY_KEY_NL_LOCALE_CLASS);
			// if no system property is set, we are probably in the client so just use Locale.getDefault()

			Class<?> nlLocaleClass;
			if (className == null)
				nlLocaleClass = NLLocale.class;
			else {
				try {
					nlLocaleClass = Class.forName(className);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException("The system-property '" + SYSTEM_PROPERTY_KEY_NL_LOCALE_CLASS + "' was specified as '"+className+"' but this class cannot be found!", e);
				}
			}

			try {
				sharedInstance = (NLLocale) nlLocaleClass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Instantiating the class '"+className+"' specified by the system-property '" + SYSTEM_PROPERTY_KEY_NL_LOCALE_CLASS + "' was found, but it could not be instantiated!", e);
			}
		}

		return sharedInstance._getDefault();  
	}

	protected Locale _getDefault()
	{
		return Locale.getDefault();
	}
}
