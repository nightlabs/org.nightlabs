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
 * This number en/decoder compresses a number the most effeciently using the base
 * 62. This means, one digit can have 62 different values: <code>'0'...'9', 'A'...'Z', 'a'...'z'</code>
 * <p>
 * If you need to encode huge numbers in as few digits as possible (for example when creating
 * a barcode), this coder is the best choice.
 * </p>
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class Base62Coder
extends BaseNCoder
{
	private static Base62Coder _sharedInstance = null;

	/**
	 * You can either use your own instance or a shared instance of this class.
	 * It makes sense, to use the shared instance, because the methods are
	 * non-blocking (not synchronized working only on their stack or reading
	 * shared data) anyway.
	 */
	public static Base62Coder sharedInstance()
	{
		if (_sharedInstance == null)
			_sharedInstance = new Base62Coder();

		return _sharedInstance;
	}

	/**
	 * It is recommended to use a shared instance instead.
	 * @see #sharedInstance()
	 */
	public Base62Coder()
	{
		char[] symbols = new char[62];
		char c = '0';
		for (int i = 0; i < 10; ++i) {
			symbols[i] = c++;
		}
		c = 'A';
		for (int i = 10; i < 36; ++i) {
			symbols[i] = c++;
		}
		c = 'a';
		for (int i = 36; i < 62; ++i) {
			symbols[i] = c++;
		}

		init(symbols);
	}

}
