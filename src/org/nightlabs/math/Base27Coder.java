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
 * This en/decoder is a very special one. If using it with <code>skip0 == false</code>, it
 * behaves just like a normal {@link BaseNCoder} using the base 27 and the digits
 * <code>'0', 'a', 'b'...'z'</code>.
 * <p>
 * But if <code>skip0 == true</code>, the first
 * digit (i.e. <code>'0'</code>) will be skipped and only the digits <code>'a'...'z'</code> used. Note, that if you call
 * {@link #encode(long, int) } with a <code>minDigitCount</code> higher than needed, the leading digits will
 * still be <code>'0'</code> however!
 * </p>
 * <p>
 * Here's a small table showing how numbers are encoded:
 * <table border="0" cellspacing="0" cellpadding="0">
 * <tr><td align="right">0</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">a</td><td>&nbsp;(or "00a", if <code>minDigitCount == 3</code>)</td></tr>
 * <tr><td align="right">1</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">b</td></tr>
 * <tr><td align="right">2</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">c</td></tr>
 *
 * <tr><td align="right">...</td><td>&nbsp;</td><td align="right">&nbsp;</td></tr>
 *
 * <tr><td align="right">24</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">y</td></tr>
 * <tr><td align="right">25</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">z</td></tr>
 * <tr><td align="right">26</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">aa</td></tr>
 * <tr><td align="right">27</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">ab</td></tr>
 *
 * <tr><td align="right">...</td><td>&nbsp;</td><td align="right">&nbsp;</td></tr>
 *
 * <tr><td align="right">49</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">ax</td></tr>
 * <tr><td align="right">50</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">ay</td></tr>
 * <tr><td align="right">51</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">az</td></tr>
 * <tr><td align="right">52</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">ba</td></tr>
 * <tr><td align="right">53</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">bb</td></tr>
 * <tr><td align="right">54</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">bc</td></tr>
 *
 * <tr><td align="right">...</td><td>&nbsp;</td><td align="right">&nbsp;</td></tr>
 *
 * <tr><td align="right">76</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">by</td></tr>
 * <tr><td align="right">77</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">bz</td></tr>
 * <tr><td align="right">78</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">ca</td></tr>
 * <tr><td align="right">79</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">cb</td></tr>
 *
 * <tr><td align="right">...</td><td>&nbsp;</td><td align="right">&nbsp;</td></tr>
 *
 * <tr><td align="right">699</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">zx</td></tr>
 * <tr><td align="right">700</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">zy</td></tr>
 * <tr><td align="right">701</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">zz</td></tr>
 * <tr><td align="right">702</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">aaa</td></tr>
 * <tr><td align="right">703</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">aab</td></tr>
 * <tr><td align="right">704</td><td>&nbsp;=&gt;&nbsp;</td><td align="right">aac</td></tr>
 *
 * <tr><td align="right">...</td><td>&nbsp;</td><td align="right">&nbsp;</td></tr>
 * </table>
 * </p>
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class Base27Coder
extends BaseNCoder
{
	/**
	 * 0: lower case && !skip0 == false
	 * 1: lower case && skip0 == true
	 * 2: upper case && !skip0 == false
	 * 3: upper case && skip0 == true
	 */
	private static Base27Coder[] sharedInstances = new Base27Coder[4];

	/**
	 * You can either use your own instance or a shared instance of this class.
	 * It makes sense, to use the shared instance, because the methods are
	 * non-blocking (not synchronized working only on their stack or reading
	 * shared data) anyway.
	 *
	 * @param upperCase If <code>false</code>, the alphabet will be <code>'0', 'a'...'z'</code>.
	 *		If <code>true</code>, the symbols <code>'0', 'A'...'Z'</code> will be used.
	 * @param skip0 If <code>false</code>, this class will work as a normal {@link BaseNCoder} with the
	 *		base 27 and the alphabet <code>'0', 'a'...'z'</code> (lower or upper case). If <code>true</code>
	 *		however, the '0' will be skipped.
	 */
	public static Base27Coder sharedInstance(boolean upperCase, boolean skip0)
	{
		int idx = upperCase ? 2 : 0;
		if (skip0)
			++idx;

		if (sharedInstances[idx] == null)
			sharedInstances[idx] = new Base27Coder(upperCase, skip0);
	
		return sharedInstances[idx];
	}


	protected boolean skip0;

	public Base27Coder(boolean upperCase, boolean skip0)
	{
		this.skip0 = skip0;

		char[] symbols = new char[27];
		symbols[0] = '0';
		char c = upperCase ? 'A' : 'a';
		for (int i = 1; i < symbols.length; ++i) {
			symbols[i] = c++;
		}

		init(symbols);
	}

	@Override
	public String encode(long number, int minDigitCount)
	{
		if (skip0) {
			int base = getBase();
			int period = base - 1;

			long n = number;
			long m = number + 1;

			int digitIdx = 0;
			while (n >= period) {
				long divisor = (long)Math.pow(period, digitIdx + 1);
				long f = n / divisor;
				n = n - divisor;
				m = m + (long)Math.pow(base, digitIdx) * f;
				++digitIdx;
			}
			number = m;
		}

		return super.encode(number, minDigitCount);
	}

	@Override
	public long decode(String s)
	{
		long number = super.decode(s);

		if (skip0) {
			// We use interval finding mechanism. If someone finds a better inverse function, please implement it!
			long a = 0;
			long b = number;
			long middle = b / 2;
			long v = super.decode(encode(middle, 1));
			while (v != number) {
				if (v > number)
					b = middle;
				else
					a = middle;

				middle = (b - a) / 2 + a;

				v = super.decode(encode(middle, 1));
			}

			number = middle;
		}

		return number;
	}

//	public static void main(String[] args)
//	{
//		Base27Coder b = new Base27Coder(false, true);
//
//		for (int i = 0; i < 1000; ++i) {
//			System.out.print("" + i);
//			String s = b.encode(i, 3);
//			long m = b.decode(s);
////			System.out.println("\t" + s + "\t" + m	+ "\t" );
//			String d;
//			if (s.startsWith("a"))
//				d = s.substring(1);
//			else
//				d = s;
//
//			if (d.length() > 0) {
//				StringBuffer sb = new StringBuffer(d);
//				for (int c = d.length() - 1; c >= 0; --c) {
//					if (d.charAt(c) == 'a')
//						sb.replace(c, c+1, "1");
//					else
//						break;
//				}
//				d = sb.toString().replaceAll("[^1]", "");
//			}
//
////			String d = s.replaceAll("^a", "").replaceAll("a", "1").replaceAll("[^1]", "");
//			long back = b.decode(s);
//			System.out.println("\t" + s + "\t" + back + "\t" + (back - i) + "\t" + d);
////			System.out.println("\t" + s + "\t" + m	+ "\t" + Double.toString(((double)m - i) / Math.pow(3, s.length())));
////			System.out.println("\t" + s + "\t" + m	+ "\t" + (i == 0 ? "" : Double.toString((double)m / i)));
//		}
//	}
}
