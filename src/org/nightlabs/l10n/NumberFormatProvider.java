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

import java.text.NumberFormat;

import org.nightlabs.config.Config;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface NumberFormatProvider
{
	/**
	 * This method is called once directly after the NumberFormatProvider
	 * has been instantiated.
	 *
	 * @param config
	 */
	public void init(Config config, String isoLanguage, String isoCountry);

	public NumberFormat getIntegerFormat(int minIntegerDigitCount);
	public NumberFormat getFloatFormat(int minIntegerDigitCount, int minDecimalDigitCount, int maxDecimalDigitCount);
	public NumberFormat getCurrencyFormat(int minIntegerDigitCount, int minDecimalDigitCount, int maxDecimalDigitCount, String currencySymbol, boolean includeCurrencySymbol);
	public NumberFormat getScientificFormat(int preferredIntegerDigitCount, int minDecimalDigitCount, int maxDecimalDigitCount, int exponentDigitCount);
}
