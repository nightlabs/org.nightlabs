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
package org.nightlabs.editor2d.model;

import java.util.List;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.nightlabs.base.property.ComboBoxPropertyDescriptor;
import org.nightlabs.base.property.DoublePropertyDescriptor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.PageDrawComponent;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class PagePropertySource 
extends DrawComponentPropertySource 
{

	public PagePropertySource(PageDrawComponent element) {
		super(element);
	}

	protected PageDrawComponent getPageDrawComponent() {
		return (PageDrawComponent) drawComponent;
	}
	
	protected List createPropertyDescriptors() 
	{
		List descriptors = getDescriptors();
		
		// Name
		descriptors.add(createNamePD());			
		// Resolution
		descriptors.add(createResolutionPD());
		// Orientation
		descriptors.add(createOrientationPD());
		
		return descriptors;
	}
	
	protected PropertyDescriptor createResolutionPD() 
	{
		PropertyDescriptor desc = new DoublePropertyDescriptor(PageDrawComponent.PROP_RESOLUTION, 
				EditorPlugin.getResourceString("property.resolution.label"), true);
		return desc;
	}
	
	public static String getOrientationString(int orientation) 
	{
		if (orientation == PageDrawComponent.ORIENTATION_HORIZONTAL)
			return EditorPlugin.getResourceString("property.orientation.horizontal");
		if (orientation == PageDrawComponent.ORIENTATION_VERTICAL)
			return EditorPlugin.getResourceString("property.orientation.vertical");
		
		return null;
	}

	public static int getOrientation(String orientationString) 
	{
		if (orientationString.equals(EditorPlugin.getResourceString("property.orientation.horizontal")))
			return PageDrawComponent.ORIENTATION_HORIZONTAL;
		if (orientationString.equals(EditorPlugin.getResourceString("property.orientation.vertical")))
			return PageDrawComponent.ORIENTATION_VERTICAL;
		
		return -1;
	}
	
	
	protected PropertyDescriptor createOrientationPD()
	{
		String horizontal = getOrientationString(PageDrawComponent.ORIENTATION_HORIZONTAL);
		String vertical = getOrientationString(PageDrawComponent.ORIENTATION_VERTICAL);		
		String[] values = new String[] {horizontal, vertical};
		PropertyDescriptor desc = new ComboBoxPropertyDescriptor(PageDrawComponent.PROP_ORIENTATION,
				EditorPlugin.getResourceString("property.orientation.label"), values);
		return desc;
	}	
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) 
	{
		if (id.equals(PageDrawComponent.PROP_RESOLUTION)) {
			return getPageDrawComponent().getResolution().getResolution();
		}
		else if (id.equals(PageDrawComponent.PROP_ORIENTATION)) {
			return getOrientationString(getPageDrawComponent().getOrientation());
		}
		else if (id.equals(DrawComponent.PROP_NAME)) {
			return drawComponent.getI18nText().getText(nameLangMan.getCurrentLanguageID());
		}			
		return null;
	}
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */	
	public void setPropertyValue(Object id, Object value) 
	{
		if (id.equals(PageDrawComponent.PROP_RESOLUTION)) 
		{
			getPageDrawComponent().getResolution().setResolution(((Double)value).doubleValue());
		}
		else if (id.equals(PageDrawComponent.PROP_ORIENTATION)) {
			int newOrientation = getOrientation((String)value);
			getPageDrawComponent().setOrientation(newOrientation);
		}
		else if (id.equals(DrawComponent.PROP_NAME)) {
			drawComponent.setName((String)value);
		}		
	}
}
