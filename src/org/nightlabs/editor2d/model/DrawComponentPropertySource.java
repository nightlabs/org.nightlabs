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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import org.nightlabs.base.language.LanguageChangeEvent;
import org.nightlabs.base.language.LanguageChangeListener;
import org.nightlabs.base.property.IntPropertyDescriptor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.properties.NameLanguageManager;
import org.nightlabs.editor2d.properties.NamePropertyDescriptor;
import org.nightlabs.editor2d.properties.RotationPropertyDescriptor;

public class DrawComponentPropertySource 
implements IPropertySource
{	
	protected DrawComponent drawComponent;
	protected NameLanguageManager nameLangMan;	
	public DrawComponentPropertySource(DrawComponent element) {
		this.drawComponent = element;
		descriptors = createPropertyDescriptors();
		nameLangMan = NameLanguageManager.sharedInstance();	
		nameLangMan.addLanguageChangeListener(langListener);
	}
 
	protected LanguageChangeListener langListener = new LanguageChangeListener(){	
		public void languageChanged(LanguageChangeEvent event) {	
			String newLanguageID = event.getNewLanguage().getLanguageID();
			drawComponent.setLanguageId(newLanguageID);
		}	
	};
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue() {
		return drawComponent;
	}

	public static final String CATEGORY_NAME = EditorPlugin.getResourceString("property.category.name");
	public static final String CATEGORY_GEOM = EditorPlugin.getResourceString("property.category.geom");		
	public static final String CATEGORY_ROTATION = EditorPlugin.getResourceString("property.category.rotation");
	
	protected List descriptors = null;
	protected List getDescriptors() {
		if (descriptors == null)
			descriptors = new ArrayList();
		return descriptors;
	}
		
	protected List createPropertyDescriptors() 
	{
		List descriptors = getDescriptors();
		
//		// Name
//		PropertyDescriptor desc = new TextPropertyDescriptor(DrawComponent.PROP_NAME,
//				EditorPlugin.getResourceString("property.name.label"));
//		desc.setCategory(CATEGORY_NAME);
//		descriptors.add(desc);
//		// Name
//		PropertyDescriptor desc = new LanguagePropertyDescriptor(
//				drawComponent.getI18nText(),
//				DrawComponent.PROP_NAME,
//				EditorPlugin.getResourceString("property.name.label"));
		// Name
		PropertyDescriptor desc = new NamePropertyDescriptor(drawComponent,
				DrawComponent.PROP_NAME,
				EditorPlugin.getResourceString("property.name.label"));		
		desc.setCategory(CATEGORY_NAME);
		descriptors.add(desc);		
		
		// X
		desc = new IntPropertyDescriptor(DrawComponent.PROP_X,
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
	
	public IPropertyDescriptor[] getPropertyDescriptors() 
	{
		if (drawComponent == null)
			return new IPropertyDescriptor[0];
		
		List descriptors = getDescriptors();		
		return (IPropertyDescriptor[])descriptors.toArray( new IPropertyDescriptor[] {} );		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
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
		else if (id.equals(DrawComponent.PROP_NAME)) {
			return drawComponent.getI18nText().getText(nameLangMan.getCurrentLanguageID());
		}		
						
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * @see org.eclipse.ui.views.properties.IPropertySouce#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
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
		else if (id.equals(DrawComponent.PROP_NAME)) {
			drawComponent.setName((String)value);
		}
	}
			
}

