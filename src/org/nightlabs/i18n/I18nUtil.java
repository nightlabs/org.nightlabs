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

import java.util.Locale;
import java.util.Map;

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
}
