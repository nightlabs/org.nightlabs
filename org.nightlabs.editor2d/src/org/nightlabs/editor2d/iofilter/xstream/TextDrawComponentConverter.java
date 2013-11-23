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
import org.nightlabs.editor2d.TextDrawComponent;
import org.nightlabs.editor2d.impl.TextDrawComponentImpl;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class TextDrawComponentConverter
extends ShapeDrawComponentConverter
{
	public static final String BOLD = TextDrawComponent.PROP_BOLD;
	public static final String ITALIC = TextDrawComponent.PROP_ITALIC;
	public static final String FONT_NAME = TextDrawComponent.PROP_FONT_NAME;
	public static final String FONT_SIZE = TextDrawComponent.PROP_FONT_SIZE;
	public static final String TEXT = TextDrawComponent.PROP_TEXT;
	
	public TextDrawComponentConverter() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class type)
	{
		if (type.equals(TextDrawComponentImpl.class)) {
			return true;
		}
		return false;
	}
	
	@Override
	protected void readAdditional(DrawComponent dc, HierarchicalStreamReader reader, UnmarshallingContext context)
	{
		TextDrawComponent tdc = (TextDrawComponent) dc;
		tdc.setText(reader.getAttribute(TEXT));
		tdc.setFontName(reader.getAttribute(FONT_NAME));
		tdc.setFontSize(Integer.parseInt(reader.getAttribute(FONT_SIZE)));
		tdc.setBold(Boolean.parseBoolean(reader.getAttribute(BOLD)));
		tdc.setItalic(Boolean.parseBoolean(reader.getAttribute(ITALIC)));
	}
	
	@Override
	protected void writeAdditional(DrawComponent dc, HierarchicalStreamWriter writer, MarshallingContext context)
	{
		TextDrawComponent tdc = (TextDrawComponent) dc;
    writer.addAttribute(TEXT, ""+tdc.getText());
    writer.addAttribute(FONT_NAME, ""+tdc.getFontName());
    writer.addAttribute(FONT_SIZE, ""+tdc.getFontSize());
    writer.addAttribute(BOLD, ""+tdc.isBold());
    writer.addAttribute(ITALIC, ""+tdc.isItalic());
	}
	
	@Override
	protected String getNodeName() {
		return "TextDrawComponent";
	}
		
	@Override
	public Class<? extends DrawComponent> getImpl() {
		return TextDrawComponentImpl.class;
	}
}
