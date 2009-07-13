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

import java.awt.Rectangle;
import java.awt.geom.Arc2D;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.EllipseDrawComponent;
import org.nightlabs.editor2d.j2d.GeneralShape;

public class EllipseDrawComponentImpl
extends ShapeDrawComponentImpl
implements EllipseDrawComponent
{
	private static final long serialVersionUID = 1L;
	private int startAngle = START_ANGLE_DEFAULT;
  private int endAngle = END_ANGLE_DEFAULT;
  private boolean connect = CONNECT_DEFAULT;

  public EllipseDrawComponentImpl() {
		super();
	}

  /**
   * determines if the EllipseDrawComponent should be connected to the middle or not
   * @return if the EllipseDrawComponent is connected to the middle or not
   */
  public boolean isConnect() {
		return connect;
	}
  
  /**
   * determines if the EllipseDrawComponent should be connected to the middle or not
   * @param newConnect determines if the EllipseDrawComponent should be connected to the middle or not
   */
  public void setConnect(boolean newConnect)
  {
		boolean oldConnect = connect;
		connect = newConnect;
		generalShape = createEllipse();
		generalShape.transform(getAffineTransform());
//		primSetGeneralShape(createEllipse());
//		getGeneralShape().transform(getAffineTransform());
		
		clearBounds();
		firePropertyChange(PROP_CONNECT, oldConnect, connect);
	}

	/**
	 * returns the startAngle of the EllipseDrawComponent in degrees
	 * @return the startAngle of the EllipseDrawComponent in degrees
	 */
  public int getStartAngle() {
		return startAngle;
	}
  /**
   * sets the startAngle of the EllipseDrawComponent in degrees
   * @param newStartAngle the new startAngle to set
   */
  public void setStartAngle(int newStartAngle)
  {
		int oldStartAngle = startAngle;
		startAngle = newStartAngle;
		generalShape = createEllipse();
		generalShape.transform(getAffineTransform());
//		primSetGeneralShape(createEllipse());
//		getGeneralShape().transform(getAffineTransform());
		
		clearBounds();
		firePropertyChange(PROP_START_ANGLE, oldStartAngle, startAngle);
	}

  /**
   * returns the endAngle of the EllipseDrawComponent in degrees
   * @return the endAngle of the EllipseDrawComponent in degrees
   */
  public int getEndAngle() {
		return endAngle;
	}
  /**
   * sets the endAngle of the EllipseDrawComponent in degrees
   * @param newEndAngle the new endAngle to set
   */
  public void setEndAngle(int newEndAngle) {
		int oldEndAngle = endAngle;
		endAngle = newEndAngle;
		generalShape = createEllipse();
    generalShape.transform(getAffineTransform());
//		primSetGeneralShape(createEllipse());
//    getGeneralShape().transform(getAffineTransform());
		
		clearBounds();
		firePropertyChange(PROP_END_ANGLE, oldEndAngle, endAngle);
	}

  @Override
	public String toString()
  {
		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (startAngle: ");
		result.append(startAngle);
		result.append(", endAngle: ");
		result.append(endAngle);
		result.append(", connect: ");
		result.append(connect);
		result.append(')');
		return result.toString();
	}

  @Override
	public String getTypeName() {
  	return "Ellipse";
  }
  
  protected GeneralShape createEllipse()
  {
  	Rectangle originalBounds = getOriginalShape().getBounds();
  	int type = connect ? Arc2D.PIE : Arc2D.OPEN;
    Arc2D arc = new Arc2D.Double(originalBounds.x, originalBounds.y,
    		originalBounds.width, originalBounds.height, getStartAngle(), getEndAngle()-getStartAngle(), type);
  	
    GeneralShape originalEllipse = new GeneralShape(arc);
    return originalEllipse;
  }
  
//  /**
//   * @see DrawComponent#transform(AffineTransform, boolean)
//   */
//  public void transform(AffineTransform at, boolean fromParent)
//  {
//    Rectangle oldBounds = getBounds();
//    if (generalShape != null) {
//    	super.transform(at, fromParent);
//    	affineTransform.preConcatenate(at);
//      generalShape = createEllipse();
//      generalShape.transform(getAffineTransform());
//    }
//
//    if (!fromParent && getParent() != null)
//      getParent().notifyChildTransform(this);
//
//    clearBounds();
//  }

	@Override
	protected GeneralShape initGeneralShape()
	{
		return createEllipse();
	}
  
//  public Object clone()
//  {
//  	EllipseDrawComponentImpl ellipse = (EllipseDrawComponentImpl) super.clone();
//  	return ellipse;
//  }

  /**
   * @see DrawComponent#clone(DrawComponentContainer)
   */
  @Override
	public Object clone(DrawComponentContainer parent)
  {
  	EllipseDrawComponentImpl ellipse = (EllipseDrawComponentImpl) super.clone(parent);
  	ellipse.connect = connect;
  	ellipse.endAngle = endAngle;
  	ellipse.startAngle = startAngle;
  	return ellipse;
  }
    
//	public DrawComponent clone()
//	{
//		EllipseDrawComponent ellipse = new EllipseDrawComponentImpl();
//		ellipse = (EllipseDrawComponent) assign(ellipse);
//		return ellipse;
//	}
//
//	protected DrawComponent assign(DrawComponent dc)
//	{
//		super.assign(dc);
//		if (dc instanceof EllipseDrawComponent) {
//			EllipseDrawComponent ellipse = (EllipseDrawComponent) dc;
//			ellipse.setConnect(isConnect());
//			ellipse.setEndAngle(getEndAngle());
//			ellipse.setStartAngle(getStartAngle());
//		}
//		return dc;
//	}
} //EllipseDrawComponentImpl
