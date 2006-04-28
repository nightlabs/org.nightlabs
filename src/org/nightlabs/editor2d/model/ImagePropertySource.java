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
import org.nightlabs.base.property.IntPropertyDescriptor;
import org.nightlabs.base.property.XTextPropertyDescriptor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.properties.RotationPropertyDescriptor;

public class ImagePropertySource 
extends DrawComponentPropertySource
{
	public ImagePropertySource(ImageDrawComponent element) {
		super(element);
	}

	protected ImageDrawComponent getImageDrawComponent() {
		return (ImageDrawComponent) drawComponent;
	}
	
	protected List createPropertyDescriptors() 
	{
		List descriptors = getDescriptors();
		// Name
		descriptors.add(createNamePD());
		// File Name
		descriptors.add(createFileNamePD());
		// X
		descriptors.add(createXPD());		
		// Y		
		descriptors.add(createYPD());		
		// Width		
		descriptors.add(createWidthPD());		
		// Height		
		descriptors.add(createHeightPD());		
		// Rotation		
		descriptors.add(createRotationPD());	
		
		return descriptors;
	}	
	
	public Object getPropertyValue(Object id) 
	{
		if (id.equals(ImageDrawComponent.PROP_ORIGINAL_FILE_NAME)) {
			return getImageDrawComponent().getOriginalImageFileName();
		}
		return super.getPropertyValue(id);
	}	
	
//	public void setPropertyValue(Object id, Object value) 
//	{
//		super.setPropertyValue(id, value);
//	}	
	
	protected PropertyDescriptor createFileNamePD() 
	{
		PropertyDescriptor pd = new XTextPropertyDescriptor(
				ImageDrawComponent.PROP_ORIGINAL_FILE_NAME, 
				EditorPlugin.getResourceString("property.originalImageFileName.label"), 
				true);
		pd.setCategory(CATEGORY_NAME);
		return pd;
	}
}
