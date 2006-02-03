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

	protected List createPropertyDescriptors() 
	{
		List descriptors = getDescriptors();
		
//		// Name
//		PropertyDescriptor desc = new NamePropertyDescriptor(drawComponent,
//				DrawComponent.PROP_NAME,
//				EditorPlugin.getResourceString("property.name.label"));		
//		desc.setCategory(CATEGORY_NAME);
//		descriptors.add(desc);		
		
		// X
		PropertyDescriptor desc = new IntPropertyDescriptor(DrawComponent.PROP_X,
				EditorPlugin.getResourceString("property.x.label"));
		desc.setCategory(CATEGORY_GEOM);		
		descriptors.add(desc);
		
		// Y		
		desc = new IntPropertyDescriptor(DrawComponent.PROP_Y,
				EditorPlugin.getResourceString("property.y.label"));
		desc.setCategory(CATEGORY_GEOM);		
		descriptors.add(desc);
		
		// Width		
		desc = new IntPropertyDescriptor(DrawComponent.PROP_WIDTH,
				EditorPlugin.getResourceString("property.width.label"));
		desc.setCategory(CATEGORY_GEOM);		
		descriptors.add(desc);
		
		// Height		
		desc = new IntPropertyDescriptor(DrawComponent.PROP_HEIGHT,
				EditorPlugin.getResourceString("property.height.label"));
		desc.setCategory(CATEGORY_GEOM);		
		descriptors.add(desc);
		
		// Rotation		
		desc = new RotationPropertyDescriptor(DrawComponent.PROP_ROTATION,
				EditorPlugin.getResourceString("property.rotation.label"));
		desc.setCategory(CATEGORY_ROTATION);		
		descriptors.add(desc);
		
		// RotationX
		desc = new IntPropertyDescriptor(DrawComponent.PROP_ROTATION_X,
				EditorPlugin.getResourceString("property.rotationx.label"));
		desc.setCategory(CATEGORY_ROTATION);		
		descriptors.add(desc);
		
		// RotationY
		desc = new IntPropertyDescriptor(DrawComponent.PROP_ROTATION_Y,
				EditorPlugin.getResourceString("property.rotationy.label"));
		desc.setCategory(CATEGORY_ROTATION);		
		descriptors.add(desc);
		
		return descriptors;
	}	
	
	public Object getPropertyValue(Object id) 
	{
		if (id.equals(DrawComponent.PROP_X)) {
			return new Integer(drawComponent.getX());
		}
		else if (id.equals(DrawComponent.PROP_Y)) {
			return new Integer(drawComponent.getY());
		}
		else if (id.equals(DrawComponent.PROP_WIDTH)) {
			return new Integer(drawComponent.getWidth());
		}
		else if (id.equals(DrawComponent.PROP_HEIGHT)) {
			return new Integer(drawComponent.getHeight());
		}
		else if (id.equals(DrawComponent.PROP_ROTATION)) {
			return new Double(drawComponent.getRotation());
		}		
		else if (id.equals(DrawComponent.PROP_ROTATION_X)) {
			return new Integer(drawComponent.getRotationX());
		}		
		else if (id.equals(DrawComponent.PROP_ROTATION_Y)) {
			return new Integer(drawComponent.getRotationY());
		}		
//		else if (id.equals(DrawComponent.PROP_NAME)) {
//			return drawComponent.getI18nText().getText(nameLangMan.getCurrentLanguageID());
//		}		
						
		return null;
	}	
	
	public void setPropertyValue(Object id, Object value) 
	{
		if (id.equals(DrawComponent.PROP_X)) {
			drawComponent.setX(((Integer)value).intValue());
		}
		else if (id.equals(DrawComponent.PROP_Y)) {
			drawComponent.setY(((Integer)value).intValue());
		}
		else if (id.equals(DrawComponent.PROP_WIDTH)) {
			drawComponent.setWidth(((Integer)value).intValue());
		}
		else if (id.equals(DrawComponent.PROP_HEIGHT)) {
			drawComponent.setHeight(((Integer)value).intValue());
		}
		else if (id.equals(DrawComponent.PROP_ROTATION)) {
			drawComponent.setRotation(((Double)value).doubleValue());
		}		
		else if (id.equals(DrawComponent.PROP_ROTATION_X)) {
			drawComponent.setRotationX(((Integer)value).intValue());
		}		
		else if (id.equals(DrawComponent.PROP_ROTATION_Y)) {
			drawComponent.setRotationY(((Integer)value).intValue());
		}
//		else if (id.equals(DrawComponent.PROP_NAME)) {
//			drawComponent.setName((String)value);
//		}
	}	
}
