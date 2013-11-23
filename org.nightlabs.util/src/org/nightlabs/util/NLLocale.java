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

import java.util.Deque;
import java.util.LinkedList;
import java.util.Locale;

/**
 *
 * @author Alexander Bieber <!-- alex [at] nightlabs [dot] de -->
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 */
public class NLLocale {

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
		Locale overrideLocale = getOverrideLocale();
		if (overrideLocale != null)
			return overrideLocale;

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

	private static ThreadLocal<Deque<Locale>> overrideLocale = new ThreadLocal<Deque<Locale>>();

	/**
	 * Set a value to override the current thread's {@link Locale}.
	 * <p>
	 * It is possible to override the <code>Locale</code> returned by {@link #getDefault()} for the current
	 * thread. Therefore, this method stores the passed <code>locale</code> into a static {@link ThreadLocal} instance and
	 * {@link #getDefault()} will directly return it (i.e. bypass the normal resolve-mechanism).
	 * </p>
	 * <p>
	 * If you pass <code>null</code> to this method, it will stop overriding and resume to normal resolve-mode.
	 * </p>
 	 * <p>
	 * Note, that this method works with a {@link Deque} on the {@link ThreadLocal} and thus allows cascaded calls.
	 * </p>
	 * <p>
	 * <b>Important:</b> You should always call {@link #setOverrideLocale(Locale)} with argument <code>null</code>
	 * in a finally block!
	 * </p>
	 * <p>Example:
	 * <blockquote><pre>
	 *  NLLocale.setOverrideLocale(myLocaleAAA);
	 *  try {
	 *  	// Do some operation with NLLocale.getDefault() returning myLocaleAAA.
	 *
	 *  	NLLocale.setOverrideLocale(myLocaleBBB);
	 *  	try {
  	 *  		// Now, NLLocale.getDefault() returns myLocaleBBB.
	 *  	} finally {
	 *  		NLLocale.setOverrideLocale(null); // remove myLocaleBBB from stack
	 *  	}
	 *
	 *  	// Again, NLLocale.getDefault() returns myLocaleAAA.
	 *  } finally {
	 *  	NLLocale.setOverrideLocale(null); // remove myLocaleAAA from stack
	 *  }
	 *  // Now, the normal resolve-mode will by used by NLLocale.getDefault() again.
	 * </pre></blockquote>
	 * </p>
	 *
	 * @param locale the locale to use until the method is called again with <code>null</code> as its parameter.
	 */
	public static void setOverrideLocale(Locale locale) {
		Deque<Locale> d = overrideLocale.get();
		if (locale == null) {
			if (d != null)
				d.removeFirst();

			if (d == null || d.isEmpty())
				overrideLocale.remove();
		}
		else {
			if (d == null) {
				d = new LinkedList<Locale>();
				overrideLocale.set(d);
			}
			d.addFirst(locale);
		}
	}

	/**
	 * Get the current overriding {@link Locale} or <code>null</code>, if it is not overridden.
	 *
	 * @return <code>null</code> or the value that was previously set by {@link #setOverrideLocale(Locale)}.
	 */
	public static Locale getOverrideLocale() {
		Deque<Locale> d = overrideLocale.get();
		if (d == null || d.isEmpty())
			return null;

		return d.getFirst();
	}
}
