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

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface Currency
{
	/**
	 * @return Returns the international currency id (e.g. USD, EUR).
	 */
	String getCurrencyID();
	
	/**
	 * We manage currency values as integers (int or long) and therefore
	 * all values have to be divided (usually by 100) before being displayed.
	 * And after parsing, the entered amount needs to be multiplied.
	 * The divisor/multiplicator is 10^decimalDigitCount.
	 * 
	 * @return Returns the number of digits after the decimal separator.
	 */
	int getDecimalDigitCount();

	/**
	 * Instead of displaying the international currency id, you might want to
	 * display the real currency symbol.
	 *
	 * @return Returns the currency symbol (e.g. $).
	 */
	String getCurrencySymbol();
}
