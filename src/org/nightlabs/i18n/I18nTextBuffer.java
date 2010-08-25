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

import java.util.HashMap;
import java.util.Map;

/**
 * This class is a simple implementation of <tt>I18nText</tt>. It is meant to be able
 * to store temporarily i18n data when not yet having a real <tt>I18nText</tt> available
 * (e.g. in a wizard before creating the real object).
 *
 * @version $Revision: 12386 $ - $Date: 2008-10-10 14:42:28 +0200 (Fr, 10 Okt 2008) $
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class I18nTextBuffer extends I18nText
{
	/**
	 * The serial version of this class.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * key: String languageID<br/>
	 * value: String text
	 */
	private Map<String, String> texts = new HashMap<String, String>();

	/**
	 * @see org.nightlabs.i18n.I18nText#getI18nMap()
	 */
	@Override
	protected Map<String, String> getI18nMap()
	{
		return texts;
	}

	private String fallBackValue = "";

	public void setFallBackValue(String fallBackValue)
	{
		if (fallBackValue == null)
			throw new NullPointerException("fallBackValue");

		this.fallBackValue = fallBackValue;
	}

	/**
	 * @see org.nightlabs.i18n.I18nText#getFallBackValue(java.lang.String)
	 */
	@Override
	protected String getFallBackValue(String languageID)
	{
		return fallBackValue;
	}

	/**
	 * Clears all entries out of this {@link I18nTextBuffer}.
	 */
	public void clear() {
		texts.clear();
	}
}
