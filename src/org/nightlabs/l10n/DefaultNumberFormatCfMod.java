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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.nightlabs.config.ConfigModule;
import org.nightlabs.config.InitException;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class DefaultNumberFormatCfMod extends ConfigModule
{
	/**
	 * The serial version of this class.
	 */
	private static final long serialVersionUID = 1L;
	
	private String positivePrefix;
	private String positiveSuffix;
	private String negativePrefix;
	private String negativeSuffix;

	private char decimalSeparator = 0;
	private char groupingSeparator = 0;
	private int groupingSize = -1;

	private String currencySymbolPosition;

	public static String CURRENCYSYMBOLPOSITION_BEGIN = "begin";
	public static String CURRENCYSYMBOLPOSITION_END = "end";

	
	public DefaultNumberFormatCfMod()
	{
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


	/**
	 * @see org.nightlabs.config.ConfigModule#init()
	 */
	@Override
	public void init() throws InitException
	{
		String identifier = getIdentifier();
		if (identifier == null)
			throw new IllegalStateException("identifier of this ConfigModule is null! It should be the language/country-code (e.g. en_US)!");

		String[] sa = identifier.split("_");
		if (sa.length < 1)
			throw new IllegalStateException("identifier of this ConfigModule is invalid (empty?!)! It should be the language/country-code (e.g. en_US)!");

		String language = sa[0];
		String country = null;
		if (sa.length > 1)
			country = sa[1];

		Locale locale = null;
		if (country != null)
			locale = new Locale(language, country);
		else
			locale = new Locale(language);
		
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
		
		if (positivePrefix == null)
			setPositivePrefix("");

		if (positiveSuffix == null)
			setPositiveSuffix("");

		if (negativePrefix == null)
			setNegativePrefix(""+dfs.getMinusSign());

		if (negativeSuffix == null)
			setNegativeSuffix("");

		if (decimalSeparator == 0)
			setDecimalSeparator(dfs.getDecimalSeparator());

		if (groupingSeparator == 0)
			setGroupingSeparator(dfs.getGroupingSeparator());

		if (groupingSize < 0) {
			NumberFormat nf = NumberFormat.getNumberInstance();
			if (nf instanceof DecimalFormat)
				setGroupingSize(((DecimalFormat)nf).getGroupingSize());
			else
				setGroupingSize(3);
		}

		if (currencySymbolPosition == null)
			setCurrencySymbolPosition(CURRENCYSYMBOLPOSITION_END);
	}

	/**
	 * @return Returns the currencySymbolPosition.
	 */
	public String getCurrencySymbolPosition()
	{
		return currencySymbolPosition;
	}
	/**
	 * @param currencySymbolPosition The currencySymbolPosition to set.
	 */
	public void setCurrencySymbolPosition(String currencySymbolPosition)
	{
		if (CURRENCYSYMBOLPOSITION_BEGIN.equals(currencySymbolPosition))
			this.currencySymbolPosition = CURRENCYSYMBOLPOSITION_BEGIN;
		else if (CURRENCYSYMBOLPOSITION_END.equals(currencySymbolPosition))
			this.currencySymbolPosition = CURRENCYSYMBOLPOSITION_END;
		else
			throw new IllegalArgumentException(
					"currencySymbolPosition \""+currencySymbolPosition+"\" is invalid!" +
							" Must be CURRENCYSYMBOLPOSITION_BEGIN=\""+CURRENCYSYMBOLPOSITION_BEGIN+"\" or " +
									"CURRENCYSYMBOLPOSITION_END=\""+CURRENCYSYMBOLPOSITION_END+"\"!");

		setChanged();
	}
	/**
	 * @return Returns the decimalSeparator.
	 */
	public char getDecimalSeparator()
	{
		return decimalSeparator;
	}
	/**
	 * @param decimalSeparator The decimalSeparator to set.
	 */
	public void setDecimalSeparator(char decimalSeparator)
	{
		this.decimalSeparator = decimalSeparator;
		setChanged();
	}
	/**
	 * @return Returns the groupingSeparator.
	 */
	public char getGroupingSeparator()
	{
		return groupingSeparator;
	}
	/**
	 * @param groupingSeparator The groupingSeparator to set.
	 */
	public void setGroupingSeparator(char groupingSeparator)
	{
		this.groupingSeparator = groupingSeparator;
		setChanged();
	}
	/**
	 * @return Returns the groupingSize.
	 */
	public int getGroupingSize()
	{
		return groupingSize;
	}
	/**
	 * @param groupingSize The groupingSize to set.
	 */
	public void setGroupingSize(int groupingSize)
	{
		this.groupingSize = groupingSize;
		setChanged();
	}
	/**
	 * @return Returns the negativePrefix.
	 */
	public String getNegativePrefix()
	{
		return negativePrefix;
	}
	/**
	 * @param negativePrefix The negativePrefix to set.
	 */
	public void setNegativePrefix(String negativePrefix)
	{
		this.negativePrefix = negativePrefix;
		setChanged();
	}
	/**
	 * @return Returns the negativeSuffix.
	 */
	public String getNegativeSuffix()
	{
		return negativeSuffix;
	}
	/**
	 * @param negativeSuffix The negativeSuffix to set.
	 */
	public void setNegativeSuffix(String negativeSuffix)
	{
		this.negativeSuffix = negativeSuffix;
		setChanged();
	}
	/**
	 * @return Returns the positivePrefix.
	 */
	public String getPositivePrefix()
	{
		return positivePrefix;
	}
	/**
	 * @param positivePrefix The positivePrefix to set.
	 */
	public void setPositivePrefix(String positivePrefix)
	{
		this.positivePrefix = positivePrefix;
		setChanged();
	}
	/**
	 * @return Returns the positiveSuffix.
	 */
	public String getPositiveSuffix()
	{
		return positiveSuffix;
	}
	/**
	 * @param positiveSuffix The positiveSuffix to set.
	 */
	public void setPositiveSuffix(String positiveSuffix)
	{
		this.positiveSuffix = positiveSuffix;
		setChanged();
	}
}
