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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * This implementation of {@link ParameterCoder} uses an {@link URLEncoder} and {@link URLDecoder}
 * to encode/decode.
 *
 * @author Marius Heinzmann -- Marius[at]NightLabs[dot]de
 */
public class ParameterCoderURL
implements ParameterCoder
{
	public String encode(String plain)
	{
		try {
			return URLEncoder.encode(plain, IOUtil.CHARSET_NAME_UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Fucking shit! How the hell can it happen that UTF-8 is not supported?!?!?!!??!???!");
		}
	}

	public String decode(String encoded)
	{
		try {
			return URLDecoder.decode(encoded, IOUtil.CHARSET_NAME_UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Fucking shit! How the hell can it happen that UTF-8 is not supported?!?!?!!??!???!");
		}
	}
}
