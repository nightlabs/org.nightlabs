/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 24.01.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.model;

import java.awt.Color;
import java.util.List;

import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.nightlabs.editor2d.EditorPlugin;
import com.nightlabs.editor2d.ShapeDrawComponent;
import com.nightlabs.editor2d.properties.AWTColorPropertyDescriptor;
import com.nightlabs.editor2d.properties.CheckboxPropertyDescriptor;
import com.nightlabs.editor2d.properties.IntPropertyDescriptor;


public class ShapeDrawComponentPropertySource 
extends DrawComponentPropertySource 
{
	public static final String CATEGORY_COLORS = EditorPlugin.getResourceString("property.category.color");
	public static final String CATEGORY_LINE = EditorPlugin.getResourceString("property.category.line");	
	
  /**
   * @param element
   */
  public ShapeDrawComponentPropertySource(ShapeDrawComponent element) 
  {
    super(element);
  }
    
	protected List createPropertyDescriptors() 
	{
		List descriptors = super.createPropertyDescriptors();
		
		// Line Color		
		PropertyDescriptor desc = new AWTColorPropertyDescriptor(ShapeDrawComponent.PROP_LINE_COLOR,
				EditorPlugin.getResourceString("property.linecolor.label"));
		desc.setCategory(CATEGORY_COLORS);
		descriptors.add(desc);
		
		// Fill Color
		desc = new AWTColorPropertyDescriptor(ShapeDrawComponent.PROP_FILL_COLOR,
				EditorPlugin.getResourceString("property.fillcolor.label"));
		desc.setCategory(CATEGORY_COLORS);
		descriptors.add(desc);
		
		// Line Width
		desc = new IntPropertyDescriptor(ShapeDrawComponent.PROP_LINE_WIDTH,
				EditorPlugin.getResourceString("property.linewidth.label"));
		desc.setCategory(CATEGORY_LINE);
		descriptors.add(desc);
		
		// Line Style
		desc = new IntPropertyDescriptor(ShapeDrawComponent.PROP_LINE_STYLE,
				EditorPlugin.getResourceString("property.linestyle.label"));
		desc.setCategory(CATEGORY_LINE);
		descriptors.add(desc);
		
		// Fill
		desc = new CheckboxPropertyDescriptor(ShapeDrawComponent.PROP_FILL,
				EditorPlugin.getResourceString("property.fill.label"));				
		descriptors.add(desc);
		
		return descriptors;
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
			if (id.equals(ShapeDrawComponent.PROP_FILL_COLOR)) {
				return getShapeDrawComponent().getFillColor();
			}
			else if (id.equals(ShapeDrawComponent.PROP_LINE_COLOR)) {
				return getShapeDrawComponent().getLineColor();
			}
			else if (id.equals(ShapeDrawComponent.PROP_LINE_WIDTH)) {
				return new Integer(getShapeDrawComponent().getLineWidth());
			}
			else if (id.equals(ShapeDrawComponent.PROP_LINE_STYLE)) {
				return new Integer(getShapeDrawComponent().getLineStyle());
			}
			else if (id.equals(ShapeDrawComponent.PROP_FILL)) {
				return new Boolean(getShapeDrawComponent().isFill());
			}
			
			return null;			
		}
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) 
	{
		super.setPropertyValue(id, value);
		
		if (id.equals(ShapeDrawComponent.PROP_FILL_COLOR)) {
			getShapeDrawComponent().setFillColor((Color)value);
		}
		else if (id.equals(ShapeDrawComponent.PROP_LINE_COLOR)) {
			getShapeDrawComponent().setLineColor((Color)value);
		}
		else if (id.equals(ShapeDrawComponent.PROP_LINE_WIDTH)) {
			getShapeDrawComponent().setLineWidth(((Integer)value).intValue());
		}
		else if (id.equals(ShapeDrawComponent.PROP_LINE_STYLE)) {
			getShapeDrawComponent().setLineStyle(((Integer)value).intValue());
		}
		else if (id.equals(ShapeDrawComponent.PROP_FILL)) {
			getShapeDrawComponent().setFill(((Boolean)value).booleanValue());
		}		
	}	
	
	protected ShapeDrawComponent getShapeDrawComponent() {
		return (ShapeDrawComponent) drawComponent;
	}
}
