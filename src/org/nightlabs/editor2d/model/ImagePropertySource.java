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
import org.nightlabs.base.property.DoublePropertyDescriptor;
import org.nightlabs.base.property.IntPropertyDescriptor;
import org.nightlabs.base.property.XTextPropertyDescriptor;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.image.RenderModeMetaData;
import org.nightlabs.editor2d.page.resolution.DPIResolutionUnit;
import org.nightlabs.editor2d.page.resolution.IResolutionUnit;
import org.nightlabs.editor2d.properties.ImageColorConversionPropertyDescriptor;

public class ImagePropertySource 
extends DrawComponentPropertySource
{
	public static final String CATEGORY_IMAGE = EditorPlugin.getResourceString("property.category.image");
	
	public ImagePropertySource(ImageDrawComponent element) {
		super(element);
	}

	protected ImageDrawComponent getImageDrawComponent() {
		return (ImageDrawComponent) drawComponent;
	}
	
//	protected List createPropertyDescriptors() 
//	{
//		List descriptors = getDescriptors();
//		// Name
//		descriptors.add(createNamePD());
//		// File Name
//		descriptors.add(createFileNamePD());
//		// X
//		descriptors.add(createXPD());		
//		// Y		
//		descriptors.add(createYPD());		
//		// Width		
//		descriptors.add(createWidthPD());		
//		// Height		
//		descriptors.add(createHeightPD());		
//		// Rotation		
//		descriptors.add(createRotationPD());	
//		// Bit Per Pixel
//		descriptors.add(createBitsPerPixelPD());
////		// Resolution Width
////		descriptors.add(createResolutionWidthPD());
////		// Resolution Height
////		descriptors.add(createResolutionHeightPD());
//		// Color Conversion
//		descriptors.add(createColorConversionPD());
//		
//		return descriptors;
//	}	

	protected List createPropertyDescriptors() 
	{
		super.createPropertyDescriptors();
		
		List descriptors = getDescriptors();
		// Bit Per Pixel
		descriptors.add(createBitsPerPixelPD());
//		// Resolution Width
//		descriptors.add(createResolutionWidthPD());
//		// Resolution Height
//		descriptors.add(createResolutionHeightPD());
		// Color Conversion
		descriptors.add(createColorConversionPD());
		
		return descriptors;
	}	
		
	public Object getPropertyValue(Object id) 
	{
		if (id.equals(ImageDrawComponent.PROP_ORIGINAL_FILE_NAME)) {
			return getImageDrawComponent().getOriginalImageFileName();
		}
		else if (id.equals(ImageDrawComponent.PROP_BITS_PER_PIXEL)) {
			return getImageDrawComponent().getBitsPerPixel();
		}
		else if (id.equals(ImageDrawComponent.PROP_RESOLUTION_WIDTH)) {
			return getResolutionInDPI(getImageDrawComponent(), true);
		}
		else if (id.equals(ImageDrawComponent.PROP_RESOLUTION_HEIGHT)) {
			return getResolutionInDPI(getImageDrawComponent(), false);
		} 
		return super.getPropertyValue(id);
	}	
			
	@Override
	public void setPropertyValue(Object id, Object value) 
	{
		if (id.equals(ImageDrawComponent.PROP_RENDER_MODE_META_DATA)) 
		{
			List<RenderModeMetaData> renderModeMetaData = (List<RenderModeMetaData>) value;
			for (RenderModeMetaData data : renderModeMetaData) {
				getImageDrawComponent().addRenderModeMetaData(data);
			}
			getImageDrawComponent().setRenderMode(getImageDrawComponent().getRenderMode());
			return;
		}
		super.setPropertyValue(id, value);
	}

	private IResolutionUnit dpiUnit = new DPIResolutionUnit();
	private Double getResolutionInDPI(ImageDrawComponent img, boolean width) 
	{
		IResolutionUnit unit = null;
		if (width)
			unit = img.getResolutionWidth().getResolutionUnit();
		else
			unit = img.getResolutionHeight().getResolutionUnit();
		
		double resolution = -1;
		if (width)
			resolution = img.getResolutionWidth().getResolutionX(); 
		else
			resolution = img.getResolutionHeight().getResolutionY();
		
		if (unit.getResolutionID().equals(dpiUnit.getResolutionID())) {
			return new Double(resolution);
		}
		else 
		{ 			
			double oldfactor = dpiUnit.getUnit().getFactor();
			double dpiResolution = (resolution * unit.getUnit().getFactor()) / oldfactor;
			return new Double(dpiResolution);
		}
	}
	
	protected PropertyDescriptor createFileNamePD() 
	{
		PropertyDescriptor pd = new XTextPropertyDescriptor(
				ImageDrawComponent.PROP_ORIGINAL_FILE_NAME, 
				EditorPlugin.getResourceString("property.originalImageFileName.label"), 
				true);
		pd.setCategory(CATEGORY_NAME);
		return pd;
	}
	
	protected PropertyDescriptor createBitsPerPixelPD() 
	{
		PropertyDescriptor pd = new IntPropertyDescriptor(ImageDrawComponent.PROP_BITS_PER_PIXEL, 
				EditorPlugin.getResourceString("property.bitsPerPixel.label"), true);
		pd.setCategory(CATEGORY_IMAGE);
		return pd;
	}
	
	protected PropertyDescriptor createResolutionWidthPD() 
	{
		PropertyDescriptor pd = new DoublePropertyDescriptor(ImageDrawComponent.PROP_RESOLUTION_WIDTH, 
				EditorPlugin.getResourceString("property.resolutionWidth.label"), true);
		pd.setCategory(CATEGORY_IMAGE);
		return pd;		
	}
	
	protected PropertyDescriptor createResolutionHeightPD() 
	{
		PropertyDescriptor pd = new DoublePropertyDescriptor(ImageDrawComponent.PROP_RESOLUTION_HEIGHT, 
				EditorPlugin.getResourceString("property.resolutionHeight.label"), true);
		pd.setCategory(CATEGORY_IMAGE);
		return pd;		
	}
	
	protected PropertyDescriptor createColorConversionPD()
	{
		PropertyDescriptor pd = new ImageColorConversionPropertyDescriptor(getImageDrawComponent(), 
				ImageDrawComponent.PROP_RENDER_MODE_META_DATA, 
				EditorPlugin.getResourceString("property.colorConversion.label"));
		return pd;
	}
}
