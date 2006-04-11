/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

package org.nightlabs.editor2d.model;

import java.util.List;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import org.nightlabs.base.property.CheckboxPropertyDescriptor;
import org.nightlabs.base.property.IntPropertyDescriptor;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.TextDrawComponent;
import org.nightlabs.editor2d.properties.FontNamePropertyDescriptor;

public class TextPropertySource 
extends ShapeDrawComponentPropertySource 
{
	public TextPropertySource(TextDrawComponent text) {
		super(text);
	}
	
	protected TextDrawComponent getTextDrawComponent() {
		return (TextDrawComponent) drawComponent;
	}
	
	public static final String CATEGORY_FONT = EditorPlugin.getResourceString("property.category.text");	
		
	protected List createPropertyDescriptors() 
	{
		List descriptors = super.createPropertyDescriptors();
		
		// Font Name
		descriptors.add(createFontNamePD());		
		// Font Size
		descriptors.add(createFontSizePD());		
		// Bold
		descriptors.add(createBoldPD());		
		// Italic
		descriptors.add(createItalicPD());		
		// Text
		descriptors.add(createTextPD());
		
		return descriptors;
	}	
	
	protected PropertyDescriptor createFontNamePD() 
	{
		PropertyDescriptor desc = new FontNamePropertyDescriptor(TextDrawComponent.PROP_FONT_NAME,
				EditorPlugin.getResourceString("property.fontname.label"));
		desc.setCategory(CATEGORY_FONT);
		return desc;
	}
	
	protected PropertyDescriptor createFontSizePD() 
	{
//	PropertyDescriptor desc = new FontSizePropertyDescriptor(TextDrawComponent.PROP_FONT_SIZE,
//	EditorPlugin.getResourceString("property.fontsize.label"));
//  desc.setCategory(CATEGORY_FONT);		
		PropertyDescriptor desc = new IntPropertyDescriptor(TextDrawComponent.PROP_FONT_SIZE,
				EditorPlugin.getResourceString("property.fontsize.label"));
		desc.setCategory(CATEGORY_FONT);
		return desc;
	}
	
	protected PropertyDescriptor createBoldPD() 
	{
		PropertyDescriptor desc = new CheckboxPropertyDescriptor(TextDrawComponent.PROP_BOLD,
				EditorPlugin.getResourceString("property.bold.label"));
		desc.setCategory(CATEGORY_FONT);
		return desc;
	}

	protected PropertyDescriptor createItalicPD() 
	{
		PropertyDescriptor desc = new CheckboxPropertyDescriptor(TextDrawComponent.PROP_ITALIC,
				EditorPlugin.getResourceString("property.italic.label"));
		desc.setCategory(CATEGORY_FONT);
		return desc;
	}
	
	protected PropertyDescriptor createTextPD() 
	{
		PropertyDescriptor desc = new TextPropertyDescriptor(TextDrawComponent.PROP_TEXT,
				EditorPlugin.getResourceString("property.text.label"));
		desc.setCategory(CATEGORY_FONT);
		return desc;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) 
	{
		super.setPropertyValue(id, value);
		
		if (id.equals(TextDrawComponent.PROP_FONT_NAME)) {
			getTextDrawComponent().setFontName((String)value);
		}
		else if (id.equals(TextDrawComponent.PROP_FONT_SIZE)) {
			getTextDrawComponent().setFontSize(((Integer)value).intValue());
		}
		else if (id.equals(TextDrawComponent.PROP_BOLD)) {
			getTextDrawComponent().setBold(((Boolean)value).booleanValue());
		}
		else if (id.equals(TextDrawComponent.PROP_ITALIC)) {
			getTextDrawComponent().setItalic(((Boolean)value).booleanValue());
		}
		else if (id.equals(TextDrawComponent.PROP_TEXT)) {
			getTextDrawComponent().setText((String)value);
		}

	}		
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) 
	{
		Object o = super.getPropertyValue(id);
		if (o != null) 
			return o;
		else 
		{
			if (id.equals(TextDrawComponent.PROP_FONT_NAME)) {
				return getTextDrawComponent().getFontName();
			}
			else if (id.equals(TextDrawComponent.PROP_FONT_SIZE)) {
				return new Integer(getTextDrawComponent().getFontSize());
			}
			else if (id.equals(TextDrawComponent.PROP_BOLD)) {
				return new Boolean(getTextDrawComponent().isBold());
			}
			else if (id.equals(TextDrawComponent.PROP_ITALIC)) {
				return new Boolean(getTextDrawComponent().isItalic());
			}
			else if (id.equals(TextDrawComponent.PROP_TEXT)) {
				return getTextDrawComponent().getText();
			}

			return null;			
		}
	}			
}
