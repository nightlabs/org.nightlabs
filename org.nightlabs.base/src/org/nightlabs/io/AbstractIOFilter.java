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
import java.util.Map;

import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.util.NLLocale;


public abstract class AbstractIOFilter
implements IOFilter
{
	public AbstractIOFilter()
	{
		super();
		fileExtensions = initFileExtensions();
		fileExtension2Description = initDescriptions();
		getName().setText(NLLocale.getDefault().getLanguage(), initName());
		getDescription().setText(NLLocale.getDefault().getLanguage(), initDescription());
	}
	
	private String[] fileExtensions;
	public String[] getFileExtensions()
	{
		if (fileExtensions == null)
			fileExtensions = new String[0];
		return fileExtensions;
	}
	public void setFileExtensions(String[] fileExtensions) {
		this.fileExtensions = fileExtensions;
	}

	private Map<String, I18nText> fileExtension2Description;
	private Map<String, I18nText> getFileExtension2Description() {
		if (fileExtension2Description == null)
			fileExtension2Description = new HashMap<String, I18nText>();
		return fileExtension2Description;
	}
	
	private I18nText name;
	public I18nText getName() {
		if (name == null)
			name = new I18nTextBuffer();
		return name;
	}
	public void setName(I18nText name) {
		this.name = name;
	}
	
	private I18nText description;
	public I18nText getDescription()
	{
		if (description == null)
			description = new I18nTextBuffer();
		return description;
	}
	public void setDescription(I18nText description) {
		this.description = description;
	}
	
	public I18nText getFileExtensionDescription(String fileExtension) {
		return getFileExtension2Description().get(fileExtension);
	}
	public void setFileExtensionDescription(String fileExtension, I18nText description) {
		getFileExtension2Description().put(fileExtension, description);
	}
	
	private IOFilterInformationProvider informationProvider;
	public IOFilterInformationProvider getInformationProvider() {
		return informationProvider;
	}
	public void setInformationProvider(IOFilterInformationProvider provider) {
		this.informationProvider = provider;
	}
	
	/**
	 * returns a String[] containing all supported fileExtensions
	 * @return a String[] containing all supported fileExtensions
	 */
	protected abstract String[] initFileExtensions();
	/**
	 * returns a {@link Map} containing all supported fileExtensions as key
	 * and the corresponding descrption as value
	 * 
	 * @return a {@link Map} containing all supported fileExtensions as key
	 * and the corresponding descrption as value
	 */
	protected abstract Map<String, I18nText> initDescriptions();
	
	/**
	 * returns the name of the IOFilter
	 * @return the name of the IOFilter
	 */
	protected abstract String initName();
	
	/**
	 * returns the description of the IOFilter
	 * @return the description of the IOFilter
	 */
	protected abstract String initDescription();
}
