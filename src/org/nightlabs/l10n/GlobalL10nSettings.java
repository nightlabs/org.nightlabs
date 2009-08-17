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
package org.nightlabs.l10n;

import java.util.Locale;

import org.nightlabs.config.ConfigModule;
import org.nightlabs.config.InitException;
import org.nightlabs.i18n.NLLocale;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class GlobalL10nSettings extends ConfigModule
{
	/**
	 * The serial version of this class.
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean autoDetect = false;
	private String language;
	private String country;

	public GlobalL10nSettings()
	{
	}

	@Override
	public void init() throws InitException
	{
		Locale locale = NLLocale.getDefault();

		if (language == null || country == null)
			setAutoDetect(true);

		if (autoDetect == true) {
			setLanguage(locale.getLanguage());
			setCountry(locale.getCountry());
		}
		else {
			System.setProperty("user.language", language);
			System.setProperty("user.country", country);
			Locale.setDefault(new Locale(language, country));
		}
	}
	/**
	 * @return Returns the autoDetect.
	 */
	public boolean isAutoDetect()
	{
		return autoDetect;
	}
	/**
	 * @param autoDetect The autoDetect to set.
	 */
	public void setAutoDetect(boolean autoDetect)
	{
		this.autoDetect = autoDetect;
		setChanged();
	}
	/**
	 * @return Returns the country.
	 */
	public String getCountry()
	{
		return country;
	}
	/**
	 * @param country The country to set.
	 */
	public void setCountry(String country)
	{
		this.country = country;
		setChanged();
	}
	/**
	 * @return Returns the language.
	 */
	public String getLanguage()
	{
		return language;
	}
	/**
	 * @param language The language to set.
	 */
	public void setLanguage(String language)
	{
		this.language = language;
		setChanged();
	}
}
