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

import org.nightlabs.config.ConfigModule;
import org.nightlabs.config.InitException;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class L10nFormatCfMod extends ConfigModule
{
	/**
	 * The serial version of this class.
	 */
	private static final long serialVersionUID = 1L;
	
	private String dateFormatProvider;
	private String numberFormatProvider;
	
	public L10nFormatCfMod()
	{
	}

	/**
	 * @see org.nightlabs.config.ConfigModule#init()
	 */
	@Override
	public void init() throws InitException
	{
		if (dateFormatProvider == null)
			setDateFormatProvider(DefaultDateFormatProvider.class.getName());

		if (numberFormatProvider == null)
			setNumberFormatProvider(DefaultNumberFormatProvider.class.getName());
	}
	/**
	 * @return Returns the dateFormatProvider.
	 */
	public String getDateFormatProvider()
	{
		return dateFormatProvider;
	}
	/**
	 * @param dateFormatProvider The dateFormatProvider to set.
	 */
	public void setDateFormatProvider(String dateFormatProvider)
	{
		this.dateFormatProvider = dateFormatProvider;
		setChanged();
	}
	/**
	 * @return Returns the numberFormatProvider.
	 */
	public String getNumberFormatProvider()
	{
		return numberFormatProvider;
	}
	/**
	 * @param numberFormatProvider The numberFormatProvider to set.
	 */
	public void setNumberFormatProvider(String numberFormatProvider)
	{
		this.numberFormatProvider = numberFormatProvider;
		setChanged();
	}
	/**
	 * @see org.nightlabs.config.ConfigModule#getIdentifier()
	 */
	@Override
	public String getIdentifier()
	{
		return super.getIdentifier();
	}
	/**
	 * @see org.nightlabs.config.ConfigModule#setIdentifier(java.lang.String)
	 */
	@Override
	public void setIdentifier(String _identifier)
	{
		super.setIdentifier(_identifier);
	}
}
