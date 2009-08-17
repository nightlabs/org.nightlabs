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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.nightlabs.config.Config;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class DefaultNumberFormatProvider implements NumberFormatProvider
{
	protected Config config;
	protected String isoLanguage;
	protected String isoCountry;
	protected DefaultNumberFormatCfMod defaultNumberFormatCfMod;
	
	public DefaultNumberFormatProvider()
	{
	}

	/**
	 * @see org.nightlabs.l10n.NumberFormatProvider#init(Config, String, String)
	 */
	public void init(Config config, String isoLanguage, String isoCountry)
	{
		try {
			this.config = config;
			this.isoLanguage = isoLanguage;
			this.isoCountry = isoCountry;
			this.defaultNumberFormatCfMod = (DefaultNumberFormatCfMod) ConfigUtil.createConfigModule(config, DefaultNumberFormatCfMod.class, isoLanguage, isoCountry);
		} catch (RuntimeException x) {
			throw x;
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	/**
	 * key: String key (Long.toHexString(flags)+'_'+Integer.toHexString(decimalDigitCount))
	 */
	protected Map<String, DecimalFormat> numberFormats = new HashMap<String, DecimalFormat>();

	protected DecimalFormatSymbols createDecimalFormatSymbols()
	{
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(new Locale(isoLanguage, isoCountry));
		dfs.setGroupingSeparator(defaultNumberFormatCfMod.getGroupingSeparator());
		dfs.setDecimalSeparator(defaultNumberFormatCfMod.getDecimalSeparator());
		return dfs;
	}
	
	protected String createIntegerDigitCountPattern(int integerDigitCount)
	{
		StringBuffer sb = new StringBuffer();
		int groupingSize = defaultNumberFormatCfMod.getGroupingSize();
		for (int i = 1; i <= integerDigitCount || i <= groupingSize; ++i) {
			sb.insert(0, i <= integerDigitCount ? '0' : '#');
			if (i % groupingSize == 0)
				sb.insert(0, ',');
		}
		return sb.toString();
	}
	
	protected String createDecimalDigitCountPattern(int minDecimalDigitCount, int maxDecimalDigitCount)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i <= minDecimalDigitCount || i <= maxDecimalDigitCount; ++i) {
			sb.append(i <= minDecimalDigitCount ? '0' : '#');
		}
		return sb.toString();
	}

	/**
	 * @see org.nightlabs.l10n.NumberFormatProvider#getIntegerFormat(int)
	 */
	public NumberFormat getIntegerFormat(int minIntegerDigitCount)
	{
		String key = "integer_"+Integer.toHexString(minIntegerDigitCount);
		DecimalFormat df;
		synchronized (numberFormats) {
			df = numberFormats.get(key);
		}
		if (df == null) {
			DecimalFormatSymbols dfs = createDecimalFormatSymbols();
			String integerDigitCountPattern = createIntegerDigitCountPattern(minIntegerDigitCount);

			df = new DecimalFormat(
					defaultNumberFormatCfMod.getPositivePrefix()
					+'#'+integerDigitCountPattern
					+defaultNumberFormatCfMod.getPositiveSuffix()
					+';'
					+defaultNumberFormatCfMod.getNegativePrefix()
					+'#'+integerDigitCountPattern
					+defaultNumberFormatCfMod.getNegativeSuffix(),
					dfs);

			synchronized (numberFormats) {
				numberFormats.put(key, df);
			}
		}
		
		return df;
	}

	/**
	 * @see org.nightlabs.l10n.NumberFormatProvider#getFloatFormat(int, int, int)
	 */
	public NumberFormat getFloatFormat(int minIntegerDigitCount, int minDecimalDigitCount, int maxDecimalDigitCount)
	{
		String key = "float_"+Integer.toHexString(minIntegerDigitCount)
				+'_'+Integer.toHexString(minDecimalDigitCount)
				+'_'+Integer.toHexString(maxDecimalDigitCount);
		DecimalFormat df;
		synchronized (numberFormats) {
			df = numberFormats.get(key);
		}
		if (df == null) {
			DecimalFormatSymbols dfs = createDecimalFormatSymbols();
			String integerDigitCountPattern = createIntegerDigitCountPattern(minIntegerDigitCount);
			String decimalDigitCountPattern = createDecimalDigitCountPattern(minDecimalDigitCount, maxDecimalDigitCount);

			df = new DecimalFormat(
					defaultNumberFormatCfMod.getPositivePrefix()
					+'#'+integerDigitCountPattern
					+'.'+decimalDigitCountPattern
					+defaultNumberFormatCfMod.getPositiveSuffix()
					+';'
					+defaultNumberFormatCfMod.getNegativePrefix()
					+'#'+integerDigitCountPattern
					+'.'+decimalDigitCountPattern
					+defaultNumberFormatCfMod.getNegativeSuffix(),
					dfs);
			
			synchronized (numberFormats) {
				numberFormats.put(key, df);
			}
		}

		return df;
	}

	/**
	 * @see org.nightlabs.l10n.NumberFormatProvider#getCurrencyFormat(int, int, int, String, boolean)
	 */
	public NumberFormat getCurrencyFormat(int minIntegerDigitCount, int minDecimalDigitCount, int maxDecimalDigitCount, String currencySymbol, boolean includeCurrencySymbol)
	{
		String key = "currency_"+Integer.toHexString(minIntegerDigitCount)
				+'_'+Integer.toHexString(minDecimalDigitCount)
				+'_'+Integer.toHexString(maxDecimalDigitCount)
				+'_'+currencySymbol
				+'_'+includeCurrencySymbol;
		DecimalFormat df;
		synchronized (numberFormats) {
			df = numberFormats.get(key);
		}
		if (df == null) {
			DecimalFormatSymbols dfs = createDecimalFormatSymbols();
//			dfs.setInternationalCurrencySymbol(null);
			dfs.setCurrencySymbol(currencySymbol);
			String integerDigitCountPattern = createIntegerDigitCountPattern(minIntegerDigitCount);
			String decimalDigitCountPattern = createDecimalDigitCountPattern(minDecimalDigitCount, maxDecimalDigitCount);

			String beginCurrSymbol;
			String endCurrSymbol;
			if (!includeCurrencySymbol) {
				beginCurrSymbol = "";
				endCurrSymbol = "";
			}
			else if (DefaultNumberFormatCfMod.CURRENCYSYMBOLPOSITION_BEGIN.equals(defaultNumberFormatCfMod.getCurrencySymbolPosition())) {
				beginCurrSymbol = "\u00A4 ";
				endCurrSymbol = "";
			}
			else {
				beginCurrSymbol = "";
				endCurrSymbol = " \u00A4";
			}

			df = new DecimalFormat(
					beginCurrSymbol
					+defaultNumberFormatCfMod.getPositivePrefix()
					+'#'+integerDigitCountPattern
					+'.'+decimalDigitCountPattern
					+defaultNumberFormatCfMod.getPositiveSuffix()
					+endCurrSymbol
					+';'
					+beginCurrSymbol
					+defaultNumberFormatCfMod.getNegativePrefix()
					+'#'+integerDigitCountPattern
					+'.'+decimalDigitCountPattern
					+defaultNumberFormatCfMod.getNegativeSuffix()
					+endCurrSymbol,
					dfs);

			synchronized (numberFormats) {
				numberFormats.put(key, df);
			}
		}

		return df;
	}

	/**
	 * @see org.nightlabs.l10n.NumberFormatProvider#getScientificFormat(int, int, int, int)
	 */
	public NumberFormat getScientificFormat(int preferredIntegerDigitCount, int minDecimalDigitCount, int maxDecimalDigitCount, int exponentDigitCount)
	{
		String key = "scientific_"+Integer.toHexString(preferredIntegerDigitCount)
				+'_'+Integer.toHexString(minDecimalDigitCount)
				+'_'+Integer.toHexString(maxDecimalDigitCount)
				+'_'+Integer.toHexString(exponentDigitCount);
		DecimalFormat df;
		synchronized (numberFormats) {
			df = numberFormats.get(key);
		}
		if (df == null) {
			DecimalFormatSymbols dfs = createDecimalFormatSymbols();
			
			StringBuffer sb = new StringBuffer();
			for (int i = 1; i <= preferredIntegerDigitCount; ++i) {
				sb.append('0');
			}
			String integerDigitCountPattern = sb.toString();
			String decimalDigitCountPattern = createDecimalDigitCountPattern(minDecimalDigitCount, maxDecimalDigitCount);
			sb.setLength(0);
			for (int i = 1; i <= exponentDigitCount; ++i) {
				sb.append('0');
			}
			String exponentDigitCountPattern = sb.toString();

			df = new DecimalFormat(
					defaultNumberFormatCfMod.getPositivePrefix()
					+'#'+integerDigitCountPattern
					+'.'+decimalDigitCountPattern
					+'E'+exponentDigitCountPattern
					+defaultNumberFormatCfMod.getPositiveSuffix()
					+';'
					+defaultNumberFormatCfMod.getNegativePrefix()
					+'#'+integerDigitCountPattern
					+'.'+decimalDigitCountPattern
					+'E'+exponentDigitCountPattern
					+defaultNumberFormatCfMod.getNegativeSuffix(),
					dfs);

			synchronized (numberFormats) {
				numberFormats.put(key, df);
			}
		}

		return null;
	}

}
