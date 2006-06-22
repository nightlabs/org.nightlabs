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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.nightlabs.base.language.LanguageManager;
import org.nightlabs.base.property.DoublePropertyDescriptor;
import org.nightlabs.base.property.IntPropertyDescriptor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.page.PageRegistryEP;
import org.nightlabs.editor2d.page.resolution.ResolutionImpl;
import org.nightlabs.editor2d.page.unit.DotUnit;
import org.nightlabs.editor2d.preferences.Preferences;
import org.nightlabs.editor2d.properties.NamePropertyDescriptor;
import org.nightlabs.editor2d.properties.RotationPropertyDescriptor;
import org.nightlabs.editor2d.properties.UnitManager;
import org.nightlabs.i18n.IUnit;
import org.nightlabs.language.LanguageCf;

public class DrawComponentPropertySource 
implements IPropertySource
{	
	public static final String CATEGORY_NAME = EditorPlugin.getResourceString("property.category.name");
	public static final String CATEGORY_GEOM = EditorPlugin.getResourceString("property.category.geom");		
	public static final String CATEGORY_ROTATION = EditorPlugin.getResourceString("property.category.rotation");
	
	public static final Logger LOGGER = Logger.getLogger(DrawComponentPropertySource.class);
	
	public DrawComponentPropertySource(DrawComponent element) 
	{
		this.drawComponent = element;
		
		String unitID = Preferences.getPreferenceStore().getString(
				Preferences.PREF_STANDARD_UNIT_ID);
		unit = PageRegistryEP.sharedInstance().getPageRegistry().getUnit(unitID);
		
		descriptors = createPropertyDescriptors();
		nameLangMan = LanguageManager.sharedInstance();	
		nameLangMan.addPropertyChangeListener(langListener);
	}
	
	protected DrawComponent drawComponent;	
	protected LanguageManager nameLangMan;	
	
	protected PropertyChangeListener langListener = new PropertyChangeListener()
	{	
		public void propertyChange(PropertyChangeEvent evt) 
		{
			if (evt.getPropertyName().equals(LanguageManager.LANGUAGE_CHANGED)) 
			{
				LanguageCf langCf = (LanguageCf) evt.getNewValue();
				drawComponent.setLanguageId(langCf.getLanguageID());
			}
		}	
	};
	
	private IUnit unit = new DotUnit(new ResolutionImpl());
	public IUnit getUnit() {
		return unit;
	}
	public void setUnit(IUnit unit) {
		this.unit = unit;
	}
	
//	public PropertyChangeListener unitListener = new PropertyChangeListener()
//	{
//		public void propertyChange(PropertyChangeEvent evt) 
//		{
//			if (evt.getPropertyName().equals(UnitManager.PROP_CURRENT_UNIT_CHANGED)) 
//			{
//				unit = (IUnit) evt.getNewValue();
//			}
//		}			
//	};
	
	public double getValue(int modelValue, IUnit unit) 
	{
		return modelValue;
	}
		
	public int getSetValue(double value, IUnit unit) 
	{
		return (int) Math.rint(value);
	}	
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue() {
		return drawComponent;
	}
	
	protected List descriptors = null;
	protected List getDescriptors() {
		if (descriptors == null)
			descriptors = new ArrayList();
		return descriptors;
	}
		
	protected List createPropertyDescriptors() 
	{
		List descriptors = getDescriptors();
		
		// Name
		descriptors.add(createNamePD());				
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
		// RotationX
		descriptors.add(createRotationXPD());		
		// RotationY
		descriptors.add(createRotationYPD());
		
		return descriptors;
	}
	
	protected PropertyDescriptor createNamePD() 
	{
		PropertyDescriptor desc = new NamePropertyDescriptor(drawComponent,
				DrawComponent.PROP_NAME,
				EditorPlugin.getResourceString("property.name.label"));		
		desc.setCategory(CATEGORY_NAME);
		return desc;
	}
	
	protected PropertyDescriptor createXPD() 
	{
		PropertyDescriptor desc = new DoublePropertyDescriptor(DrawComponent.PROP_X,
				EditorPlugin.getResourceString("property.x.label"));
		desc.setCategory(CATEGORY_GEOM);
		return desc;
	}
	
	protected PropertyDescriptor createYPD() 
	{
		PropertyDescriptor desc = new DoublePropertyDescriptor(DrawComponent.PROP_Y,
				EditorPlugin.getResourceString("property.y.label"));
		desc.setCategory(CATEGORY_GEOM);
		return desc;
	}

	protected PropertyDescriptor createWidthPD() 
	{
		PropertyDescriptor desc = new DoublePropertyDescriptor(DrawComponent.PROP_WIDTH,
				EditorPlugin.getResourceString("property.width.label"));
		desc.setCategory(CATEGORY_GEOM);
		return desc;
	}

	protected PropertyDescriptor createHeightPD() 
	{
		PropertyDescriptor desc = new DoublePropertyDescriptor(DrawComponent.PROP_HEIGHT,
				EditorPlugin.getResourceString("property.height.label"));
		desc.setCategory(CATEGORY_GEOM);
		return desc;
	}
	
	protected PropertyDescriptor createRotationPD() 
	{
		PropertyDescriptor desc = new RotationPropertyDescriptor(DrawComponent.PROP_ROTATION,
				EditorPlugin.getResourceString("property.rotation.label"));
		desc.setCategory(CATEGORY_ROTATION);		
		return desc;
	}
	
	protected PropertyDescriptor createRotationXPD() 
	{
		PropertyDescriptor desc = new IntPropertyDescriptor(DrawComponent.PROP_ROTATION_X,
				EditorPlugin.getResourceString("property.rotationx.label"));
		desc.setCategory(CATEGORY_ROTATION);		
		return desc;
	}

	protected PropertyDescriptor createRotationYPD() 
	{
		PropertyDescriptor desc = new IntPropertyDescriptor(DrawComponent.PROP_ROTATION_Y,
				EditorPlugin.getResourceString("property.rotationy.label"));
		desc.setCategory(CATEGORY_ROTATION);		
		return desc;
	}
	
	public IPropertyDescriptor[] getPropertyDescriptors() 
	{
		if (drawComponent == null)
			return new IPropertyDescriptor[0];
		
		List descriptors = getDescriptors();		
		return (IPropertyDescriptor[])descriptors.toArray( new IPropertyDescriptor[] {} );		
	}
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) 
	{
		if (id.equals(DrawComponent.PROP_X)) {
			return new Double(getValue(drawComponent.getX(), getUnit()));
		}
		else if (id.equals(DrawComponent.PROP_Y)) {
			return new Double(getValue(drawComponent.getY(), getUnit()));
		}
		else if (id.equals(DrawComponent.PROP_WIDTH)) {
			return new Double(getValue(drawComponent.getWidth(), getUnit()));
		}
		else if (id.equals(DrawComponent.PROP_HEIGHT)) {
			return new Double(getValue(drawComponent.getHeight(), getUnit()));
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
	
	/* 
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id) {
		return false;
	}

	/*
	 * @see org.eclipse.ui.views.properties.IPropertySouce#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id) {
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) 
	{
		if (id.equals(DrawComponent.PROP_X)) {
			double x = ((Double)value).doubleValue();
			drawComponent.setX(getSetValue(x, getUnit()));
			return;
		}
		else if (id.equals(DrawComponent.PROP_Y)) {
			double y = ((Double)value).doubleValue();
			drawComponent.setY(getSetValue(y, getUnit()));
			return;
		}
		else if (id.equals(DrawComponent.PROP_WIDTH)) {
			double width = ((Double)value).doubleValue();
			drawComponent.setWidth(getSetValue(width, getUnit()));
			return;
		}
		else if (id.equals(DrawComponent.PROP_HEIGHT)) {
			double height = ((Double)value).doubleValue();
			drawComponent.setHeight(getSetValue(height, getUnit()));
			return;
		}
		else if (id.equals(DrawComponent.PROP_ROTATION)) {
			drawComponent.setRotation(((Double)value).doubleValue());
			return;
		}		
		else if (id.equals(DrawComponent.PROP_ROTATION_X)) {
			drawComponent.setRotationX(((Integer)value).intValue());
			return;
		}		
		else if (id.equals(DrawComponent.PROP_ROTATION_Y)) {
			drawComponent.setRotationY(((Integer)value).intValue());
			return;
		}
		else if (id.equals(DrawComponent.PROP_NAME)) {
			drawComponent.setName((String)value);
			return;
		}
	}
			
}

