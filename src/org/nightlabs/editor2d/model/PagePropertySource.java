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

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.nightlabs.base.celleditor.CheckboxCellEditor;
import org.nightlabs.base.language.LanguageManager;
import org.nightlabs.base.property.CheckboxPropertyDescriptor;
import org.nightlabs.base.property.ComboBoxPropertyDescriptor;
import org.nightlabs.base.property.DoublePropertyDescriptor;
import org.nightlabs.base.property.XTextPropertyDescriptor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.MultiLayerDrawComponent;
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
		// Orientation
		descriptors.add(createOrientationPD());
		// Width
		descriptors.add(createPageWidthPD());
		// Height
		descriptors.add(createPageHeightPD());
		// Resolution Unit
		descriptors.add(createResolutionUnitPD());		
		// Resolution Value
		descriptors.add(createResolutionValuePD());
		// Page Size
//		descriptors.add(createPageSizePD());
		// Show Page Bounds
		descriptors.add(createShowPageBoundsPD());
		// Visible
		descriptors.add(createVisiblePD());		
		
		// PropertyDescriptors from extension point
		List<IPropertyDescriptor> extensionPointProperties = getExtensionPointProperties(); 
		if (!extensionPointProperties.isEmpty())
			descriptors.addAll(extensionPointProperties);
		
		return descriptors;
	}
	
	protected PropertyDescriptor createPageWidthPD() 
	{
		PropertyDescriptor desc = new DoublePropertyDescriptor(PageDrawComponent.PROP_WIDTH,
				EditorPlugin.getResourceString("property.width.label"), true);
		desc.setCategory(CATEGORY_GEOM);
		return desc;
	}
	
	protected PropertyDescriptor createPageHeightPD() 
	{
		PropertyDescriptor desc = new DoublePropertyDescriptor(DrawComponent.PROP_HEIGHT,
				EditorPlugin.getResourceString("property.height.label"), true);
		desc.setCategory(CATEGORY_GEOM);
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

	protected PropertyDescriptor createShowPageBoundsPD()
	{
		PropertyDescriptor desc = new CheckboxPropertyDescriptor(PageDrawComponent.PROP_SHOW_PAGE_BOUNDS,
				EditorPlugin.getResourceString("property.showPageBounds.label"));
		desc.setCategory(CATEGORY_PAGE);
		return desc;
	}	
		
	protected PropertyDescriptor createResolutionValuePD() 
	{
		PropertyDescriptor desc = new DoublePropertyDescriptor(MultiLayerDrawComponent.PROP_RESOLUTION,
				EditorPlugin.getResourceString("property.resolution.label"), true);
		desc.setCategory(CATEGORY_RESOLUTION);
		return desc;
	}

	public static final String ID_RESOLUTION_UNIT = "ResolutionUnit";
	public static final String CATEGORY_RESOLUTION = "Resolution";	
	public static final String CATEGORY_PAGE = "Page";	
	
	protected PropertyDescriptor createResolutionUnitPD() 
	{
		PropertyDescriptor desc = new XTextPropertyDescriptor(ID_RESOLUTION_UNIT,
				EditorPlugin.getResourceString("property.resolutionUnit.label"), true);
		desc.setCategory(CATEGORY_RESOLUTION);
		return desc;
	}
		
//	protected PropertyDescriptor createPageSizePD()
//	{
//		PropertyDescriptor pd = new PageSelectPropertyDescriptor(PageDrawComponent.PROP_PAGE_BOUNDS,
//				EditorPlugin.getResourceString("property.pageBounds.label"));
//		return pd;
//	}
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) 
	{
		if (id.equals(PageDrawComponent.PROP_ORIENTATION)) {
			return getOrientationString(getPageDrawComponent().getOrientation());
		}
		else if (id.equals(DrawComponent.PROP_NAME)) {
			return drawComponent.getI18nText().getText(LanguageManager.sharedInstance().getCurrentLanguageID());
		}
		else if (id.equals(DrawComponent.PROP_WIDTH)) {
			return new Double(getValue(getPageDrawComponent().getPageBounds().width, getUnit()));
		}
		else if (id.equals(DrawComponent.PROP_HEIGHT)) {
			return new Double(getValue(getPageDrawComponent().getPageBounds().height, getUnit()));
		}		
		else if (id.equals(MultiLayerDrawComponent.PROP_RESOLUTION)) {
			return getPageDrawComponent().getRoot().getResolution().getResolutionX();
		}
		else if (id.equals(ID_RESOLUTION_UNIT)) {
			return getPageDrawComponent().getRoot().getResolution().getResolutionUnit().getResolutionID();
		}
		else if (id.equals(PageDrawComponent.PROP_SHOW_PAGE_BOUNDS)) {
			return getPageDrawComponent().isShowPageBounds();
		}						
//		else if (id.equals(PageDrawComponent.PROP_PAGE_BOUNDS)) {
//			PageSize pageSize = new PageSize();
//			pageSize.setPageHeight(getPageDrawComponent().getPageBounds().width);
//			pageSize.setPageWidth(getPageDrawComponent().getPageBounds().height);
//			pageSize.setResolution(getPageDrawComponent().getResolution());
//			return pageSize;
//		}
		
//		return null;
		return super.getPropertyValue(id);
	}
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */	
	public void setPropertyValue(Object id, Object value) 
	{
		if (id.equals(PageDrawComponent.PROP_ORIENTATION)) {
			int newOrientation = getOrientation((String)value);
			getPageDrawComponent().setOrientation(newOrientation);
		}
		else if (id.equals(DrawComponent.PROP_NAME)) {
			drawComponent.setName((String)value);
		}
		else if (id.equals(PageDrawComponent.PROP_SHOW_PAGE_BOUNDS)) {
			getPageDrawComponent().setShowPageBounds((Boolean)value);
		}		
//		else if (id.equals(MultiLayerDrawComponent.PROP_RESOLUTION)) 
//		{
//			double resolution = ((Double)value).doubleValue();
//			getPageDrawComponent().getRoot().getResolution().setResolutionX(resolution);
//			getPageDrawComponent().getRoot().getResolution().setResolutionY(resolution);
//		}		
//		else if (id.equals(PageDrawComponent.PROP_PAGE_BOUNDS)) 
//		{
//			PageSize pageSize = (PageSize) value;
//			int defaultX = 25;
//			int defaultY = 25;
//			Rectangle pageBounds = new Rectangle(defaultX, defaultY, 
//					(int)pageSize.getPageWidth(), (int)pageSize.getPageHeight());
//			getPageDrawComponent().setPageBounds(pageBounds);
//			getPageDrawComponent().setResolution(pageSize.getResolution());
//		}	
		super.setPropertyValue(id, value);
	}
	
}
