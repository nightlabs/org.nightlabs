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
package org.nightlabs.math;

/**
 * This number en/decoder works with the base 36, where the first 10 digits are represented
 * by '0'...'9' and the rest by 'a'...'z' (lower or upper case).
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class Base36Coder
extends BaseNCoder
{
	/**
	 * 0: lower case
	 * 1: upper case
	 */
	private static Base36Coder[] sharedInstances = new Base36Coder[2];

	/**
	 * You can either use your own instance or a shared instance of this class.
	 * It makes sense, to use the shared instance, because the methods are
	 * non-blocking (not synchronized working only on their stack or reading
	 * shared data) anyway.
	 */
	public static Base36Coder sharedInstance(boolean upperCase)
	{
		int idx = upperCase ? 1 : 0;

		if (sharedInstances[idx] == null)
			sharedInstances[idx] = new Base36Coder(upperCase);

		return sharedInstances[idx];
	}

	/**
	 * It is recommended to use a shared instance instead.
	 * @see #sharedInstance(boolean)
	 */
	public Base36Coder(boolean upperCase)
	{
		char[] symbols = new char[36];
		char c = '0';
		for (int i = 0; i < 10; ++i) {
			symbols[i] = c++;
		}
		c = upperCase ? 'A' : 'a';
		for (int i = 10; i < symbols.length; ++i) {
			symbols[i] = c++;
		}

		init(symbols);
	}

}
