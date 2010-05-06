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
package org.nightlabs.i18n;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

import org.nightlabs.util.IOUtil;

/**
 * @version $Revision: 12386 $ - $Date: 2008-10-10 14:42:28 +0200 (Fr, 10 Okt 2008) $
 */
public class I18nUtil
{
	public static String getClosestL10n(Map<Locale, String> i18nMap, Locale locale)
	{
		String res = i18nMap.get(locale);
		if (res == null) {
			String res_sameLanguageAndCountry = null;
			String res_sameLanguage = null;
			String res_defaultLanguage = i18nMap.get(Locale.ENGLISH);
			String res_any = null;
			for (Map.Entry<Locale, String> me : i18nMap.entrySet()) {
				res_any = me.getValue();

				if (res_defaultLanguage == null && Locale.ENGLISH.getLanguage().equals(me.getKey().getLanguage()))
					res_defaultLanguage = me.getValue();

				if (locale.getLanguage().equals(me.getKey().getLanguage())) {
					res_sameLanguage = me.getValue();
					if (locale.getCountry().equals(me.getKey().getCountry())) {
						res_sameLanguageAndCountry = me.getValue();
					}
				}
			}

			if (res_sameLanguageAndCountry != null)
				res = res_sameLanguageAndCountry;
			else if (res_sameLanguage != null)
				res = res_sameLanguage;
			else if (res_defaultLanguage != null)
				res = res_defaultLanguage;
			else
				res = res_any;
		}
		return res;
	}

	private static byte[] getBytesFromInputStreamAndCloseInputStream(InputStream in)
	{
		if (in == null)
			return null;

		try {
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				try {
					IOUtil.transferStreamData(in, out);
				} catch (IOException e) {
					throw new RuntimeException(e);
				} finally {
					out.close();
				}
				return out.toByteArray();
			} finally {
				in.close();
			}
		} catch (IOException x) {
			// In this method, we only work with JAR-packaged resources and with memory streams - we should never
			// get an IO Exception, thus we don't declare it in the throws block but instead wrap in a RuntimeException.
			throw new RuntimeException(x);
		}
	}

	/**
	 * Get a byte array for a flag image resource that represents the specified language
	 * (2-character ISO code, e.g. "de" or "en"). You must close the returned stream!
	 * <p>
	 * The returned byte array will contain a PNG image that is smaller than or equals 16 * 16 pixels. If there is no such
	 * resource, the result is <code>null</code> or a fallback image, if the <code>fallback</code> argument was <code>true</code>.
	 * </p>
	 *
	 * @param languageID the ISO language identifier for which to get the image. This must not be <code>null</code>!
	 * @param fallback if there is no resource for the specified language and this argument is <code>true</code>,
	 * this method will return a fallback image instead of returning <code>null</code>.
	 * @return a byte array from which a PNG image representing the specified language can be read or <code>null</code>,
	 * if there is no such resource and <code>fallback</code> is <code>false</code>.
	 */
	public static byte[] getLanguageFlag16x16(String languageID, boolean fallback)
	{
		InputStream in = openLanguageFlag16x16InputStream(languageID, fallback);
		return getBytesFromInputStreamAndCloseInputStream(in);
	}

	/**
	 * Get a byte array for a flag image resource that represents the specified country
	 * (2-character ISO code, e.g. "de" or "en"). You must close the returned stream!
	 * <p>
	 * The returned byte array will contain a PNG image that is smaller than or equals 16 * 16 pixels. If there is no such
	 * resource, the result is <code>null</code> or a fallback image, if the <code>fallback</code> argument was <code>true</code>.
	 * </p>
	 *
	 * @param countryID the ISO country identifier for which to get the image. This must not be <code>null</code>!
	 * @param fallback if there is no resource for the specified language and this argument is <code>true</code>,
	 * this method will return a fallback image instead of returning <code>null</code>.
	 * @return a byte array from which a PNG image representing the specified language can be read or <code>null</code>,
	 * if there is no such resource and <code>fallback</code> is <code>false</code>.
	 */
	public static byte[] getCountryFlag16x16(String languageID, boolean fallback)
	{
		InputStream in = openCountryFlag16x16InputStream(languageID, fallback);
		return getBytesFromInputStreamAndCloseInputStream(in);
	}

	/**
	 * Open an {@link InputStream} for a flag image resource that represents the specified language
	 * (2-character ISO code, e.g. "de" or "en"). You must close the returned stream!
	 * <p>
	 * The returned resource stream will contain a PNG image that is smaller than or equals 16 * 16 pixels. If there is no such
	 * resource, the result is <code>null</code> or a fallback image, if the <code>fallback</code> argument was <code>true</code>.
	 * </p>
	 *
	 * @param languageID the ISO language identifier for which to get the image. This must not be <code>null</code>!
	 * @param fallback if there is no resource for the specified language and this argument is <code>true</code>,
	 * this method will return a fallback image instead of returning <code>null</code>.
	 * @return an {@link InputStream} from which a PNG image representing the specified language can be read or <code>null</code>,
	 * if there is no such resource and <code>fallback</code> is <code>false</code>.
	 */
	public static InputStream openLanguageFlag16x16InputStream(String languageID, boolean fallback)
	{
		if (languageID == null)
			throw new IllegalArgumentException("languageID must not be null!");

		String flagResource = "resource/language/" + languageID + ".png";
		InputStream in = I18nUtil.class.getResourceAsStream(flagResource);

		if (in == null) {
			flagResource = "resource/language/" + languageID.toLowerCase() + ".png";
			in = I18nUtil.class.getResourceAsStream(flagResource);
		}

		if (in == null) {
			flagResource = "resource/language/" + languageID.toUpperCase() + ".png";
			in = I18nUtil.class.getResourceAsStream(flagResource);
		}

		if (in == null && fallback) {
			flagResource = "resource/Flag-fallback.16x16.png";
			in = I18nUtil.class.getResourceAsStream(flagResource);
		}

		return in;
	}

	/**
	 * Open an {@link InputStream} for a flag image resource that represents the specified country
	 * (2-character ISO code, e.g. "DE" or "GB"). You must close the returned stream!
	 * <p>
	 * The returned resource stream will contain a PNG image that is smaller than or equals 16 * 16 pixels. If there is no such
	 * resource, the result is <code>null</code> or a fallback image, if the <code>fallback</code> argument was <code>true</code>.
	 * </p>
	 *
	 * @param countryID the ISO country identifier for which to get the image. This must not be <code>null</code>!
	 * @param fallback if there is no resource for the specified language and this argument is <code>true</code>,
	 * this method will return a fallback image instead of returning <code>null</code>.
	 * @return an {@link InputStream} from which a PNG image representing the specified language can be read or <code>null</code>,
	 * if there is no such resource and <code>fallback</code> is <code>false</code>.
	 */
	public static InputStream openCountryFlag16x16InputStream(String countryID, boolean fallback)
	{
		if (countryID == null)
			throw new IllegalArgumentException("countryID must not be null!");

		String flagResource = "resource/country/" + countryID + ".png";
		InputStream in = I18nUtil.class.getResourceAsStream(flagResource);

		if (in == null) {
			flagResource = "resource/country/" + countryID.toLowerCase() + ".png";
			in = I18nUtil.class.getResourceAsStream(flagResource);
		}

		if (in == null) {
			flagResource = "resource/country/" + countryID.toUpperCase() + ".png";
			in = I18nUtil.class.getResourceAsStream(flagResource);
		}

		if (in == null && fallback) {
			flagResource = "resource/Flag-fallback.16x16.png";
			in = I18nUtil.class.getResourceAsStream(flagResource);
		}

		return in;
	}
}
