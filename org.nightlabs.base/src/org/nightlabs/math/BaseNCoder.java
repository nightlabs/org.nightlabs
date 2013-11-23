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

import java.util.HashMap;
import java.util.Map;

/**
 * An instance of this class can be used to encode and decode numbers with a
 * definable alphabet of symbols.
 * <p>
 * <u>Examples:</u>
 * <ul>
 * <li>If you use the symbols <code>'0', '1', '2' ... '9'</code>,
 * {@link #encode(long, int) } will have the same result as using {@link Long#toString()}.
 * </li>
 * <li>
 * With the symbols
 * <code>'0', '1' ... '9', 'a', 'b' ... 'f'</code>, the encoding is the same
 * as {@link Long#toHexString(long)}.
 * </li>
 * </ul>
 * </p>
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class BaseNCoder
{
	private int base;
	private char[] symbols = null;
	private Map<Character, Integer> symbolMap = new HashMap<Character, Integer>();

	/**
	 * Constructor that can be used in child-classes. Note, that you
	 * MUST call {@link #init(char[]) } yourself then, because this
	 * constructor is empty (doesn't do anything).
	 */
	protected BaseNCoder()
	{
	}

	/**
	 * This constructor creates a <code>BaseNCoder</code> with a free-definable
	 * alphabet of symbols.
	 *
	 * @param digitSymbols The list of symbols used to express the digits.
	 *		Each <tt>char</tt> represents one digit with the value represented
	 *		by the array index.
	 */
	public BaseNCoder(char[] digitSymbols)
	{
		init(digitSymbols);
	}

	/**
	 * @param digitSymbols The list of symbols used to express the digits.
	 *		Each <tt>char</tt> represents one digit with the value represented
	 *		by the array index.
	 */
	protected void init(char[] digitSymbols)
	{
		this.symbols = digitSymbols;
		base = digitSymbols.length;
		symbolMap.clear();
		for (int i = 0; i < digitSymbols.length; ++i) {
			char c = digitSymbols[i];

			if (symbolMap.put(Character.valueOf(c), Integer.valueOf(i)) != null)
				throw new IllegalArgumentException("Collision: The character '" + c + "' occurs at least twice in list of digitSymbols!");
		}
	}

	/**
	 * Encodes a number using this <code>BaseNCoder</code>'s alphabet of symbols.
	 *
	 * @param number The number to encode.
	 * @return Returns a string representing the given <code>number</code> encoded using the
	 *		symbols of the alphabet as digits.
	 */
	public String encode(long number)
	{
		return encode(number, 1);
	}
	
	/**
	 * Encodes a number using this <code>BaseNCoder</code>'s alphabet of symbols.
	 *
	 * @param number The number to encode.
	 * @param minDigitCount The minimum number of digits (the first symbol will be prepended,
	 *		till the desired length is reached). Minimum value is 1.
	 * @return Returns a string representing the given <code>number</code> encoded using the
	 *		symbols of the alphabet as digits.
	 */
	public String encode(long number, int minDigitCount)
	{
		if (symbols == null)
			throw new IllegalStateException("symbols undefined! Call init(...) first!");
		if(minDigitCount < 1)
			throw new IllegalArgumentException("minDigitCount out of bounds (must be > 0): "+minDigitCount);

		boolean numberStarted = false;

		int digitCount = number < base ? 1 : 1 + (int)MathUtil.log(base, number);
		if (digitCount < minDigitCount)
			digitCount = minDigitCount;

		long divisor;
		int digitValue;
		StringBuffer res = new StringBuffer();
		for (int digitIdx = digitCount - 1; digitIdx >= 0; --digitIdx) {
			divisor = (long)Math.pow(base, digitIdx);
			digitValue = (int)(number / divisor);
			if (numberStarted || digitIdx < minDigitCount || digitValue > 0) {
				numberStarted = true;
				number = number - (digitValue * divisor);
				res.append(symbols[digitValue]);
			}
		}
		return res.toString();
	}

	/**
	 * Parses and decodes a string that has been created by {@link #encode(long, int) } before.
	 *
	 * @param s The encoded representation of the number.
	 * @return Returns the value of the encoded number according to this <code>BaseNCoder</code>'s alphabet.
	 */
	public long decode(String s)
	{
		if (symbols == null)
			throw new IllegalStateException("symbols undefined! Call init(...) first!");

		int digitIdx = 0;
		long res = 0;
		for (int i = s.length() - 1; i >= 0; --i) {
			char c = s.charAt(i);
			Integer digitValue = symbolMap.get(Character.valueOf(c));
			if (digitValue == null)
				throw new NumberFormatException("Unknown digit: " + c);

			res = res + (digitValue.intValue()) * (long)Math.pow(base, digitIdx);
			++digitIdx;
		}
		return res;
	}

	/**
	 * @return Returns the base, which is the number of symbols in the alphabet.
	 *		For example, using the symbols <code>'0', '1' ... '9'</code>, the base
	 *		will be 10 (= ordinary decimal system).
	 */
	public int getBase()
	{
		return base;
	}
}
