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
import org.nightlabs.editor2d.IFillable;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent.LineStyle;
import org.nightlabs.editor2d.impl.Editor2DFactoryImpl;
import org.nightlabs.util.ColorUtil;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public abstract class ShapeDrawComponentConverter
//implements Converter
extends DrawComponentConverter
{
	public ShapeDrawComponentConverter() {
		super();
		factory = new Editor2DFactoryImpl();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class type)
	{
		if (ShapeDrawComponent.class.isAssignableFrom(type)) {
			return true;
		}
		return false;
	}

	public static final String GENERAL_SHAPE = ShapeDrawComponent.PROP_GENERAL_SHAPE;
	public static final String FILL = IFillable.PROP_FILL;
	public static final String FILL_COLOR = ShapeDrawComponent.PROP_FILL_COLOR;
	public static final String LINE_COLOR = ShapeDrawComponent.PROP_LINE_COLOR;
	public static final String LINE_STYLE = ShapeDrawComponent.PROP_LINE_STYLE;
	public static final String LINE_WIDTH = ShapeDrawComponent.PROP_LINE_WIDTH;
	
	protected Editor2DFactoryImpl factory;
	
	@Override
	protected String getNodeName() {
		return "ShapeDrawComponent";
	}
	
	@Override
	protected DrawComponent readSpecific(DrawComponent dc, HierarchicalStreamReader reader, UnmarshallingContext context)
	{
		ShapeDrawComponent sdc = (ShapeDrawComponent) dc;
		
		// GeneralShape
//		reader.moveDown();
//		String generalShape = reader.getValue();
//		sdc.setGeneralShape(factory.createGeneralShapeFromString(generalShape));
//		reader.moveUp();
		sdc.setGeneralShape(factory.createGeneralShapeFromString(reader.getAttribute(GENERAL_SHAPE)));
		
		// Fill
		sdc.setFill(Boolean.parseBoolean(reader.getAttribute(FILL)));
		
		// Fill Color
//		reader.moveDown();
//		String fillColor = reader.getValue();
//		sdc.setFillColor(ColorUtil.stringToColor(fillColor));
//		reader.moveUp();
		sdc.setFillColor(ColorUtil.stringToColor(reader.getAttribute(FILL_COLOR)));
		
		// Line Color
//		reader.moveDown();
//		String lineColor = reader.getValue();
//		sdc.setLineColor(ColorUtil.stringToColor(lineColor));
//		reader.moveUp();
		sdc.setLineColor(ColorUtil.stringToColor(reader.getAttribute(LINE_COLOR)));
				
		sdc.setLineWidth(Integer.parseInt(reader.getAttribute(LINE_WIDTH)));
//		sdc.setLineStyle(Integer.parseInt(reader.getAttribute(LINE_STYLE)));
		sdc.setLineStyle((LineStyle.valueOf(reader.getAttribute(LINE_STYLE))));
		
		readAdditional(dc, reader, context);
		
		return sdc;
	}

	/**
	 * SubClasses should override this Method to get Additional Data
	 * 
	 * @param dc the DrawComponent to read
	 * @param reader the reader to read
	 * @param context the context for the reader
	 */
	protected void readAdditional(DrawComponent dc, HierarchicalStreamReader reader, UnmarshallingContext context)
	{
 
	}
	
	@Override
	protected void writeSpecific(DrawComponent dc, HierarchicalStreamWriter writer, MarshallingContext context)
	{
		ShapeDrawComponent sdc = (ShapeDrawComponent) dc;
		
//    writer.startNode(GENERAL_SHAPE);
//    writer.setValue(factory.convertGeneralShapeToString(sdc.getGeneralShape()));
//    writer.endNode();
		writer.addAttribute(GENERAL_SHAPE, factory.convertGeneralShapeToString(sdc.getGeneralShape()));
    writer.addAttribute(FILL, ""+sdc.isFill());
    
//    writer.startNode(FILL_COLOR);
//    writer.setValue(ColorUtil.colorToString(sdc.getFillColor()));
//    writer.endNode();
    writer.addAttribute(FILL_COLOR, ColorUtil.colorToString(sdc.getFillColor()));
    
//    writer.startNode(LINE_COLOR);
//    writer.setValue(ColorUtil.colorToString(sdc.getLineColor()));
//    writer.endNode();
    writer.addAttribute(LINE_COLOR, ColorUtil.colorToString(sdc.getLineColor()));
    
    writer.addAttribute(LINE_WIDTH, ""+sdc.getLineWidth());
    writer.addAttribute(LINE_STYLE, ""+sdc.getLineStyle());
    
    writeAdditional(dc, writer, context);
	}

	/**
	 * SubClasses should override this Method to write Additional Data
	 * 
	 * @param dc the DrawComponent to write
	 * @param writer the writer to write
	 * @param context the context for the writer
	 */
	protected void writeAdditional(DrawComponent dc, HierarchicalStreamWriter writer, MarshallingContext context)
	{
		
	}
}
