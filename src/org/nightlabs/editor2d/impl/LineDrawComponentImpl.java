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

package org.nightlabs.editor2d.impl;

import java.awt.geom.PathIterator;

import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.LineDrawComponent;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.j2d.GeneralShapeUtil;


public class LineDrawComponentImpl
extends ShapeDrawComponentImpl
implements LineDrawComponent
{
	private static final long serialVersionUID = 1L;
	private boolean connect = CONNECT_DEFAULT;

	public LineDrawComponentImpl() {
		super();
	}

	public boolean isConnect() {
		return connect;
	}

	public void setConnect(boolean newConnect)
	{
		if (connect == newConnect)
			return;

		boolean oldConnect = connect;
		connect = newConnect;
		primSetConnect(newConnect);
		firePropertyChange(PROP_CONNECT, oldConnect, connect);
	}
	
//	public void primSetConnect(boolean newConnect)
//	{
//		if (connect)
//			getGeneralShape().closePath();
////		else
////			generalShape = J2DUtil.removePathSegment(getGeneralShape(), getGeneralShape().getSize()-1);
//		firePropertyChange(PROP_CONNECT, oldConnect, connect);
//	}
	
	// TODO should not manipulate originalShape, should be another method
	// for performing low level shape manipulation except transform(AffineTransform)
	protected void primSetConnect(boolean newConnect) {
		float[] coords = new float[6];
//		GeneralShape gs = getGeneralShape();
		GeneralShape gs = getOriginalShape();
		if (connect) {
			float beginX = 0;
			float beginY = 0;
			for (PathIterator pi = gs.getPathIterator(null); !pi.isDone(); pi.next()) {
				int segType = pi.currentSegment(coords);
			    switch (segType) {
			    	case (PathIterator.SEG_MOVETO):
			    		beginX = coords[0];
			    		beginY = coords[1];
			    		break;
			    }
			}
			gs.lineTo(beginX, beginY);
		}
		else {
			originalShape = GeneralShapeUtil.removePathSegment(gs, gs.getPathSegments().size()-1);
		}
		generalShape = null;
		getGeneralShape();
	}
	
	@Override
	public String toString()
	{
		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (connect: ");
		result.append(connect);
		result.append(')');
		return result.toString();
	}

	@Override
	public String getTypeName() {
		return "Line";
	}

	@Override
	public Object clone(DrawComponentContainer parent) {
		LineDrawComponentImpl line = (LineDrawComponentImpl) super.clone(parent);
		line.connect = connect;
		return line;
	}

//	@Override
//	public void transform(AffineTransform at, boolean fromParent) {
//		super.transform(at, fromParent);
//		primSetConnect(connect);
//	}
	
} //LineDrawComponentImpl
