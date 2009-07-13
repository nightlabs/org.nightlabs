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
import org.nightlabs.editor2d.EllipseDrawComponent;
import org.nightlabs.editor2d.IConnectable;
import org.nightlabs.editor2d.impl.EllipseDrawComponentImpl;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class EllipseDrawComponentConverter
extends ShapeDrawComponentConverter
{
	public static final String CONNECT = IConnectable.PROP_CONNECT;
	public static final String START_ANGLE = EllipseDrawComponent.PROP_START_ANGLE;
	public static final String END_ANGLE = EllipseDrawComponent.PROP_END_ANGLE;
		
	public EllipseDrawComponentConverter() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class type)
	{
		if (type.equals(EllipseDrawComponentImpl.class)) {
			return true;
		}
		return false;
	}
	
	@Override
	protected void readAdditional(DrawComponent dc, HierarchicalStreamReader reader, UnmarshallingContext context)
	{
		EllipseDrawComponent edc = (EllipseDrawComponent) dc;
		edc.setConnect(Boolean.parseBoolean(reader.getAttribute(CONNECT)));
		edc.setStartAngle(Integer.parseInt(reader.getAttribute(START_ANGLE)));
		edc.setEndAngle(Integer.parseInt(reader.getAttribute(END_ANGLE)));
	}
	
	@Override
	protected void writeAdditional(DrawComponent dc, HierarchicalStreamWriter writer, MarshallingContext context)
	{
		EllipseDrawComponent edc = (EllipseDrawComponent) dc;
    writer.addAttribute(CONNECT, ""+edc.isConnect());
    writer.addAttribute(START_ANGLE, ""+edc.getStartAngle());
    writer.addAttribute(END_ANGLE, ""+edc.getEndAngle());
  }
	
	@Override
	protected String getNodeName() {
		return "EllipseDrawComponent";
	}
	
	@Override
	public Class<? extends DrawComponent> getImpl() {
		return EllipseDrawComponentImpl.class;
	}
}
