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
package org.nightlabs.language;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.i18n.I18nUtil;
import org.nightlabs.util.Util;

//TODO: use language and country (complete locale)
public class LanguageCf
implements Serializable
{
	/**
	 * The serial version of this class.
	 */
	private static final long serialVersionUID = 1L;

	private String languageID = null;

	public String getLanguageID() {
		return languageID;
	}

	public void setLanguageID(String languageID) {
		this.languageID = languageID;
	}

	/**
	 * @deprecated Constructor only for deserialization!
	 */
	@Deprecated
	public LanguageCf()
	{
	}

	public LanguageCf(String languageID)
	{
		this.languageID = languageID;
	}

	private String nativeName = null;

	public String getNativeName()
	{
//		if (nativeName == null) {
//			Locale l = new Locale(languageID);
//			nativeName = l.getDisplayLanguage();
//		}

		return nativeName;
	}

	public void setNativeName(String nativeName) {
		this.nativeName = nativeName;
	}

	private I18nTextBuffer name = null;

	public I18nTextBuffer getName()
	{
		return name;
	}

	private transient Map<String, String> names = null;

	/**
	 * @deprecated Do not call this method! It exists only for the {@link Config} system (in order to allow the bean-serializer to read+write XML files).
	 */
	@Deprecated
	public Map<String, String> getNames()
	{
		if (names == null && name != null) {
			names = new HashMap<String, String>();
			for (Map.Entry<String, String> me : name.getTexts())
				names.put(me.getKey(), me.getValue());
		}

		return names;
	}
	/**
	 * @deprecated Do not call this method! It exists only for the {@link Config} system (in order to allow the bean-serializer to read+write XML files).
	 */
	@Deprecated
	public void setNames(Map<String, String> names)
	{
		this.names = names;
	}

	private String flagIcon16x16 = null;

	public String getFlagIcon16x16()
	{
		return flagIcon16x16;
	}
	public void setFlagIcon16x16(String flagIcon16x16)
	{
		this.flagIcon16x16 = flagIcon16x16;
	}

	public byte[] _getFlagIcon16x16()
	{
		if (flagIcon16x16 == null)
			return null;

		return Util.decodeHexStr(flagIcon16x16);
	}
	public void _setFlagIcon16x16(byte[] flagIcon16x16)
	{
		if (flagIcon16x16 == null)
			this.flagIcon16x16 = null;
		else
			this.flagIcon16x16 = Util.encodeHexStr(flagIcon16x16);
	}

	/**
	 * @return <code>true</code> if the instance has been modified.
	 */
	public boolean init(Set<String> languageIDs)
	{
		boolean modified = false;

		Locale locale = null;
		if (nativeName == null && languageID != null) {
			if (locale == null)
				locale = new Locale(languageID);

			nativeName = locale.getDisplayLanguage(locale);
			modified = true;
		}

		name = new I18nTextBuffer();

		if (names != null) {
			for (Map.Entry<String, String> me : names.entrySet())
				name.setText(me.getKey(), me.getValue());
		}
		names = null;

		if (languageID != null && (!name.containsLanguageID(languageID) || !nativeName.equals(name.getText(languageID)))) {
			modified = true;
			name.setText(languageID, nativeName);
		}

		if (languageID != null && languageIDs != null) {
			if (locale == null)
				locale = new Locale(languageID);

			for (String lid : languageIDs) {
				if (!name.containsLanguageID(lid)) {
					name.setText(lid, locale.getDisplayLanguage(new Locale(lid)));
					modified = true;
				}
			}
		} // if (languageIDs != null) {

		if (flagIcon16x16 == null) {
			_setFlagIcon16x16(I18nUtil.getLanguageFlag16x16(languageID, true));
////			String flagResource = "resource/Flag-" + languageID + ".16x16.png";
//			String flagResource = "resource/language/" + languageID + ".png";
//			try {
//				InputStream in = I18nUtil.class.getResourceAsStream(flagResource);
//				if (in != null) {
//					try {
//						_setFlagIcon16x16(new DataBuffer(in).createByteArray());
//					} finally {
//						in.close();
//					}
//					modified = true;
//				}
//			} catch (IOException x) {
//				flagIcon16x16 = null;
//				Logger.getLogger(LanguageCf.class).warn("Loading resource \"" + flagResource + "\" failed!", x);
//			}
		}

		return modified;
	}
}
