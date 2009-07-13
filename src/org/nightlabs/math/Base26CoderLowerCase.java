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
 * @author Marco Schulze - marco at nightlabs dot de
 * @deprecated Use {@link org.nightlabs.math.Base26Coder} instead!
 */
@Deprecated
public class Base26CoderLowerCase
extends Base26Coder
{
	private static Base26CoderLowerCase sharedInstance_With09 = null;
	private static Base26CoderLowerCase sharedInstance_No09 = null;

	/**
	 * You can either use your own instance or a shared instance of this class.
	 * It makes sense, to use the shared instance.
	 */
	public static Base26CoderLowerCase sharedInstance(boolean include09)
	{
		if (include09) {
			if (sharedInstance_With09 == null)
				sharedInstance_With09 = new Base26CoderLowerCase(include09);
	
			return sharedInstance_With09;
		}
		else {
			if (sharedInstance_No09 == null)
				sharedInstance_No09 = new Base26CoderLowerCase(include09);
	
			return sharedInstance_No09;
		}
	}

	/**
	 * @param include09 Whether or not the symbols should include '0', '1'...'9'.
	 *		If <tt>true</tt>, the first symbol will be '0' and the last 'p', if <tt>false</tt>,
	 *		the range is 'a'..'z'
	 */
	public Base26CoderLowerCase(boolean include09)
	{
		super(false, include09);
	}

}
