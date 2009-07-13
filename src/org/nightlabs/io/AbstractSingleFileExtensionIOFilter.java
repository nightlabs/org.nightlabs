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
package org.nightlabs.io;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractSingleFileExtensionIOFilter
extends AbstractIOFilter
{
	@Override
	protected abstract String initDescription();

	@Override
	protected abstract String initName();

	protected abstract String initFileExtension();
	
	protected abstract boolean supportsRead();
	
	protected abstract boolean supportsWrite();
	
	@Override
	protected Map<String, I18nText> initDescriptions()
	{
		Map<String, I18nText> descriptions = new HashMap<String, I18nText>();
		I18nText description = new I18nTextBuffer();
		description.setText(Locale.ENGLISH.getLanguage(), initDescription());
		descriptions.put(initFileExtension(), description);
		return descriptions;
	}

	@Override
	protected String[] initFileExtensions() {
		return new String[] {initFileExtension()};
	}

	public boolean supportsRead(String fileExtension)
	{
		if (fileExtension.equals(getFileExtensions()[0]))
			return supportsRead();
		return false;
	}

	public boolean supportsWrite(String fileExtension)
	{
		if (fileExtension.equals(getFileExtensions()[0]))
			return supportsWrite();
		return false;
	}

}
