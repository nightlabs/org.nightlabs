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

package org.nightlabs.editor2d.iofilter.xstream;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.impl.ImageDrawComponentImpl;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ImageDrawComponentConverter
extends DrawComponentConverter
{
	public static final String IMAGE_SHAPE = ImageDrawComponent.PROP_IMAGE_SHAPE;
	public static final String ORIGINAL_FILE_NAME = ImageDrawComponent.PROP_ORIGINAL_FILE_NAME;
	public static final String LAST_MODIFIED = ImageDrawComponent.PROP_LAST_MODIFIED;
	public static final String IMAGE_KEY = ImageDrawComponent.PROP_IMAGE_KEY;
	
	public ImageDrawComponentConverter() {
		super();
	}
	
	@Override
	protected DrawComponent readSpecific(DrawComponent dc, HierarchicalStreamReader reader,
			UnmarshallingContext context)
	{
		ImageDrawComponent image = (ImageDrawComponent) dc;
//		image.setImageShape(factory.createGeneralShapeFromString(
//				reader.getAttribute(IMAGE_SHAPE)));
//		image.setOriginalImageFileName(
//				reader.getAttribute(ORIGINAL_FILE_NAME));
//		image.setOriginalImageFileLastModified(Long.parseLong(
//				reader.getAttribute(LAST_MODIFIED)));
		
		return image;
	}

	@Override
	protected void writeSpecific(DrawComponent dc,
			HierarchicalStreamWriter writer, MarshallingContext context)
	{
//		ImageDrawComponent image = (ImageDrawComponent) dc;
//		writer.addAttribute(IMAGE_SHAPE,
//				factory.convertGeneralShapeToString(image.getImageShape()));
//		writer.addAttribute(ORIGINAL_FILE_NAME,
//				image.getOriginalImageFileName());
//		writer.addAttribute(LAST_MODIFIED,
//				""+image.getOriginalImageFileLastModified());
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class type)
	{
		if (type.equals(ImageDrawComponentImpl.class)) {
			return true;
		}
		return false;
	}

	@Override
	protected String getNodeName() {
		return "ImageDrawComponent";
	}
	
	@Override
	public Class<? extends DrawComponent> getImpl() {
		return ImageDrawComponentImpl.class;
	}
	
}
