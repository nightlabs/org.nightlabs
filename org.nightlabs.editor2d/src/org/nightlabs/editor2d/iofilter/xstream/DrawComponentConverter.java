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
import org.nightlabs.editor2d.impl.Editor2DFactoryImpl;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public abstract class DrawComponentConverter
implements Converter
{
	public static final String ID = DrawComponent.PROP_ID;
	public static final String AFFINE_TRANSFORM = DrawComponent.PROP_AFFINE_TRANSFORM;
	public static final String HORIZONTAL_GUIDE = DrawComponent.PROP_HORIZONTAL_GUIDE;
	public static final String VERTICAL_GUIDE = DrawComponent.PROP_VERTICAL_GUIDE;
	public static final String X = DrawComponent.PROP_X;
	public static final String Y = DrawComponent.PROP_Y;
	public static final String WIDTH = DrawComponent.PROP_WIDTH;
	public static final String HEIGHT = DrawComponent.PROP_HEIGHT;
	public static final String RENDER_MODE = DrawComponent.PROP_RENDER_MODE;
	public static final String ROTATION = DrawComponent.PROP_ROTATION;
	public static final String ROTATION_X = DrawComponent.PROP_ROTATION_X;
	public static final String ROTATION_Y = DrawComponent.PROP_ROTATION_Y;
	
	public static final String TMP_ROTATION_X = DrawComponent.PROP_TMP_ROTATION_X;
	public static final String TMP_ROTATION_Y = DrawComponent.PROP_TMP_ROTATION_Y;
	
	public static final String PARENT_ID = "ParentID";
	public static final String NAME = DrawComponent.PROP_NAME;
		
	public static final Editor2DFactoryImpl factory = new Editor2DFactoryImpl();
	
	public DrawComponentConverter() {
		super();
	}

	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type)
	{
		if (DrawComponent.class.isAssignableFrom(type)) {
			return true;
		}
		return false;
	}

	protected void write(Object source, HierarchicalStreamWriter writer, MarshallingContext context)
	{
  	DrawComponent dc = (DrawComponent) source;
    writer.startNode(getNodeName());
    writer.addAttribute(ID, ""+dc.getId());
    writer.addAttribute(PARENT_ID, ""+dc.getParent().getId());
    writer.addAttribute(X, ""+dc.getX());
    writer.addAttribute(Y, ""+dc.getY());
    writer.addAttribute(WIDTH, ""+dc.getWidth());
    writer.addAttribute(HEIGHT, ""+dc.getHeight());
    writer.addAttribute(RENDER_MODE, ""+dc.getRenderMode());
    writer.addAttribute(NAME, ""+dc.getName());
    writer.addAttribute(ROTATION, ""+dc.getRotation());
    writer.addAttribute(ROTATION_X, ""+dc.getRotationX());
    writer.addAttribute(ROTATION_Y, ""+dc.getRotationY());
    writer.addAttribute(AFFINE_TRANSFORM, factory.convertAffineTransformToString(dc.getAffineTransform()));
    
    writeSpecific(dc, writer, context);
    
    writer.endNode();
	}
		
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context)
	{
		write(source, writer, context);
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
	{
		return read(reader, context);
	}

	protected Object read(HierarchicalStreamReader reader, UnmarshallingContext context)
	{
		DrawComponent dc;
		try {
			dc = getImpl().newInstance();
//			String nodeName = reader.getNodeName();
			reader.getNodeName();
			reader.moveDown();
			dc.setId(Long.parseLong(reader.getAttribute(ID)));
			dc.setX(Integer.parseInt(reader.getAttribute(X)));
			dc.setY(Integer.parseInt(reader.getAttribute(Y)));
//			dc.setWidth(Integer.parseInt(reader.getAttribute(WIDTH)));
//			dc.setHeight(Integer.parseInt(reader.getAttribute(HEIGHT)));
			dc.setRenderMode(reader.getAttribute(RENDER_MODE));
			dc.setName(reader.getAttribute(NAME));
			dc.setRotationX(Integer.parseInt(reader.getAttribute(ROTATION_X)));
			dc.setRotationY(Integer.parseInt(reader.getAttribute(ROTATION_Y)));
			dc.setRotation(Double.parseDouble(reader.getAttribute(ROTATION)));
			dc.setAffineTransform(factory.createAffineTransformFromString(reader.getAttribute(AFFINE_TRANSFORM)));
			dc.setTmpRotationX(DrawComponent.ROTATION_X_DEFAULT);
			dc.setTmpRotationY(DrawComponent.ROTATION_Y_DEFAULT);
			
			dc = readSpecific(dc, reader, context);
			
			reader.moveUp();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return dc;
	}
	
	protected abstract DrawComponent readSpecific(DrawComponent dc, HierarchicalStreamReader reader, UnmarshallingContext context);
	protected abstract void writeSpecific(DrawComponent dc, HierarchicalStreamWriter writer, MarshallingContext context);
	protected abstract String getNodeName();
	protected abstract Class<? extends DrawComponent> getImpl();
}
