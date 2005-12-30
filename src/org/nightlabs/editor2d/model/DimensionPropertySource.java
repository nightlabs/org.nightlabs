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

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import org.nightlabs.editor2d.EditorPlugin;

public class DimensionPropertySource implements IPropertySource {

	public static String ID_WIDTH = "width";  //$NON-NLS-1$
	public static String ID_HEIGHT = "height";//$NON-NLS-1$
	protected static IPropertyDescriptor[] descriptors;

	static {
		PropertyDescriptor widthProp =
			new TextPropertyDescriptor(ID_WIDTH,
				EditorPlugin.getResourceString("property.width.label"));
		widthProp.setValidator(NumberCellEditorValidator.getSharedInstance());
		PropertyDescriptor heightProp = 
			new TextPropertyDescriptor(ID_HEIGHT,
					EditorPlugin.getResourceString("property.height.label"));
		heightProp.setValidator(NumberCellEditorValidator.getSharedInstance());
		descriptors = new IPropertyDescriptor[] {widthProp,heightProp};
	}

	protected Dimension dimension = null;

	public DimensionPropertySource(Dimension dimension){
		this.dimension = dimension.getCopy();
	}

	public Object getEditableValue(){
		return dimension.getCopy();
	}

	public Object getPropertyValue(Object propName){
		return getPropertyValue((String)propName);
	}

	public Object getPropertyValue(String propName){
		if(ID_HEIGHT.equals(propName)){
			return new String(new Integer(dimension.height).toString());
		}
		if(ID_WIDTH.equals(propName)){
			return new String(new Integer(dimension.width).toString());
		}
		return null;
	}

	public void setPropertyValue(Object propName, Object value){
		setPropertyValue((String)propName, value);
	}

	public void setPropertyValue(String propName, Object value){
		if(ID_HEIGHT.equals(propName)){
			Integer newInt = new Integer((String)value);
			dimension.height = newInt.intValue();
		}
		if(ID_WIDTH.equals(propName)){
			Integer newInt = new Integer((String)value);
			dimension.width = newInt.intValue();
		}
	}

	public IPropertyDescriptor[] getPropertyDescriptors(){
		return descriptors;
	}

	public void resetPropertyValue(String propName){
	}

	public void resetPropertyValue(Object propName){
	}

	public boolean isPropertySet(Object propName){
		return true;
	}

	public boolean isPropertySet(String propName){
		if(ID_HEIGHT.equals(propName) || ID_WIDTH.equals(propName))return true;
		return false;
	}

	public String toString(){
		return new String("("+dimension.width+","+dimension.height+")");//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
	}

}
