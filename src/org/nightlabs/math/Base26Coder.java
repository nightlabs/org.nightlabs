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
 * This number en/decoder works with the base 26 using either the alphabet
 * <code>'a'...'z'</code> or <code>'0'...'9', 'a'...'p'</code>
 * (lower or upper case).
 *
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class Base26Coder
extends BaseNCoder
{
	/**
	 * 0: lower case without 0..9
	 * 1: lower case with 0..9
	 * 2: upper case without 0..9
	 * 3: upper case with 0..9
	 */
	private static Base26Coder[] sharedInstances = new Base26Coder[4];

	/**
	 * Get a base 26 coder shared instance.
	 * <p>
	 * You can either use your own instance or a shared instance of this class.
	 * It makes sense, to use the shared instance, because the methods are
	 * non-blocking (not synchronized working only on their stack or reading
	 * shared data) anyway.
	 * </p>
	 * @param upperCase Whether to use upper case.
	 * @param beginWithZero Whether or not the symbols should include '0', '1'...'9'.
	 *		If <tt>true</tt>, the first symbol will be '0' and the last 'p', if <tt>false</tt>,
	 *		the range is 'a'..'z'
	 */
	public static Base26Coder sharedInstance(boolean upperCase, boolean beginWithZero)
	{
		int idx = upperCase ? 2 : 0;
		if (beginWithZero)
			++idx;

		if (sharedInstances[idx] == null)
			sharedInstances[idx] = new Base26Coder(upperCase, beginWithZero);
	
		return sharedInstances[idx];
	}

	/**
	 * Get a base 26 coder shared instance. It will use a symbol table from 'a'-'z'
	 * or 'A'-'Z' depending on the <code>upperCase</code> parameter.
	 * <p>
	 * You can either use your own instance or a shared instance of this class.
	 * It makes sense, to use the shared instance, because the methods are
	 * non-blocking (not synchronized working only on their stack or reading
	 * shared data) anyway.
	 * </p>
	 * @param upperCase Whether to use upper case.
	 */
	public static Base26Coder sharedInstance(boolean upperCase)
	{
		return sharedInstance(upperCase, false);
	}
	
	/**
	 * Create a new base 26 coder.
	 * @param upperCase Whether to use upper case.
	 * @param beginWithZero Whether or not the symbols should include '0', '1'...'9'.
	 *		If <tt>true</tt>, the first symbol will be '0' and the last 'p', if <tt>false</tt>,
	 *		the range is 'a'..'z'
	 */
	public Base26Coder(boolean upperCase, boolean beginWithZero)
	{
		char[] symbols = new char[26];
		char firstChar = upperCase ? 'A' : 'a';
		if (beginWithZero) {
			char c = '0';
			for (int i = 0; i < 10; ++i) {
				symbols[i] = c++;
			}
			c = firstChar;
			for (int i = 10; i < symbols.length; ++i) {
				symbols[i] = c++;
			}
		}
		else {
			char c = firstChar;
			for (int i = 0; i < symbols.length; ++i) {
				symbols[i] = c++;
			}
		}

		init(symbols);
	}

	/**
	 * Create a new base 26 coder. It will use a symbol table from 'a'-'z'
	 * or 'A'-'Z' depending on the <code>upperCase</code> parameter.
	 * @param upperCase Whether to use upper case.
	 */
	public Base26Coder(boolean upperCase)
	{
		this(upperCase, false);
	}
}
