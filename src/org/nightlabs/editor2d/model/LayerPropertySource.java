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
import org.nightlabs.base.property.CheckboxPropertyDescriptor;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.Layer;

public class LayerPropertySource 
extends DrawComponentPropertySource 
{
	public LayerPropertySource(Layer layer) {
		super(layer);
	}

	protected Layer getLayer() {
		return (Layer) drawComponent;
	}
	
	protected List createPropertyDescriptors() 
	{
		List descriptors = getDescriptors();
		// Name
		descriptors.add(createNamePD());
		// Visible
		descriptors.add(createVisiblePD());
		
		return descriptors;
	}			 
	
	protected PropertyDescriptor createVisiblePD() 
	{
		PropertyDescriptor pd = new CheckboxPropertyDescriptor(Layer.PROP_VISIBLE, 
				EditorPlugin.getResourceString("property.visible.label"), false);
		return pd;
	}

	@Override
	public Object getPropertyValue(Object id) 
	{
		if (id.equals(Layer.PROP_VISIBLE)) {
			return new Boolean(getLayer().isVisible());
		}		
		return super.getPropertyValue(id);
	}

	@Override
	public void setPropertyValue(Object id, Object value) 
	{
		if (id.equals(Layer.PROP_VISIBLE)) {
			getLayer().setVisible(((Boolean)value).booleanValue());
		}
		super.setPropertyValue(id, value);
	}
		
}
