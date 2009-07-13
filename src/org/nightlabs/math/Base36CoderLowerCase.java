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
 * This Coder works with the base 36 where the first 10 digits are represented
 * by '0'...'9' and the rest by 'a'...'z'.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 * @deprecated Use {@link org.nightlabs.math.Base36Coder} instead!
 */
@Deprecated
public class Base36CoderLowerCase
extends Base36Coder
{
	private static Base36CoderLowerCase _sharedInstance = null;

	/**
	 * You can either use your own instance or a shared instance of this class.
	 * It makes sense, to use the shared instance.
	 */
	public static Base36CoderLowerCase sharedInstance()
	{
		if (_sharedInstance == null)
			_sharedInstance = new Base36CoderLowerCase();

		return _sharedInstance;
	}

	public Base36CoderLowerCase()
	{
		super(false);
	}

}
