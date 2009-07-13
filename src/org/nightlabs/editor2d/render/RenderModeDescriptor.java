/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.editor2d.render;

import java.awt.image.BufferedImage;

import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.util.NLLocale;

/**
 * A Descriptor for a RenderMode
 * 
 * @author Daniel.Mazurek [AT] NightLabs [DOT] com
 *
 */
public class RenderModeDescriptor
{

	public RenderModeDescriptor(String renderMode, String localizedText)
	{
		this(renderMode, localizedText, null, null);
	}
	
	public RenderModeDescriptor(String renderMode, String localizedText, String localizedDesc)
	{
		this(renderMode, localizedText, localizedDesc, null);
	}
	
	public RenderModeDescriptor(String renderMode, String localizedText, String localizedDesc,
		BufferedImage img)
	{
		this.renderMode = renderMode;
		setLocalizedText(localizedText);
		setLocalizedDescription(localizedDesc);
		this.image = img;
	}
	
	public RenderModeDescriptor(String renderMode, I18nText text) {
		this(renderMode, text, null, null);
	}
	
	public RenderModeDescriptor(String renderMode, I18nText text, I18nText description) {
		this(renderMode, text, description, null);
	}

	public RenderModeDescriptor(String renderMode, I18nText text, I18nText description, BufferedImage img) {
		super();
		this.renderMode = renderMode;
		this.text = text;
		this.image = img;
	}
	
	protected String renderMode = RenderConstants.DEFAULT_MODE;
	/**
	 * 
	 * @return the renderMode
	 */
	public String getRenderMode() {
		return renderMode;
	}
	
	protected String getLanguageID() {
		return NLLocale.getDefault().getLanguage();
	}
	
	protected I18nText text = null;
	/**
	 * 
	 * @return the I18nText which stores the localized text for the renderMode
	 */
	public I18nText getText()
	{
		if (text == null)
			text = new I18nTextBuffer();
		
		return text;
	}
	public void setText(I18nText text) {
		this.text = text;
	}
	
	/**
	 * 
	 * @param text the localized (NLLocale.getDefault().getLanguage()) Text for
	 * the renderMode
	 */
	public void setLocalizedText(String text) {
		getText().setText(getLanguageID(), text);
	}
	
	public String getLocalizedText() {
		return getText().getText(getLanguageID());
	}
	
	
	protected I18nText description = null;
	/**
	 * 
	 * @return the I18nText which stores the localized description for the renderMode
	 */
	public I18nText getDescription()
	{
		if (description == null)
			description = new I18nTextBuffer();
		
		return description;
	}
	public void setDescription(I18nText description) {
		this.description = description;
	}
	
	/**
	 * 
	 * @param desc the localized (NLLocale.getDefault().getLanguage())
	 * Descriptor for the renderMode
	 */
	public void setLocalizedDescription(String desc) {
		getDescription().setText(getLanguageID(), desc);
	}
	
	public String getLocalizedDescription() {
		return getDescription().getText(getLanguageID());
	}
	
	protected BufferedImage image;
	/**
	 * 
	 * @return the BufferedImage (Icon) for the renderMode
	 */
	public BufferedImage getImage() {
		return image;
	}
	/**
	 * 
	 * @param image the BufferedImage (Icon) for the renderMode
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
}
