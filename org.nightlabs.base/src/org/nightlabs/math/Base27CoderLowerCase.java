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
 * @deprecated Use {@link org.nightlabs.math.Base27Coder} instead!
 */
@Deprecated
public class Base27CoderLowerCase
extends Base27Coder
{
	private static Base27CoderLowerCase sharedInstance_WithSkip0 = null;
	private static Base27CoderLowerCase sharedInstance_NoSkip0 = null;

	/**
	 * You can either use your own instance or a shared instance of this class.
	 * It makes sense, to use the shared instance.
	 */
	public static Base27CoderLowerCase sharedInstance(boolean skip0)
	{
		if (skip0) {
			if (sharedInstance_WithSkip0 == null)
				sharedInstance_WithSkip0 = new Base27CoderLowerCase(skip0);
	
			return sharedInstance_WithSkip0;
		}
		else {
			if (sharedInstance_NoSkip0 == null)
				sharedInstance_NoSkip0 = new Base27CoderLowerCase(skip0);
	
			return sharedInstance_NoSkip0;
		}
	}


	protected boolean skip0;

	public Base27CoderLowerCase(boolean skip0)
	{
		super(false, skip0);
	}
}
