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
import org.nightlabs.editor2d.IConnectable;
import org.nightlabs.editor2d.LineDrawComponent;
import org.nightlabs.editor2d.impl.LineDrawComponentImpl;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class LineDrawComponentConverter
extends ShapeDrawComponentConverter
{
	public static final String CONNECT = IConnectable.PROP_CONNECT;
	
	public LineDrawComponentConverter() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class type)
	{
		if (type.equals(LineDrawComponentImpl.class)) {
			return true;
		}
		return false;
	}

	@Override
	protected void readAdditional(DrawComponent dc, HierarchicalStreamReader reader, UnmarshallingContext context)
	{
		LineDrawComponent ldc = (LineDrawComponent) dc;
		ldc.setConnect(Boolean.parseBoolean(reader.getAttribute(CONNECT)));
	}

	@Override
	protected void writeAdditional(DrawComponent dc, HierarchicalStreamWriter writer, MarshallingContext context)
	{
		LineDrawComponent ldc = (LineDrawComponent) dc;
    writer.addAttribute(CONNECT, ""+ldc.isConnect());
	}

	@Override
	public Class<? extends DrawComponent> getImpl() {
		return LineDrawComponentImpl.class;
	}
	
	@Override
	protected String getNodeName() {
		return "LineDrawComponent";
	}
}
