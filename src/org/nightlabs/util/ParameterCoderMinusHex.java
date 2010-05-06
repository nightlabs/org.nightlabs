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
package org.nightlabs.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * This implementation of {@link ParameterCoder} encodes all characters other 'a-zA-Z0-9' by
 * escaping their hex value with '{@value #ESCAPE_CHAR}'.
 * It is at least as fast as the {@link ParameterCoderURL}!
 *
 * @author Marco Schulze -- Marco[at]NightLabs[dot]de
 */
public class ParameterCoderMinusHex
implements ParameterCoder
{
	/**
	 * The character to use as an escape character
	 */
	public static final char ESCAPE_CHAR = '-';
	
	public String decode(String encoded)
	{
		try {
			StringBuffer sb = new StringBuffer();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			for (int index = 0; index < encoded.length(); ++index) {
				char c = encoded.charAt(index);
				if (c == ESCAPE_CHAR) {
					// read the next 2 chars and
					sb.setLength(0);
					sb.append(encoded.charAt(++index));
					sb.append(encoded.charAt(++index));
					int i = Integer.parseInt(sb.toString(), 16);
					bout.write(i);
				}
				else {
					bout.write((int)c); // this should be casted to int, because the bout.write(int) method must be called. Even though, there is currently no overloaded write method taking a char arg, this might change with future Java versions.
				}
			}
			bout.close();

			sb.setLength(0);
			ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			InputStreamReader r = new InputStreamReader(bin, IOUtil.CHARSET_UTF_8);
			char[] cbuf = new char[10];
			int charsRead;
			while ((charsRead = r.read(cbuf)) >= 0) {
				if (charsRead > 0)
					sb.append(cbuf, 0, charsRead);
			}
			return sb.toString();
		} catch (IOException e) {
			// writing into / reading from an in-memory-stream should never fail
			throw new RuntimeException(e);
		}
	}

	public String encode(String plain) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		Writer w = new OutputStreamWriter(bout, IOUtil.CHARSET_UTF_8);
		try {
			w.write(plain);
			w.close();
		} catch (IOException e) {
			// writing into an in-memory-stream should never fail
			throw new RuntimeException(e);
		}

		StringBuffer sb = new StringBuffer();
		for (byte bb : bout.toByteArray()) {
			int i = 0xff & bb;
			if (literalAllowed(i))
				sb.append((char)i); // this *MUST* be casted to char, because the sb.append(char) method must be called - not sb.append(int) - the result is quite different!
			else {
				sb.append(ESCAPE_CHAR);
				String s = Integer.toHexString(i);
				if (s.length() == 1) {
					sb.append('0');
					sb.append(s);
				}
				else if (s.length() == 2) {
					sb.append(s);
				}
			}
		}
		return sb.toString();
	}

	protected boolean literalAllowed(int c)
	{
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
	}

//	Small test environment
//	private static final boolean sysouts = false;
//	private static void encodeTest(String input, ParameterCoder pc)
//	{
//		if (sysouts)
//			System.out.println("input:   " + input);
//
//		String encoded = pc.encode(input);
//
//		if (sysouts)
//			System.out.println("encoded: " + encoded);
//
//		String decoded = pc.decode(encoded);
//
//		if (sysouts)
//			System.out.println("decoded: " + decoded);
//
//		if (decoded.equals(input)) {
//			if (sysouts)
//				System.out.println("input == decoded");
//		}
//		else
//			System.err.println("Input is *not* equal to decoded string!");
//	}
//
//	public static void main(String[] args) {
//		String input = "äöü#+absadlkjhfiuot823#ö432Sfdakgz21378 asksdjh awr3298 4adlkj#üpsdf023cABC091!/.:,;";
//		ParameterCoder parameterCoder = new ParameterCoderURL();
//		long start;
//
//		int cycleCount = 100000;
//
//		parameterCoder = new ParameterCoderMinusHex();
//		start = System.currentTimeMillis();
//		for (int i = 0; i < cycleCount; ++i) {
//			encodeTest(input, parameterCoder);
//		}
//		System.out.println("my: " + (System.currentTimeMillis() - start));
//
//		start = System.currentTimeMillis();
//		for (int i = 0; i < cycleCount; ++i) {
//			encodeTest(input, parameterCoder);
//		}
//		System.out.println("url: " + (System.currentTimeMillis() - start));
//
//		start = System.currentTimeMillis();
//		for (int i = 0; i < cycleCount; ++i) {
//			encodeTest(input, parameterCoder);
//		}
//		System.out.println("url: " + (System.currentTimeMillis() - start));
//
//		parameterCoder = new ParameterCoderMinusHex();
//		start = System.currentTimeMillis();
//		for (int i = 0; i < cycleCount; ++i) {
//			encodeTest(input, parameterCoder);
//		}
//		System.out.println("my: " + (System.currentTimeMillis() - start));
//	}
}
